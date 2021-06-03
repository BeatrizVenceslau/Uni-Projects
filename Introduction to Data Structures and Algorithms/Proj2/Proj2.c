/*
 * File:  proj2.c
 * Author:  Beatriz Venceslau ist193734
 * Description: Um sistema de gestão da jogos de futebol 
 *              que permite associar a cada jogo as equipas e o resultado.
 *              Contem as funcoes principais dos comandos e as suas declaracoes.
*/

#include "auxiliar.h"
#include "hash.h"

/*declaracoes das funcoes dos comandos*/
void comando_a(int commandLine);    /*Adiciona um novo jogo*/
void comando_l(int commandLine);    /*Lista todos os jogos pela ordem em que foram introduzidos*/
void comando_p(int commandLine);    /*Procura um jogo*/
void comando_r(int commandLine);    /*Apaga um jogo*/
void comando_s(int commandLine);    /*Altera a pontuacao (score) de um jogo*/
void comando_A(int commandLine);    /*Adiciona uma nova equipa*/
void comando_P(int commandLine);    /*Procura uma equipa*/
void comando_g(int commandLine);    /*Encontra as equipas que venceram mais jogos*/

/*funcoes dos comandos*/
/*cria e insere um novo Game na htGames*/
void comando_a(int commandLine){
    scanf("%[^:\n]:%[^:\n]:%[^:\n]:%d:%d", game, team1, team2, &score1, &score2);
    
    if(searchNode(htGames, game, idGame) != NULL){      /*verifica se game existe*/
        printf("%d Jogo existente.\n", commandLine);
        return;
    }
    if(searchNode(htTeams, team1, idTeam) == NULL){     /*verifica se team1 existe*/
        printf("%d Equipa inexistente.\n", commandLine);
        return;
    }
    if(searchNode(htTeams, team2, idTeam) == NULL){     /*verifica se team2 existe*/
        printf("%d Equipa inexistente.\n", commandLine);
        return;
    }
    createNode(htGames, idGame);
}

/*lista todos os Games na listGames*/
void comando_l(int commandLine){
    int i;
    List *node;
    Game *gameInfo;

    for(i = 0; i < numGames; i++){  /*percorre a listGames*/
        node = searchNode(htGames, listGames[i], idGame);
        gameInfo = ((Game*)node->data);
        printf("%d %s %s %s", commandLine, gameInfo->name, gameInfo->team1, gameInfo->team2);
        printf(" %d %d\n", gameInfo->score1, gameInfo->score2);
    }
}

/*procura um Game com o nome dado*/
void comando_p(int commandLine){
    Game *gameInfo;
    List *node;

    scanf("%[^\n]", game);

    if((node = searchNode(htGames, game, idGame)) == NULL){   /*verifica se game existe*/
        printf("%d Jogo inexistente.\n", commandLine);
        return;
    }
    
    gameInfo = ((Game*)node->data);
    printf("%d %s %s %s", commandLine, gameInfo->name, gameInfo->team1, gameInfo->team2);
    printf(" %d %d\n", gameInfo->score1, gameInfo->score2);
}

/*apaga um Game dado um nome*/
void comando_r(int commandLine){
    List *node;

    scanf("%[^\n]", game);

    if((node = searchNode(htGames, game, idGame)) == NULL){   /*verifica se game existe*/
        printf("%d Jogo inexistente.\n", commandLine);
        return;
    }
    removeGameNode(((Game*)node->data));
}

/*altera o score de um Game dado o nome para a score recebida*/
void comando_s(int commandLine){
    List *node;

    scanf("%[^:\n]:%d:%d", game, &score1, &score2);

    if((node = searchNode(htGames, game, idGame)) == NULL){   /*verifica se game existe*/
        printf("%d Jogo inexistente.\n", commandLine);
        return;
    }

    /*altera a variavel gamesWon das Teams*/
    changeGamesWon(((Game*)node->data), score1, score2);
    
    /*altera as scores do Game*/
    ((Game*)node->data)->score1 = score1;
    ((Game*)node->data)->score2 = score2;
}

/*cria e insere uma nova Team na htTeams*/
void comando_A(int commandLine){
    scanf("%[^\n]", team1);
    
    if(searchNode(htTeams, team1, idTeam) != NULL){   /*verifica se team1 existe*/
        printf("%d Equipa existente.\n", commandLine);
        return;
    }
    createNode(htTeams, idTeam);
}

/*rocura uma Team com o nome dado*/
void comando_P(int commandLine){
    Team *teamInfo;
    List *node;

    scanf("%[^\n]", team1);

    if((node = searchNode(htTeams, team1, idTeam)) == NULL){ /*verifica se team1 existe*/
        printf("%d Equipa inexistente.\n", commandLine);
        return;
    }
    
    teamInfo = ((Team*)node->data);
    printf("%d %s %d\n", commandLine, teamInfo->name, teamInfo->gamesWon);
}

/*Encontra as Teams que ganharam mais jogos e lista por ordem lexicográfica do nome*/
void comando_g(int commandLine){
    int i, ind, maxGamesWon = -1;

    ind = findBestTeams(&maxGamesWon);
    if(ind == 0 && listTeams[0] == NULL) /*se nao houver nenhuma Team*/
        return;

    printf("%d Melhores %d\n", commandLine, maxGamesWon);
    /*percorre listTeams ate ao ultimo indice com uma Team com maxGamesWon*/
    for(i = 0; i < ind; i++){
        printf("%d * %s\n", commandLine, listTeams[i]);
    }
}

/*seleciona a funcao correta consoante o comando*/
int main() {
    int commandLine = 0;
    char command;

    /*inicializa as Hashtables*/
    htGames = htInit();
    htTeams = htInit();

    /*inicializa os contadores*/
    numGames = 0;
    sizeListGames = 0;

    while ((command = getchar()) != 'x'){  /*sai do sitema*/
        commandLine++;                     /*incrementa a linha que lancou o comando*/
        switch(command){
            case('a'):
                getchar();                 /*remove o espaco que vem a seguir ao comando*/
                comando_a(commandLine);
                break;
            case('l'):
                comando_l(commandLine);
                break;
            case('p'):
                getchar();
                comando_p(commandLine);
                break;
            case('r'):
                getchar();
                comando_r(commandLine);
                break;
            case('s'):
                getchar();
                comando_s(commandLine);
                break;
            case('A'):
                getchar();
                comando_A(commandLine);
                break;
            case('P'):
                getchar();
                comando_P(commandLine);
                break;
            case('g'):
                comando_g(commandLine);
                break;
        }
        while (getchar() != '\n'); /*resolve getchar() nao funcionar depois de scanf()*/
    }
    /*faz free as Hastables e a listGames antes de fazer exit do programa*/
    htFree(htGames, idGame);
    htFree(htTeams, idTeam);
    free(listGames);
    return 0;
}
