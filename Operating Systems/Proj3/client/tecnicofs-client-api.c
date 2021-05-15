#include "tecnicofs-client-api.h"
#include "../server/lib/unix.h"
#include "../server/lib/constants.h"
#include <unistd.h>
#include <stdlib.h>


int sockfd = -1;
int servlen;
struct sockaddr_un serv_addr;

void err_dump(char* errorMessage) {
    perror(errorMessage);
    exit(0);
}

int checkConnection() {
    if (sockfd < 0) 
        return TECNICOFS_ERROR_NO_OPEN_SESSION;
    return 0;
}

int readOutp() {
    int n;
    char line[4];
    bzero(line, 4);
    n = read(sockfd, line, 4);
    if(n <= 0)
        err_dump("readOutp:read error on socket");
    return atoi(line);
}

int tfsCreate(char *filename, permission ownerPermissions, permission othersPermissions){
    if(checkConnection() < 0)
        return checkConnection();
    int n = dprintf(sockfd, "c %s %d%d", filename, ownerPermissions, othersPermissions);
    if (n <= 0)
        err_dump("tfsCreate:write error on socket");
    return readOutp();
}

int tfsDelete(char *filename){
    if(checkConnection() < 0)
        return checkConnection();
    int n = dprintf(sockfd, "d %s", filename);
    if(n <= 0)
        err_dump("tfsDelete:write error on socket");
    return readOutp();
}

int tfsRename(char *filenameOld, char *filenameNew){
    if(checkConnection() < 0)
        return checkConnection();
    int n = dprintf(sockfd, "r %s %s", filenameOld, filenameNew);
    if (n <= 0)
        err_dump("tfsRename:write error on socket");
    return readOutp();
}

int tfsOpen(char *filename, permission mode){
    if(checkConnection() < 0)
        return checkConnection();
    int n = dprintf(sockfd, "o %s %d", filename, mode);
    if (n <= 0)
        err_dump("tfsOpen:write error on socket");
    return readOutp();
}

int tfsClose(int fd){
    if(checkConnection() < 0)
        return checkConnection();
    int n = dprintf(sockfd, "x %d", fd);
    if (n <= 0)
        err_dump("tfsClose:write error on socket");
    return readOutp();
}

int tfsRead(int fd, char *buffer, int len){
    if(checkConnection() < 0)
        return checkConnection();
    int n = dprintf(sockfd, "l %d %d", fd, len);
    int t = len + 3;
    int erro;
    char bufcpy[len];
    char buf[t];
    bzero(buf, t);
    bzero(bufcpy, len);
    if (n <= 0)
        err_dump("tfsRead:write error on socket");
    
    n = read(sockfd, buf, t);
    sscanf(buf, "%d %s", &erro, bufcpy);
    if(erro < 0) {
        return erro;
    }
    else {
        strcpy(buffer, bufcpy);
        return strlen(bufcpy);
    }
}

int tfsWrite(int fd, char *buffer, int len){
    if(checkConnection() < 0)
        return checkConnection();
    int n = dprintf(sockfd, "w %d %s", fd, buffer);
    if (n <= 0)
        err_dump("tfsWrite:write error on socket");
    return readOutp();
}



int tfsMount(char * address){
    if ((sockfd = socket(AF_UNIX, SOCK_STREAM, 0)) < 0)
        return TECNICOFS_ERROR_OPEN_SESSION;
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sun_family = AF_UNIX;
    strcpy(serv_addr.sun_path, address);
    servlen = strlen(serv_addr.sun_path) + sizeof(serv_addr.sun_family);
    if(connect(sockfd, (struct sockaddr*) &serv_addr, servlen) < 0)
        return TECNICOFS_ERROR_CONNECTION_ERROR;
    return 0;
}

int tfsUnmount(){
    close(sockfd);
    exit(0);
}
