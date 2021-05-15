/* Sistemas Operativos, DEI/IST/ULisboa 2019-20
   Beatriz Venceslau ist193734
   Carolina Ramos    ist193694                  */

#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include "lib/fs.h"
#include "lib/constants.h"
#include "lib/timer.h"
#include "lib/inodes.h"
#include "lib/sync.h"
#include "lib/hash.h"
#include "semaphore.h"
#include "lib/unix.h"
#include "signal.h"

char* nomeSocket;
char* global_outputFile = NULL;
int numBuckets = 0, m = 0;
tecnicofs* fs;
int sockfd, newsockfd, servlen;
unsigned int clilen;
struct sockaddr_un cli_addr, serv_addr;
TIMER_T startTime, stopTime;
pthread_t* worker;
FILE * outputFp;
FILE* timeFp;

static void displayUsage (const char* appName){
    printf("Usage: %s nomeSocket output_filepath numbuckets\n", appName);
    exit(EXIT_FAILURE);
}

static void parseArgs (long argc, char* const argv[]){
    if (argc != 4) {
        fprintf(stderr, "Invalid format:\n");
        displayUsage(argv[0]);
    }

    nomeSocket = argv[1];
    global_outputFile = argv[2];
    numBuckets = atoi(argv[3]);
    if (numBuckets < 0) {
        fprintf(stderr, "Invalid number of Buckets\n");
        displayUsage(argv[0]);
    }
}

void errorParse(int lineNumber){
    fprintf(stderr, "Error: line %d invalid\n", lineNumber);
    exit(EXIT_FAILURE);
}

void err_dump(char* errorMessage) {
    perror(errorMessage);
    exit(EXIT_FAILURE);
}

FILE * openOutputFile() {
    FILE *fp;
    fp = fopen(global_outputFile, "w");
    if (fp == NULL) {
        perror("Error opening output file");
        exit(EXIT_FAILURE);
    }
    return fp;
}

void checkWrite(int n) {
    if ( n < 0 ) 
        err_dump("Write couldn't connect.");
}

