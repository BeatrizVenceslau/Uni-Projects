/*
 * File:  hash.h
 * Author:  Beatriz Venceslau ist193734
 * Description: Declaracoes de funcoes da Hashtable
 *              Constantes e estruturas.
*/

#ifndef HASH_H
#define HASH_H

#include "aux.h"
#include "hash.h"

/*variaveis globais*/
#define HT_SIZE 1999          /*tamanho das Hashtables*/

/*variaveis globais*/
Hashtable *htGames;           /*Hashtable de Games*/
Hashtable *htTeams;           /*Hashtable de Teams*/

/*funcoes auxiliares*/
int hash(char *key, int size);              /*retorna um indice para cada key*/
Hashtable *htInit();                        /*inicializa uma Hashtable*/
void createNode(Hashtable *ht, char id);    /*cria um node Game ou Team - identificado pelo id*/
void insertNode(Hashtable *ht, List *linkNode, char id); /*insere o linkNode na Hashtable ht*/
List* searchNode(Hashtable *ht, char *nameP, char id); /*procura um node com o nome nameP na Hashtable ht*/
void removeGameNode(Game *gameNode);        /*remove um Game node dado da Hashtable htGames*/
void htFree(Hashtable *ht, char id);        /*faz free a Hashtable ht*/
void freeData(void *data, char id);         /*faz free ao conteudo de cada node da list da Hashtable*/

#endif