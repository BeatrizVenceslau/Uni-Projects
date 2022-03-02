#include "packet-format.h"
#include <arpa/inet.h>
#include <limits.h>
#include <netinet/in.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>

int main(int argc, char *argv[]) {
  char *file_name = argv[1];
  int port = atoi(argv[2]);
  int rw_size = atoi(argv[3]);  //define the receive window size

  FILE *file = fopen(file_name, "w");
  if (!file) {
    perror("fopen");
    exit(EXIT_FAILURE);
  }

  // Check window size is valid
  if (rw_size < 1 || rw_size > MAX_WINDOW_SIZE) {
    perror("receive window size");
    exit(EXIT_FAILURE);
  }

  // Prepare server socket.
  int sockfd = socket(AF_INET, SOCK_DGRAM, 0);
  if (sockfd == -1) {
    perror("socket");
    exit(EXIT_FAILURE);
  }

  // Allow address reuse so we can rebind to the same port,
  // after restarting the server.
  if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &(int){1}, sizeof(int)) <
      0) {
    perror("setsockopt");
    exit(EXIT_FAILURE);
  }

  struct sockaddr_in srv_addr = {
      .sin_family = AF_INET,
      .sin_addr.s_addr = htonl(INADDR_ANY),
      .sin_port = htons(port),
  };
  if (bind(sockfd, (struct sockaddr *)&srv_addr, sizeof(srv_addr))) {
    perror("bind");
    exit(EXIT_FAILURE);
  }
  fprintf(stderr, "Receiving on port: %d\n", port);

  ssize_t len;
  uint32_t seq_num = 0; // Sequence number of the next segment expected by the receiver - base of receive window
  uint32_t acks_vector = 0; // Vector inicialized at zero, starts at window_base+1,
                            // each position represents if the segment has been received
  data_pkt_t data_pkt;
  ack_pkt_t ack_pkt;
  uint32_t seq_num_pkt;
  int rcv_resend = 0;
  uint32_t last_chunk = -1;  // Seq_num of the last chunk

  // Set timeout for loss packets coming from sender
  struct timeval tv;
  tv.tv_sec = RECEIVER_INCOMPLETE_TIMEOUT/1000;
  tv.tv_usec = 0;
  setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

  do { // Iterate over segments, until last the segment is detected.
    // Receive segment.
    struct sockaddr_in src_addr;
    if (rcv_resend == 0) {
      len =
          recvfrom(sockfd, &data_pkt, sizeof(data_pkt), 0,
                  (struct sockaddr *)&src_addr, &(socklen_t){sizeof(src_addr)});
      if (len < 0) {
        printf("Error: Receiver has not received complete file.\n");
        exit(EXIT_FAILURE);
      }
      printf("Received segment %d, size %ld.\n", ntohl(data_pkt.seq_num), len);

      seq_num_pkt = ntohl(data_pkt.seq_num);  // The seq_num of the packet received

      if (len < 1004) { //1000 of data and 4 of offset of the seq_num
        last_chunk = seq_num_pkt;
      }
    }

    // If the packet is inside the current receive window
    if (seq_num_pkt >= seq_num  &&  seq_num_pkt < (seq_num + rw_size)) {
      fseek(file, seq_num_pkt*1000, SEEK_SET);
      // Write data to file.
      fwrite(data_pkt.data, 1, len - offsetof(data_pkt_t, data), file);
      // If packet is base of receive window acks vector does not change
      if (seq_num_pkt != seq_num) {
        // Set the corresponding but in the selective_acks vector to 1
        acks_vector = acks_vector | 1 << (seq_num_pkt - seq_num - 1);
      }
    }

    // If the packet is the base of the receive window
    if (seq_num_pkt == seq_num) {
      // While the packets in the new base position have been received (have bit 1)
      while (acks_vector & 1) {
        acks_vector = acks_vector >> 1;
        seq_num++;
      }
      seq_num++;
    }

    rcv_resend = 0;

    // Sends ack of the packet to sender
    ack_pkt.seq_num = htonl(seq_num); // The seq_num of the ack is always the base of the receive window
    ack_pkt.selective_acks = htonl(acks_vector); 

    sendto(sockfd, &ack_pkt, sizeof(ack_pkt), 0,
            (struct sockaddr *)&src_addr, sizeof(src_addr));
    printf("Sent ack %d, selective_acks %08x.\n", ntohl(ack_pkt.seq_num), acks_vector);

    if (last_chunk >= 0 && seq_num > last_chunk) {
      // Have received the complete file

      // Set timeout for end of file
      tv.tv_sec = RECEIVER_COMPLETE_TIMEOUT/1000;
      tv.tv_usec = 0;
      setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

      len =
          recvfrom(sockfd, &data_pkt, sizeof(data_pkt), 0,
                  (struct sockaddr *)&src_addr, &(socklen_t){sizeof(src_addr)});

      if (len != -1) {
        // Received a packet - The sender had a problem and resent packet
        rcv_resend = 1; 
        seq_num_pkt = ntohl(data_pkt.seq_num);  // The seq_num of the packet received

      } else {
        // There was a timeout and has received complete file
        printf("Received complete file.\n");
        break;
      }
    }
    
  } while (seq_num <= last_chunk || rcv_resend != 0 || len == sizeof(data_pkt_t));

  // Clean up and exit.
  close(sockfd);
  fclose(file);

  return EXIT_SUCCESS;
}
