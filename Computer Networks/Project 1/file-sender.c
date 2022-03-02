#include "packet-format.h"
#include <limits.h>
#include <netdb.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main(int argc, char *argv[]) {
  char *file_name = argv[1];
  char *host = argv[2];
  int port = atoi(argv[3]);
  int sw_size = atoi(argv[4]);  //define the send window size

  FILE *file = fopen(file_name, "r");
  if (!file) {
    perror("fopen");
    exit(EXIT_FAILURE);
  }

  // Check window size is valid
  if (sw_size < 1 || sw_size > MAX_WINDOW_SIZE) {
    perror("send window size");
    exit(EXIT_FAILURE);
  }

  // Prepare server host address.
  struct hostent *he;
  if (!(he = gethostbyname(host))) {
    perror("gethostbyname");
    exit(EXIT_FAILURE);
  }

  struct sockaddr_in srv_addr = {
      .sin_family = AF_INET,
      .sin_port = htons(port),
      .sin_addr = *((struct in_addr *)he->h_addr),
  };

  int sockfd = socket(AF_INET, SOCK_DGRAM, 0);
  if (sockfd == -1) {
    perror("socket");
    exit(EXIT_FAILURE);
  }

  data_pkt_t data_pkt;
  ack_pkt_t ack_pkt;
  size_t data_len;
  uint32_t last_chunk = -1;   // Seq_num of the last chunk

  uint32_t seq_num = 0;       // Represents the base of the send window
  uint32_t acks_vector = 0;   // Vector inicialized at zero, starts at window_base,
                              // each position represents if the segment has been acknowledged
  uint32_t to_send = 0;       // Indicates the seq_num of the packet that is waiting to be sent
  
  int timeout = 0;            // Timeout counter
  struct timeval tv;
  tv.tv_sec = RETRANSMIT_TIMEOUT/1000;
  tv.tv_usec = 0;
  setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

  // Send packets
  do { // Generate segments from file, until the the end of the file.
    // While it has not reachead the end of the send window, sends packets
    while (to_send < seq_num + sw_size) {
      // If the segment has not been acked yet
      // Prepare data segment.
      data_pkt.seq_num = htonl(to_send);

      // Load data from file.
      fseek(file, (to_send)*1000, SEEK_SET);
      data_len = fread(data_pkt.data, 1, sizeof(data_pkt.data), file);
      
      if ((acks_vector & 1) != 1 && (data_len != 0 || last_chunk == -1)) {
        // Send segment.
        ssize_t sent_len =
            sendto(sockfd, &data_pkt, offsetof(data_pkt_t, data) + data_len, 0,
                  (struct sockaddr *)&srv_addr, sizeof(srv_addr));
        printf("Sending segment %d, size %ld.\n", ntohl(data_pkt.seq_num),
              offsetof(data_pkt_t, data) + data_len);
        
        if (sent_len < 1004) { //1000 of data and 4 of offset of the seq_num
          last_chunk = to_send;
        }

        if (sent_len != offsetof(data_pkt_t, data) + data_len) {
          fprintf(stderr, "Truncated packet.\n");
          exit(EXIT_FAILURE);
        }
      }
      to_send++;
      acks_vector = acks_vector >> 1;
    }

    // Expects acknowledgement
    ssize_t ack_len = 
        recvfrom(sockfd, &ack_pkt, sizeof(ack_pkt), 0,
                (struct sockaddr *)&srv_addr, &(socklen_t){sizeof(srv_addr)});
    
    // Ckeck it receives acknowledgement
    if (ack_len > 0) {
      printf("Received ack %d, selective_acks %d.\n", ntohl(ack_pkt.seq_num), ntohl(ack_pkt.selective_acks));

      uint32_t ack_seq_num = ntohl(ack_pkt.seq_num);
      uint32_t ack_selective_acks = ntohl(ack_pkt.selective_acks);

      // Check that the packet acknowledged is inside window
      if (ack_seq_num >= seq_num &&  ack_seq_num <= (seq_num+1 + sw_size)) {
        // The selective acks of the sender are the same as the receivers
        acks_vector = ack_selective_acks << 1;

        // If the packet acked is the base of the window (seqs are !=)
        if (ack_seq_num > seq_num) {
          // Move window to expected packet at receiver
          seq_num = ack_seq_num;
        }
        timeout = 0;
      }
    }
    // Timeout (res == -1)
    else {
      timeout++;
      to_send = seq_num;  // Returns the to_send packet to the base of the send window, to resend any not acked
      last_chunk = -1; // Resets last_chunk to -1 to ensure it is sent 

      if (timeout == MAX_RETRIES) { // MAX_RETRIES = 3
        printf("Error: Exeeded 3 timeouts.\n");
        exit(EXIT_FAILURE);
      }
      printf("Error: Timeout number %d occured.\n", timeout);
    }
    
    // If the base of the window is not after the last_chunk
    // OR If it has not reached the end of the file
  } while (seq_num != last_chunk+1 || !(feof(file) && data_len < sizeof(data_pkt.data)));

  // Clean up and exit.
  close(sockfd);
  fclose(file);

  return EXIT_SUCCESS;
}
