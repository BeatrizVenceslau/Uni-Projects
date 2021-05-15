/*
 * File:  hash.c
 * Author:  Beatriz Venceslau ist193734
 * Description: Funcoes auxiliares da Hashtable.
*/

#include "hash.h"

/*retorna um indice para cada key*/
int hash(char *key, int size){
    int hash = 0, a = 127;

    for(; *key != '\0'; key++)
        hash = (a * hash + *key) % size;
    
    return hash;
}

/*inicializa uma Hashtable*/
Hashtable *htInit(){
    Hashtable *ht;
    int i;

    ht  = malloc(sizeof(Hashtable));
    ht->list = (List**)malloc(HT_SIZE * sizeof(List));
    for(i = 0; i < HT_SIZE; i++)   /*inicializa cada linkedList da Hashtable a NULL*/
        ht->list[i] = NULL;

    return ht;
}

/*cria um node Game ou Team*/
void createNode(Hashtable *ht, char id){
    List *linkNode;
    linkNode = malloc(sizeof(List));

    switch (id){
        case idGame:
            createNewGame(linkNode);
            break;
        
        case idTeam:
            createNewTeam(linkNode);
            break;
    }
    linkNode->next = NULL;

    insertNode(ht, linkNode, id);
}

/*insere o linkNode na Hashtable ht*/
void insertNode(Hashtable *ht, List *linkNode, char id){
    int ind;

    switch (id){
        case idGame:
            ind = hash(((Game*)linkNode->data)->name, HT_SIZE);
            break;
        
        case idTeam:
            ind = hash(((Team*)linkNode->data)->name, HT_SIZE);
            break;
    }

    if(ht->list[ind] == NULL){ /*se for o primeiro elemento a ser inserido*/
        linkNode->next = NULL;
        ht->list[ind] = linkNode;
    }
    else{
        linkNode->next = ht->list[ind];
        ht->list[ind] = linkNode;
    }
}

/*procura um node com o nome nameP na Hashtable ht*/
List* searchNode(Hashtable *ht, char *name, char id){
    List *node;
    char *nameP;
    int ind;
    nameP = name;

    ind = hash(nameP, HT_SIZE);
    node = ht->list[ind];
    switch (id){
        case idGame:
            while(node != NULL){
                if(strcmp(((Game*)node->data)->name, name) == 0)
                    break;
                node = node->next;
            }
            break;
        
        case idTeam:
            while(node != NULL){
                if(strcmp(((Team*)node->data)->name, name) == 0)
                    break;
                node = node->next;
            }
            break;
    }
    return node; /*retorna NULL se nao encontrar e o node caso contrario*/
}

/*remove um Game node dado da Hashtable htGames*/
void removeGameNode(Game *gameNode){
    List *list, *temp, *previousNode;
    Game *tempInfo;
    int ind = hash(gameNode->name, HT_SIZE);

    removeGamesWon(gameNode);    /*atualiza a variavel gamesWon nas Teams em questao*/
    removeListGames(gameNode);   /*remove o Game da listGames*/
    
    list = htGames->list[ind];
    previousNode = list;
    while(list != NULL){
        temp = list;
        tempInfo = ((Game*)temp->data);
        if(strcmp(tempInfo->name, gameNode->name) == 0){
            previousNode->next = temp->next;
            freeData(tempInfo, idGame);
            free(temp->data);
            temp->data=NULL;

            if(previousNode == temp){ /*se for o primeiro elemento*/
                htGames->list[ind] = temp->next;
            }
            free(temp);
            temp=NULL;
            return;
        }
        else{
            previousNode = list;
            list = list->next;
        }
    }
}

/*faz free a Hashtable ht*/
void htFree(Hashtable *ht, char id){
    int i;
    List *temp, *linkedList;

    for(i = 0; i < HT_SIZE; i++){
        linkedList = ht->list[i];
        while(linkedList != NULL){
            temp = linkedList->next;
            freeData(linkedList->data, id);
            free(linkedList->data);
            linkedList->data=NULL;
            free(linkedList);
            linkedList = temp;
        }
        free(linkedList);    /*faz free a cabeca da lista*/
        linkedList=NULL;
    }
    free(ht->list);
    ht->list=NULL;
    free(ht);
    ht=NULL;
}

/*faz free ao conteudo de cada node da list da Hashtable*/
void freeData(void *data, char id){
    switch(id){
        case idGame:
            free(((Game*)data)->name);
            free(((Game*)data)->team1);
            free(((Game*)data)->team2);
            break;
        case idTeam:
            free(((Team*)data)->name);
            break;
    }
}
