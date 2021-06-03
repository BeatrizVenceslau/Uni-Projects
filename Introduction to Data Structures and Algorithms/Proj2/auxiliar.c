/*
 * File:  auxiliar.c
 * Author:  Beatriz Venceslau ist193734
 * Description: Funcoes auxiliares aos comandos e as funcoes das Hashtables.
*/

#include "auxiliar.h"
#include "hash.h"

/*cria um novo node Game*/
void createNewGame(List *node){
    node->data = malloc(sizeof(Game));

    insertInfoGame(node->data);
    updateGamesWon(node->data);
    insertListGames(node->data);
}

/*insere a informacao do Game*/
void insertInfoGame(Game *gameData){
    gameData->name = malloc(sizeof(char) * strlen(game) + 1);
    strcpy(gameData->name, game);

    gameData->team1 = malloc(sizeof(char) * strlen(team1) + 1);
    strcpy(gameData->team1, team1);

    gameData->team2 = malloc(sizeof(char) * strlen(team2) + 1);
    strcpy(gameData->team2, team2);

    gameData->score1 = score1;

    gameData->score2 = score2;
}

/*atualiza a variavel gamesWon das Teams se necessario*/
void updateGamesWon(Game *gameData){
    List* teamWon;

    /*se for empate nao se altera a variavel*/
    if(gameData->score1 > gameData->score2){ /*Team1 ganha*/
        teamWon = searchNode(htTeams, team1, idTeam);
        ((Team*)teamWon->data)->gamesWon += 1;
    }
    else if(gameData->score1 < gameData->score2){ /*Team2 ganha*/
        teamWon = searchNode(htTeams, team2, idTeam);
        ((Team*)teamWon->data)->gamesWon += 1;
    }
}

/*insere-se o game na listGames*/
void insertListGames(Game *gameData){
    /*se ja nao houver memoria disponivel alloca-se mais*/
    if(!(numGames % MEM_SPACE) && (sizeListGames-numGames) < MEM_SPACE){
        listGames = realloc(listGames, (sizeListGames + MEM_SPACE) * sizeof(char*));
        sizeListGames += MEM_SPACE;
    }
    listGames[numGames] = gameData->name;
    numGames += 1;
}

/*cria um novo node Team*/
void createNewTeam(List *node){
    node->data = malloc(sizeof(Team));

    ((Team*)node->data)->name = malloc(sizeof(char) * strlen(team1) + 1);
    strcpy(((Team*)node->data)->name, team1);

    ((Team*)node->data)->gamesWon = 0; /*inicializa-se os gamesWon a zero*/
}

/*remove um a variavel gamesWon na Team que ganhou o Game a remover*/
void removeGamesWon(Game *gameNode){
    List *teamWon;
    
    /*se foi um empate nao se altera a variavel gamesWon a nenhuma das Teams*/
    if(gameNode->score1 > gameNode->score2){ /*team1 tinha ganho*/
        teamWon = searchNode(htTeams, gameNode->team1, idTeam);
        ((Team*)teamWon->data)->gamesWon -= 1;
    }
    if(gameNode->score1 < gameNode->score2){ /*team2 tinha ganho*/
        teamWon = searchNode(htTeams, gameNode->team2, idTeam);
        ((Team*)teamWon->data)->gamesWon -= 1;
    }
}

/*remove-se o Game da listGames*/
void removeListGames(Game *gameNode){
    int i;

    for(i = 0; i < numGames; i++){
        if(strcmp(gameNode->name, listGames[i]) == 0){
            for(; i < numGames-1; i++)
                listGames[i] = listGames[i+1];
            numGames -=1;
            return;
        }
    }
}


/*altera a variavel gamesWon nas duas Teams envolviadas no Game*/
void changeGamesWon(Game *gameNode, int score1, int score2){
    List *teamWon;

    /*poe ambas as Teams em pe de igualdade - ignora as scores anteriores*/
    removeGamesWon(gameNode);
    
    /*atualiza as Teams com as novas scores*/
    /*se foi um empate nao se altera a variavel gamesWon a nenhuma das Teams*/
    if(score1 > score2){ /*Team1 ganha*/
        teamWon = searchNode(htTeams, gameNode->team1, idTeam);
        ((Team*)teamWon->data)->gamesWon += 1;
    }
    else if(score2 > score1){ /*Team2 ganha*/
        teamWon = searchNode(htTeams, gameNode->team2, idTeam);
        ((Team*)teamWon->data)->gamesWon += 1;
    }
}

/*encontra as Teams com mais jogos ganhos*/
int findBestTeams(int *maxGamesWon){
    int i, lastInd = 0;
    Team *teamInfo;
    List *node;

    for(i = 0; i < HT_SIZE; i++){
        node = htTeams->list[i];
        while(node != NULL){
            teamInfo = ((Team*)node->data);
            /*se for uma das melhores equipas*/
            if(teamInfo->gamesWon == *maxGamesWon){
                listTeams[lastInd] = teamInfo->name;
                lastInd += 1;
            }
            /*se for melhor que as equipas ja guardadas*/
            else if(teamInfo->gamesWon > *maxGamesWon){
                *maxGamesWon = teamInfo->gamesWon; /*atualiza-se o valor maximo de jogos ganhos*/
                listTeams[0] = teamInfo->name;
                lastInd = 1;
            }
            node = node->next;
            }
    }

    sortListTeams(lastInd);
    return lastInd;  /*retorna o ultimo indice da listTeams com maxGamesWon*/
}

/*organiza a lista de Teams por ordem lexicografica do nome*/
void sortListTeams(int ind){
    int i;

    for(i = 0; i < ind-1; i++){
        if(strcmp(listTeams[i], listTeams[i+1]) > 0){ /*se a ordem estiver trocada*/
            troca(i);
            /*percorre a lista ate ao inicio corrigindo possiveis alteracoes*/
            while(i > 0 && strcmp(listTeams[i-1], listTeams[i]) > 0){
                if(strcmp(listTeams[i-1], listTeams[i]) > 0){
                    troca(i-1);
                }
                i--;
            }
        }
    }
}

/*troca 2 elementos da listaTeams*/
void troca(int i){
    char* temp;

    temp = listTeams[i];
    listTeams[i] = listTeams[i+1];
    listTeams[i+1] = temp;
}
