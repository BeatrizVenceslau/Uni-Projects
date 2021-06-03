/*
 * File:  auxiliar.h
 * Author:  Beatriz Venceslau ist193734
 * Description: Estruturas.
 *              Constantes.
 *              Declaracoes de funcoes auxiliares.
*/

#ifndef AUXILIAR_H
#define AUXILIAR_H

#include <stdlib.h>
#include <stddef.h>
#include <stdio.h>
#include <string.h>
#include <stdint.h>

/*variaveis globais*/
#define DIM_GLOBAL 1024  /*tamanho de medida global*/
#define MEM_SPACE 20     /*tamanho de memoria a alocar incrementalmente a listGames*/

/*identificadores das Hashtables*/
#define idGame 'g'
#define idTeam 't'

/*variaveis a receber do stdin*/
char game[DIM_GLOBAL];
char team1[DIM_GLOBAL];
char team2[DIM_GLOBAL];
int score1, score2;

int numGames;            /*inicializacao do contador do numero de Games*/
int sizeListGames;       /*inicializacao do size da ListGamesmes*/

/*estruturas*/
typedef struct Game {             /*informacao de um jogo*/
    char *name;                   /*nome do jogo - serve de Key quando se chama a funcao hash*/
    char *team1;                  /*nome da Team 1*/
    char *team2;                  /*nome da Team 2*/
    int score1;                   /*pontuacao da Team 1*/
    int score2;                   /*pontuacao da Team 2*/
}Game;

typedef struct Team {             /*informacao de uma equipa*/
    char *name;                   /*nome da equipa - serve de Key quando se chama a funcao hash*/
    int gamesWon;                 /*contador de jogos ganhos por esta equipa*/
}Team;

typedef struct List{              /*informacao de uma linkedList*/
    void *data;                   /*informacao do conteudo de cada elemento da List - Game ou Team*/
    struct List *next;            /*um pointer para o proximo node da List*/
}List;

typedef struct Hashtable{         /*informacao de uma Hashtable*/
    List **list;                  /*lista com ponteiros para as cabecas das likedLists*/
}Hashtable;

char **listGames;                 /*lista dos nomes dos Games pela ordem em que foram inseridos*/
char *listTeams[DIM_GLOBAL];      /*lista dos nomes das melhores Teams*/

/*funcoes auxiliares*/
void createNewGame(List *node);          /*cria um novo node Game*/
void insertInfoGame(Game *gameData);     /*insere a informacao do Game*/
void updateGamesWon(Game *gameData);     /*atualiza a variavel gamesWon das Teams*/
void insertListGames(Game *gameData);    /*insere-se o game na listGames*/

void createNewTeam(List *node);          /*cria um novo node Team*/

void removeGamesWon(Game *gameNode);     /*atualiza a variavel gamesWon apos remover um Game*/
void removeListGames(Game *gameNode);    /*remove-se o Game da listGames*/

void changeGamesWon(Game *gameNode, int score1, int score2); /*altera a variavel gamesWon*/

int findBestTeams();                     /*encontra as Teams com mais jogos ganhos*/
void sortListTeams(int ind);             /*organiza a lista de Teams por ordem lexicografica ate ind*/
void troca(int i);                       /*troca 2 elementos da listaTeams*/

#endif