void* applyCommands(char command[MAX_INPUT_SIZE], client* cliente){
        char token;
        char name[MAX_INPUT_SIZE];
        char permissions[MAX_INPUT_SIZE];
        char buffer[MAX_INPUT_SIZE];
        char buf[103];   
        char filenameNew[MAX_INPUT_SIZE];
        int fd, len;  
        int perm, n, erro;
        int mode, mi = 0;
        int fd1, erro1 = 0;
        permission ownerPermissions;        
        permission othersPermissions;
        uid_t owner;

        sscanf(command, "%c", &token);
        
        int iNumber;
        switch (token) {
            case 'c':
                sscanf(command, "%c %s %s", &token, name, permissions);
                perm = atoi(permissions);
                ownerPermissions = perm / 10;
                othersPermissions = perm % 10;
                
                if((ownerPermissions <=3 && ownerPermissions >= 0) || (othersPermissions <= 3 && othersPermissions >= 0)) {
                    n = lookup(fs, name);
                    if(n >= 0) {
                        erro = write(cliente->socket, "-4", 3);
                        checkWrite(erro);
                    }
                    else {
                        iNumber = inode_create(cliente->uid, ownerPermissions, othersPermissions);
                        create(fs, name, iNumber);
                        erro = write(cliente->socket, "0", 3);
                        checkWrite(erro);
                    }
                }
                break;
            case 'l':
                sscanf(command, "%c %d %d", &token, &fd, &len);
                bzero(buffer, MAX_INPUT_SIZE);
                bzero(buf, 103);

                for (int i = 0; i < MAX_OPEN_FILES; i++) {
                    if(cliente -> openFile[i].fd == fd) {
                        inode_get(fd, NULL, &ownerPermissions, &othersPermissions, buffer, len);
                        if(cliente->openFile[i].mode == RW || cliente->openFile[i].mode == READ) {
                                sprintf(buf, "0 %s", buffer);
                                erro = write(cliente->socket, buf, strlen(buf));
                                checkWrite(erro);
                                break;
                        }
                        else{
                            erro = write(cliente->socket, "-10", 3);
                            checkWrite(erro);
                            break;
                        }
                    }
                    else{
                       erro = write(cliente->socket, "-8", 3);
                       checkWrite(erro);
                       break;
                    } 
                }
                break;
            case 'w':
                bzero(buffer, strlen(buffer));
                sscanf(command, "%c %d %s", &token, &fd1, buffer);
                for (int i = 0; i < MAX_OPEN_FILES; i++) {
                    if(cliente -> openFile[i].fd == fd1) {
                        if(cliente->openFile[i].mode == RW || cliente->openFile[i].mode == WRITE) {
                            inode_set(fd1, buffer, strlen(buffer));
                            erro = write(cliente->socket, "0", 3);
                            checkWrite(erro);
                            erro1 = -1;
                        }
                        else {
                            erro = write(cliente->socket, "-10", 4);
                            checkWrite(erro);
                            erro1 = -1;
                        }
                        break;
                    }
                }
                if (erro1 == 0) {
                    erro = write(cliente->socket, "-8", 3); 
                    checkWrite(erro);
                }
                break;
            case 'r':
                sscanf(command, "%c %s %s", &token, name, filenameNew);
                if (lookup(fs, filenameNew) >= 0){
                    erro = write(cliente->socket, "-4", 3);
                    checkWrite(erro);
                }
                else if (lookup(fs, name) < 0) { 
                    erro = write(cliente->socket, "-5", 3);
                    checkWrite(erro);
                }
                else {
                    iNumber = lookup(fs, name);
                    inode_get(iNumber, &owner, NULL, NULL, NULL, 0);
                    if(cliente->uid == owner) {
                        reName(fs, name, filenameNew, numBuckets);
                        erro = write(cliente->socket, "0", 3);
                        checkWrite(erro);
                    }
                }
                break;
            case 'o':
                sscanf(command, "%c %s %d", &token, name, &mode);
                iNumber = lookup(fs, name);
                if(iNumber < 0) {
                    erro = write(cliente->socket, "-5", 3);
                    checkWrite(erro);
                }
                else {
                    inode_get(iNumber, &owner, &ownerPermissions, &othersPermissions, NULL, 0);                    
                    switch (mode) {
                        case RW:
                            if((cliente->uid==owner && ownerPermissions > 0) || (cliente->uid !=owner && othersPermissions > 0)) {
                                for (int i=0; i<MAX_OPEN_FILES; i++){
                                    if(cliente->openFile[i].fd < 0){
                                        cliente->openFile[i].mode = mode;
                                        cliente->openFile[i].fd = iNumber;
                                        char* msg = malloc(5);
                                        bzero(msg, 5);
                                        sprintf(msg, "%d", iNumber);
                                        erro = write(cliente->socket, msg, 4);
                                        checkWrite(erro);
                                        mi = -1;
                                        break;
                                    }
                                }
                                if(mi == 0){
                                    erro = write(cliente->socket, "-7", 3);
                                    checkWrite(erro);
                                    break;
                                }
                            }
                            break;
                        case READ:
                            if((cliente->uid==owner && (ownerPermissions == READ || ownerPermissions == RW)) || (cliente->uid !=owner && (othersPermissions == READ || othersPermissions == RW))) {
                                for (int i=0; i<MAX_OPEN_FILES; i++){
                                    if(cliente->openFile[i].fd < 0){
                                        cliente->openFile[i].mode = mode;
                                        cliente->openFile[i].fd = iNumber;
                                        char* msg = malloc(5);
                                        bzero(msg, 5);
                                        sprintf(msg, "%d", iNumber);
                                        erro = write(cliente->socket, msg, 4);
                                        checkWrite(erro);
                                        mi = -1;
                                        break;
                                    }
                                }
                                if(mi == 0){
                                    erro = write(cliente->socket, "-7", 3);
                                    checkWrite(erro);
                                    break;
                                }
                            }
                            break;
                        case WRITE:
                            if((cliente->uid==owner && (ownerPermissions == WRITE || ownerPermissions == RW)) || (cliente->uid !=owner && (othersPermissions == WRITE || othersPermissions == RW))) {
                                for (int i=0; i<MAX_OPEN_FILES; i++){
                                    if(cliente->openFile[i].fd < 0){
                                        cliente->openFile[i].mode = mode;
                                        cliente->openFile[i].fd = iNumber;
                                        char* msg = malloc(5);
                                        bzero(msg, 5);
                                        sprintf(msg, "%d", iNumber);
                                        erro = write(cliente->socket, msg, 4);
                                        checkWrite(erro);
                                        mi = -1;
                                        break;
                                    }
                                }
                                if(mi == 0){
                                    erro = write(cliente->socket, "-7", 3);
                                    checkWrite(erro);
                                    break;
                                }
                            }
                            break;
                        }
                    if(mi == 0) {
                        erro = write(cliente->socket, "-6", 3);
                        checkWrite(erro);
                        break;
                    }        
                }
                break;    
            case 'd':
                sscanf(command, "%c %s", &token, name);
                iNumber = lookup(fs, name);
                if(iNumber < 0) {
                    erro = write(cliente->socket, "-5", 3);
                    checkWrite(erro);
                }
                else {
                    inode_get(iNumber, &owner, &ownerPermissions, &othersPermissions, NULL, 0);
                    int n = 0;
                    if(cliente->uid == owner){
                        for(int i=0; i<MAX_OPEN_FILES; i++){
                            if(cliente->openFile[i].fd == iNumber){
                                erro = write(cliente->socket, "-9", 3);
                                checkWrite(erro);
                                n = 1;
                                break;
                            }
                        }
                        if(n == 0) {
                            delete(fs, name);
                            inode_delete(iNumber);
                            erro = write(cliente->socket, "0", 3);
                            checkWrite(erro);
                        }
                    }
                    else{
                        erro = write(cliente->socket, "-6", 3);
                        checkWrite(erro);
                    }        
                }
                break;
            case 'x':
                sscanf(command, "%c %d", &token, &fd);
                int x = 0;
                iNumber = lookup(fs, name);
                if(iNumber >= 0) {
                    for (int i=0; i<5; i++){
                        if(cliente->openFile[i].fd == iNumber){
                            cliente->openFile[i].mode=-1;
                            cliente->openFile[i].fd=-1;
                            write(cliente->socket, "0", 2);
                            x = -1;
                            break;
                        }
                    }
                    if(x==0){
                        write(cliente->socket, "-7", 3);
                        break;
                    }       
                }
                else{
                    write(cliente->socket, "-5", 3);
                    break;
                }
                break;
            default: { /* error */
                fprintf(stderr, "Error: commands to apply\n");
                exit(EXIT_FAILURE);
            } 
        }
    return NULL;
}

