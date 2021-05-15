/*Beatriz Venceslau ist193734
  Carolina Ramos    ist193694*/

#include <stdio.h>
#include <stdlib.h>
#include <getopt.h>
#include <string.h>
#include <ctype.h>
#include <pthread.h>
#include <time.h>
#include <sys/time.h>
#include "fs.h"

#define MAX_COMMANDS 150000     /*tamanho do vetor*/
#define MAX_INPUT_SIZE 100      /*numero de ficheiros max no vetor*/

tecnicofs* fs;
pthread_mutex_t trincoVetor;
pthread_mutex_t trincoBst;
pthread_rwlock_t lock_rw;

char inputCommands[MAX_COMMANDS][MAX_INPUT_SIZE];
int numberCommands = 0;
int headQueue = 0;

static void displayUsage (){
    printf("Usage: tecnicofs inputfile outputfile numthreads\n");                     
    exit(EXIT_FAILURE);
}

static void parseArgs (long argc, char* const argv[]){
    if (argc != 4) {
        fprintf(stderr, "Invalid format:\n");
        displayUsage();
    }
}

int insertCommand(char* data) {
    if(numberCommands != MAX_COMMANDS) {
        strcpy(inputCommands[numberCommands++], data);
        return 1;
    }
    return 0;
}

char* removeCommand() {
    if(numberCommands > 0){
        numberCommands--;
        return inputCommands[headQueue++];  
    }
    return NULL;
}

void errorParse(){
    fprintf(stderr, "Error: command invalid\n");
    //exit(EXIT_FAILURE);
}

void processInput(FILE *inputtxt){
    char line[MAX_INPUT_SIZE];

    while (fgets(line, sizeof(line)/sizeof(char), inputtxt)) {
        char token;
        char name[MAX_INPUT_SIZE];

        int numTokens = sscanf(line, "%c %s", &token, name);

        /* perform minimal validation */
        if (numTokens < 1) {
            continue;
        }
        switch (token) {
            case 'c':
            case 'l':
            case 'd':
                if(numTokens != 2)
                    errorParse();
                if(insertCommand(line))
                    break;
                return;
            case '#':
                break;
            default: { /* error */
                errorParse();
            }
        }
    }
}

void lock(){
    #ifdef MUTEX 
        if(pthread_mutex_lock(&trincoBst) == 0){}
        else{
            fprintf(stderr, "Error: mutex lock failed.\n");
            exit(EXIT_FAILURE);
        } 
    #elif RWLOCK
        if(pthread_rwlock_wrlock(&lock_rw) == 0){}
        else{
            fprintf(stderr, "Error: rwlock failed.\n");
            exit(EXIT_FAILURE);
        }
    #endif
}

void unlock(){
    #ifdef MUTEX 
        if(pthread_mutex_unlock(&trincoBst) == 0){}
        else{
            fprintf(stderr, "Error: mutex unlock failed.\n");
            exit(EXIT_FAILURE);
        } 
    #elif RWLOCK
        if(pthread_rwlock_unlock(&lock_rw) == 0){}
        else{
            fprintf(stderr, "Error: unlock failed.\n");
            exit(EXIT_FAILURE);
        } 
    #endif
}

void *applyCommands(){
    while(numberCommands > 0){

        int searchResult;
        int iNumber;
        char token;
        char name[MAX_INPUT_SIZE];

        pthread_mutex_lock(&trincoVetor);
        
        const char* command = removeCommand();
        if (command == NULL){
            continue;
        }

        int numTokens = sscanf(command, "%c %s", &token, name);
        if (numTokens != 2) {
            fprintf(stderr, "Error: invalid command in Queue\n");
            exit(EXIT_FAILURE);
        }
        
        if(token == 'c'){
            iNumber = obtainNewInumber(fs); 
        }

        pthread_mutex_unlock(&trincoVetor); 

        switch (token) {
            case 'c':
                lock();                   
                create(fs, name, iNumber);
                unlock();
                break;

            case 'l':
                lock();
                searchResult = lookup(fs, name);
                unlock(); 
                
                if(!searchResult)
                    printf("%s not found\n", name);
                else
                    printf("%s found with inumber %d\n", name, searchResult);
                break;

            case 'd':
                lock();   
                delete(fs, name);
                unlock();
                break;

            default: { /* error */
                fprintf(stderr, "Error: command to apply\n");
                exit(EXIT_FAILURE);
            }
        }
    }
    return NULL;
}

int main(int argc, char* argv[]) {
    struct timeval start, end;
    int i;
    
    parseArgs(argc, argv);

    FILE *inputtxt = fopen(argv[1], "r");
    if(!inputtxt){                                  /*ficheiro tem que existir*/
        fprintf(stderr, "Error: input invalid.\n");
        exit(EXIT_FAILURE);
    }

    FILE *outputtxt = fopen(argv[2], "w");
    int numberThreads = atoi(argv[3]);              /*numero de tarefas a serem criadas*/

    if(numberThreads < 1){                          /*numero de tarefas nao pode ser menor que 1*/
        fprintf(stderr, "Error: number of Threads invalid.\n");
        exit(EXIT_FAILURE);
    }
    
    if(strcmp(argv[0],"./tecnicofs-nosync") == 0){     /*forca nosync a ter apenas uma tarefa*/
        numberThreads = 1;
    }

    pthread_t tarefa[numberThreads];

    fs = new_tecnicofs();
    processInput(inputtxt);

    if(pthread_mutex_init(&trincoVetor, NULL) == 0);
    else{
        fprintf(stderr, "Error: Mutex trincoVetor not initialized.\n");
    }
    if(pthread_mutex_init(&trincoBst, NULL) == 0);
    else{
        fprintf(stderr, "Error: Mutex trincoBst not initialized.\n");
    }

    gettimeofday(&start, NULL);                                 /*inicia relogio*/
    for (i=0; i < numberThreads; i++){                          /*pool de tarefas*/
       if(pthread_create(&tarefa[i], NULL, applyCommands, NULL) == 0);
       else {
           fprintf(stderr, "Error: Thread not created.\n");
       }
    }
    for (i=0; i < numberThreads; i++){
        pthread_join(tarefa[i], NULL);
    }
    gettimeofday(&end, NULL);                                   /*para relogio*/

    double total = (end.tv_sec - start.tv_sec) + ((end.tv_usec - start.tv_usec)/1000000.0);
    printf("TecnicoFS completed in %0.4f seconds.\n", total);
    
    print_tecnicofs_tree(outputtxt, fs);
    fclose(outputtxt);

    free_tecnicofs(fs);
    exit(EXIT_SUCCESS);
}