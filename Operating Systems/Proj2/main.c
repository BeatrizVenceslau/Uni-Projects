/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 
   Beatriz Venceslau ist193734
   Carolina Ramos    ist193694                 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include "fs.h"
#include "constants.h"
#include "lib/timer.h"
#include "sync.h"
#include "lib/hash.h"
#include "semaphore.h"

char* global_inputFile = NULL;
char* global_outputFile = NULL;
int numberThreads = 0;
int numBuckets = 0;
pthread_mutex_t commandsLock;
pthread_mutex_t loadLock;
pthread_mutex_t executeLock;
sem_t loadInputCommand;
sem_t executeCommand;
tecnicofs* fs;

char inputCommands[MAX_COMMANDS][MAX_INPUT_SIZE];
int prodpos = 0;
int conspos = 0;

static void displayUsage (const char* appName){
    printf("Usage: %s input_filepath output_filepath threads_number numbuckets\n", appName);
    exit(EXIT_FAILURE);
}

static void parseArgs (long argc, char* const argv[]){
    if (argc != 5) {
        fprintf(stderr, "Invalid format:\n");
        displayUsage(argv[0]);
    }

    global_inputFile = argv[1];
    global_outputFile = argv[2];
    numberThreads = atoi(argv[3]);
    numBuckets = atoi(argv[4]);
    if (!numberThreads) {
        fprintf(stderr, "Invalid number of threads\n");
        displayUsage(argv[0]);
    }
    #if defined (RWLOCK) || defined (MUTEX)
    #else
        numberThreads = 1;
    #endif
}

int insertCommand(char* data) { /*Produtor*/
    sem_wait(&loadInputCommand);
    mutex_lock(&loadLock);
    strcpy(inputCommands[prodpos], data);
    prodpos = (prodpos + 1) % MAX_COMMANDS;
    mutex_unlock(&loadLock);
    sem_post(&executeCommand);
    return 1;
}

char* removeCommand() { /*Consumidor*/
    char *command;
    sem_wait(&executeCommand);
    mutex_lock(&executeLock);
    command = inputCommands[conspos];
    conspos = (conspos + 1) % MAX_COMMANDS;  
    mutex_unlock(&executeLock);
    return command;
}

void errorParse(int lineNumber){
    fprintf(stderr, "Error: line %d invalid\n", lineNumber);
    exit(EXIT_FAILURE);
}

void* processInput(){
    FILE* inputFile;
    inputFile = fopen(global_inputFile, "r");
    if(!inputFile){
        fprintf(stderr, "Error: Could not read %s\n", global_inputFile);
        exit(EXIT_FAILURE);
    }
    char line[MAX_INPUT_SIZE];
    int lineNumber = 0;
    while (fgets(line, sizeof(line)/sizeof(char), inputFile)) {
        char token;
        char name[MAX_INPUT_SIZE];
        lineNumber++;
        int numTokens;
        char newName[MAX_INPUT_SIZE];
        
        numTokens = sscanf(line, "%c %s %s", &token, name, newName);
                
        /* perform minimal validation */
        if (numTokens < 1) {
            continue;
        }
        
        switch (token) {
            case 'c':
            case 'l':
            case 'r':
            case 'd':
                if((numTokens != 2) && (numTokens !=3))
                    errorParse(lineNumber);
                if(insertCommand(line)){
                    break;
                }
                return NULL;
            case '#':
                break;
            default: { /* error */
                errorParse(lineNumber);
            }
        }
    }
    fclose(inputFile);
    insertCommand("x");                 /*fim de comandos a executar*/
    return NULL;
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

void* applyCommands(){
    while(1){
        mutex_lock(&commandsLock);
        const char* command = removeCommand();
        if (command == NULL){
            mutex_unlock(&commandsLock);
            sem_post(&loadInputCommand);
            continue;
        }
        char token;
        char name[MAX_INPUT_SIZE];
        char newName[MAX_INPUT_SIZE];

        sscanf(command, "%c %s %s", &token, name, newName);
        sem_post(&loadInputCommand);

        int iNumber;
        switch (token) {
            case 'c':
                iNumber = obtainNewInumber(fs);
                mutex_unlock(&commandsLock);
                create(fs, name, iNumber);
                break;
            case 'l':
                mutex_unlock(&commandsLock);
                int searchResult = lookup(fs, name);
                if(!searchResult)
                    printf("%s not found\n", name);
                else
                    printf("%s found with inumber %d\n", name, searchResult);
                break;
            case 'r':
                mutex_unlock(&commandsLock);
                int idNumber = lookup(fs, name);
                int idNumberNew = lookup(fs, newName);
                if(idNumber && !idNumberNew){
                    delete(fs, name);
                    create(fs, newName, idNumber);
                }
                break;
            case 'd':
                mutex_unlock(&commandsLock);
                delete(fs, name);
                break;
            case 'x':                               /*termina a thread e alerta a proxima*/
                mutex_unlock(&commandsLock);
                insertCommand("x");
                sem_post(&executeCommand);
                return NULL;
            default: { /* error */
                mutex_unlock(&commandsLock);
                fprintf(stderr, "Error: commands to apply\n");
                exit(EXIT_FAILURE);
            } 
        }
    }
    return NULL;
}

void initSem(){
    int err = sem_init(&loadInputCommand, 0, MAX_COMMANDS);
    if (err != 0){
        perror("Can't create semaphore loadInputCommand.");
        exit(EXIT_FAILURE);
    }
    err = sem_init(&executeCommand, 0, 0);
    if (err != 0){
        perror("Can't create semaphore executeCommand.");
        exit(EXIT_FAILURE);
    }
}

void runThreads(FILE* timeFp){
    TIMER_T startTime, stopTime;
    pthread_t* workers = (pthread_t*) malloc((numberThreads + 1) * sizeof(pthread_t));

    TIMER_READ(startTime);
    int err = pthread_create(&workers[0], NULL, processInput, NULL);
    if (err != 0){
        perror("Can't create thread");
        exit(EXIT_FAILURE);
    }

    for(int i = 1; i < numberThreads + 1; i++){
        err = pthread_create(&workers[i], NULL, applyCommands, NULL);
        if (err != 0){
            perror("Can't create thread");
            exit(EXIT_FAILURE);
        }
    }

    for(int i = 0; i < numberThreads + 1; i++) {
        err = pthread_join(workers[i], NULL);
        if(err) {
            perror("Can't join thread");
        }
    }
    TIMER_READ(stopTime);
    fprintf(timeFp, "TecnicoFS completed in %.4f seconds.\n", TIMER_DIFF_SECONDS(startTime, stopTime));
    free(workers);
}

int main(int argc, char* argv[]) {
    parseArgs(argc, argv);
    initSem();
    FILE * outputFp = openOutputFile();
    mutex_init(&commandsLock);
    fs = new_tecnicofs(numBuckets);

    runThreads(stdout);
    print_tecnicofs_tree(outputFp, fs);
    fflush(outputFp);
    fclose(outputFp);

    mutex_destroy(&commandsLock);
    free_tecnicofs(fs);
    exit(EXIT_SUCCESS);
}