void* processCommand(void* tmp){
    client *cliente = (client*)tmp;
    int n;
    char line[MAX_INPUT_SIZE];
    while(1) {
        bzero(line, MAX_INPUT_SIZE);
        n = read(cliente->socket , line, MAX_INPUT_SIZE);
        if (n > 0) {
            applyCommands(line, cliente);
        }
        else
            return NULL; 
    }
}


void runThreads(){
    struct ucred ucred;
    unsigned int len;
    client* cliente;
    worker = (pthread_t*) malloc(sizeof(pthread_t));

    TIMER_READ(startTime);
    /* Cria socket stream */
    if ((sockfd = socket(AF_UNIX, SOCK_STREAM, 0)) < 0)
        err_dump("server: can't open stream socket");

    /*Elimina o nome, para o caso de já existir.*/
    unlink(nomeSocket);

    /* O nome serve para que os clientes possam identificar o servidor*/
    bzero((char *)&serv_addr, sizeof(serv_addr));
    serv_addr.sun_family = AF_UNIX;
    strcpy(serv_addr.sun_path, nomeSocket);
    servlen = strlen(serv_addr.sun_path) + sizeof(serv_addr.sun_family);
    
    if (bind(sockfd, (struct sockaddr*) &serv_addr, servlen) < 0)
        err_dump("server, can't bind local address");

    listen(sockfd, 5);

    clilen = sizeof(cli_addr);

    for(int i = 0; i<MAX_CLIENTS; i++){
        newsockfd = accept(sockfd,(struct sockaddr*)&cli_addr, &clilen);
        if(newsockfd < 0)
            err_dump("server:accepterror");
        len= sizeof(struct ucred);  
        if(getsockopt(newsockfd, SOL_SOCKET, SO_PEERCRED, &ucred, &len) == -1) {
            err_dump("can't get information");
        }
        cliente = (client*) malloc(sizeof(client));
        cliente -> uid = ucred.uid;
        cliente -> socket = newsockfd;
        for(int i = 0; i < MAX_OPEN_FILES; i++) {
            cliente->openFile[i].fd = -1;
            cliente->openFile[i].mode = -1;
        }
        
        if (pthread_create(&worker[i], NULL, processCommand, (void*)cliente) != 0){
            perror("Can't create thread");
            exit(EXIT_FAILURE);
        }
        m = i;
    }
}

void verificaC () {

    for(int i = 0; i < m; i++) {
        if(pthread_join(worker[i], NULL) != 0)
            perror("Can't join thread");
    }
    close(sockfd);
       
    TIMER_READ(stopTime);
    fprintf(timeFp, "TecnicoFS completed in %0.4f seconds.\n", TIMER_DIFF_SECONDS(startTime, stopTime));
    free(worker);
    print_tecnicofs_tree(outputFp, fs);
    fflush(outputFp);
    fclose(outputFp);

    free_tecnicofs(fs);
    exit(EXIT_SUCCESS);

}

int main(int argc, char* argv[]) {
    parseArgs(argc, argv);
    outputFp = openOutputFile();
    inode_table_init();
    fs = new_tecnicofs(numBuckets);

    timeFp = stdout;
    signal(SIGINT, verificaC);
    runThreads();
    exit(EXIT_SUCCESS);
}
