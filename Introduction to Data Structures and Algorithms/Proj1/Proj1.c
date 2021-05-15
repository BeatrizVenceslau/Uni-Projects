/*
 * File:  proj1.c
 * Author:  Beatriz Venceslau ist193734
 * Description: Um programa que apresenta um sistema de logistica que permite gerir stocks de produtos e encomendas.
*/

#include <stdlib.h>
#include <stddef.h>
#include <stdio.h>
#include <string.h>

#define DIM_INPUT 200           /*tamanho do array que contem a string do stdin*/
#define DIM_DADOS 4             /*tamanho do array que contem os dados vindos do input*/
#define DIM_DESCRICAO 64        /*tamanho do array que contem a descricao de um produto*/
#define PRODUTOS 10000          /*constante que define a quantidade maxima de produtos*/
#define ENCOMENDAS 500          /*constante que define a quantidade maxima de encomendas*/
#define PESO_MAX_ENCOMENDA 200  /*constante que define o peso maximo de uma encomenda - e por consequencia a quantidade maxima de produtos numa encomenda */

struct produto {                /*informacao de um produto*/
    int id;
    char descricao[DIM_DESCRICAO];
    int preco;
    int peso;
    int quantStock;             /*quantidade de stock do produto no sistema: nao esta em uso nas encomendas*/
    int quantNecessaria;        /*quantidade do produto em uso nas encomendas*/
};

struct prodEnc {                /*informacao do produto na encomenda*/
    int id;
    int quant;                  /*quantidade de produto na encomenda*/
};

struct encomenda {              /*informacao de uma encomenda*/
    int id;
    int numProdutos;            /*contador de produtos na encomenda: indice do ultimo produto na lista +1*/
    struct prodEnc listaProdutos[PESO_MAX_ENCOMENDA]; /*array que guarda os produtos da encomenda*/
};

int dados[DIM_DADOS];           /*array que contem os dados vindos do input*/
char dado1[DIM_DESCRICAO];      /*array que contem o primeiro dado do input*/
struct produto produtos[PRODUTOS]; /*array que guarda todos os produtos do sistema*/
int quantProd = 0;              /*contador de produtos no sistema: indice do ultimo produto +1*/
struct encomenda encomendas[ENCOMENDAS]; /*array que guarda todas as encomendas do sistema*/
int quantEnc = 0;               /*contador de encomendas no sistema: indice da ultima encomenda +1*/

/*funcoes auxiliares*/
void processaDados();
void trocaProdutos(int i);
void sortProdutos();
void trocaListaProdutos(struct prodEnc listaProdutos[], int i);
void sortListaProdutos(struct prodEnc listaProdutos[], int size);
int procuraProduto(int idP);
int procuraEncomenda(int idE);
int calculaPeso(int indE);
int calculaCusto(int indE);
void adicionaListaProdutos(int idP, int indE, int quant);
void troca(int indP, struct prodEnc listaProdutos[], int size);
void removeListaProdutos(int idP,int  indE);

/*funcoes dos comandos*/
void comando_a();
void comando_q();
void comando_N();
void comando_A();
void comando_r();
void comando_R();
void comando_C();
void comando_p();
void comando_E();
void comando_m();
void comando_l();
void comando_L();

/*le os dados do stdin para o vetor dados e char dado1 separando por :*/
void processaDados(){
    int i = 0;
    char input[DIM_INPUT], *token;

    getchar(); /*tira espaco*/
    fgets(input, DIM_INPUT, stdin);
    token = strtok(input, ":\n");
    strcpy(dado1, token);
         
    while (token != NULL) {
        token = strtok(NULL, ":\n");
        if(token != NULL){
            dados[i] = atoi(token);
            i++;
        }
    }
}

/*troca 2 elementos do vetor de produtos*/
void trocaProdutos(int i){
    struct produto temp;

    temp = produtos[i];
    produtos[i] = produtos[i+1];
    produtos[i+1] = temp;
}

/*ordena o vetor de produtos em ordem crescente de preco*/
void sortProdutos(){  
    int i;
   
    for(i = 0; i < quantProd-1; i++){
        if(produtos[i].preco > produtos[i+1].preco){
            trocaProdutos(i);
            /*percorre a array no sentido contrario para corrigir o que desordenou*/
            while(produtos[i-1].preco >= produtos[i].preco && i > 0){
                if(produtos[i-1].preco > produtos[i].preco){
                    trocaProdutos(i-1);
                }
                else if(produtos[i].preco == produtos[i-1].preco && produtos[i-1].id > produtos[i].id){
                    trocaProdutos(i-1);
                }
                i--;
            }
        }
        else if(produtos[i].preco == produtos[i+1].preco && produtos[i].id > produtos[i+1].id){
            trocaProdutos(i);
        }
    }

}

/*troca 2 elementos da lista de produtos de uma encomenda*/
void trocaListaProdutos(struct prodEnc listaProdutos[], int i){
    struct prodEnc temp;

    temp = listaProdutos[i];
    listaProdutos[i] = listaProdutos[i+1];
    listaProdutos[i+1] = temp;
}

/*ordena a lista de produtos de uma encomenda alfabeticamente*/
void sortListaProdutos(struct prodEnc listaProdutos[], int size){
    int i, mudanca = 1, indP1, indP2;

    if(size < 2)
        return;
    while(mudanca){ /*enquanto tiver havido trocas continua*/
        mudanca = 0;
        indP2 = procuraProduto(listaProdutos[0].id);
        for (i = 0; i < size-1; i++){
            indP1 = indP2;
            indP2 = procuraProduto(listaProdutos[i+1].id);
            /*se o produto de indP1 vier primeiro que o produto de indP2 alfabeticamente*/
            if(strcmp(produtos[indP1].descricao, produtos[indP2].descricao) > 0){
                trocaListaProdutos(listaProdutos, i);
                mudanca = 1;
            }
        }
    }
}

/*procura e retorna o indice do produto no vetor de produtos*/
int procuraProduto(int idP){
    int i;
    for(i = 0; i < quantProd; i++){
        if(produtos[i].id == idP)
            return i;/*retorna indice do produto no vetor*/
    }
    return -1;/*produto nao existe*/
}

/*verifica se a encomenda idE existe*/
int procuraEncomenda(int idE){
    if(idE < quantEnc)
        return idE;
    return -1;
}

/*calcula o peso da encomenda indE*/
int calculaPeso(int indE){
    int i, idP, indP, peso = 0;
    for(i = 0; i < encomendas[indE].numProdutos; i++){ /*so entra se a encomenda ja tiver produtos*/
        idP = encomendas[indE].listaProdutos[i].id;
        indP = procuraProduto(idP);
        peso += (encomendas[indE].listaProdutos[i].quant * produtos[indP].peso);
    }
    return peso;
}

/*calcula o custo da encomenda indE*/
int calculaCusto(int indE){
    int i, indP, custo = 0;
    for(i = 0; i < encomendas[indE].numProdutos; i++){ /*so entra se a encomenda ja tiver produtos*/
        indP = procuraProduto(encomendas[indE].listaProdutos[i].id);
        custo += encomendas[indE].listaProdutos[i].quant * produtos[indP].preco;
    }
    return custo;
}

/*adiciona produto a lista de Produtos de uma encomenda*/
void adicionaListaProdutos(int idP, int indE, int quant){
    int i, numProdutos, indP;
    indP = procuraProduto(idP);

    for(i = 0; i < encomendas[indE].numProdutos; i++){
        if(idP == encomendas[indE].listaProdutos[i].id){ /*o produto ja existe na encomenda*/
            encomendas[indE].listaProdutos[i].quant += quant;
            produtos[indP].quantStock -= quant;
            produtos[indP].quantNecessaria += quant;
            return;
        }
    }
    /*o produto ainda nao existe na encomenda*/
    numProdutos = encomendas[indE].numProdutos;
    encomendas[indE].listaProdutos[numProdutos].id = idP;
    encomendas[indE].listaProdutos[numProdutos].quant = quant;
    produtos[indP].quantStock -= quant;
    produtos[indP].quantNecessaria += quant;
    encomendas[indE].numProdutos += 1;
}

/*troca um produto da lista de Produtos de uma encomenda com o ultimo*/
void troca(int indP, struct prodEnc listaProdutos[], int size){
    /*passa o ultimo produto da lista para o indice do produto a eliminar*/
    listaProdutos[indP].id = listaProdutos[size - 1].id;
    listaProdutos[indP].quant = listaProdutos[size - 1].quant;
    /*coloca o ultimo produto da lista a zeros*/
    listaProdutos[size - 1].id = 0;
    listaProdutos[size - 1].quant = 0;
}

/*remove produto da lista de Produtos de uma encomenda*/
void removeListaProdutos(int idP,int  indE){
    int i, size, indP;
    size = encomendas[indE].numProdutos;
    indP = procuraProduto(idP);

    for(i = 0; i < size; i++){
        if(idP == encomendas[indE].listaProdutos[i].id){ /*encontramos produto a apagar*/
            produtos[indP].quantStock += encomendas[indE].listaProdutos[i].quant;
            produtos[indP].quantNecessaria -= encomendas[indE].listaProdutos[i].quant;
            troca(i, encomendas[indE].listaProdutos, encomendas[indE].numProdutos);
            encomendas[indE].numProdutos -= 1;
            return;
        }
    }
}

/*adiciona um novo produto ao sistema*/
void comando_a(){
    processaDados();
    /*indice do novo produto = quantProd*/
    strcpy(produtos[quantProd].descricao, dado1);
    produtos[quantProd].id = quantProd;
    produtos[quantProd].preco = dados[0];
    produtos[quantProd].peso = dados[1];
    produtos[quantProd].quantStock = dados[2];
    produtos[quantProd].quantNecessaria = 0;

    printf("Novo produto %d.\n", produtos[quantProd].id);
    quantProd += 1;
}

/*adiciona stock a um produto existente no sistema*/
void comando_q(){
    int idProduto, indP;

    processaDados();
    idProduto = atoi(dado1);

    if(procuraProduto(idProduto) < 0){
        printf("Impossivel adicionar produto %d ao stock. Produto inexistente.\n", idProduto);
        return;
    }

    indP = procuraProduto(idProduto);
    produtos[indP].quantStock += dados[0];
}

/*cria uma nova encomenda*/
void comando_N(){
    /*indice da nova encomenda = quantEnc*/
    encomendas[quantEnc].id = quantEnc;
    encomendas[quantEnc].numProdutos = 0;
    
    printf("Nova encomenda %d.\n", encomendas[quantEnc].id);
    quantEnc += 1;
}
/*adiciona um produto a uma encomenda.
Se o produto ja existir na encomenda, adiciona a nova quantidade a quantidade existente*/
void comando_A(){
    int idEncomenda, idProduto, quantidade;
    int pesoProduto, indP, indE;
    
    processaDados();
    idEncomenda = atoi(dado1);
    idProduto = dados[0];
    quantidade = dados[1];

    if((indE = procuraEncomenda(idEncomenda)) < 0){
        printf("Impossivel adicionar produto %d a encomenda %d. Encomenda inexistente.\n", idProduto, idEncomenda);
        return;
    }
    if((indP = procuraProduto(idProduto)) < 0){
        printf("Impossivel adicionar produto %d a encomenda %d. Produto inexistente.\n", idProduto, idEncomenda);
        return;
    }
    if(produtos[indP].quantStock < quantidade){
        printf("Impossivel adicionar produto %d a encomenda %d. Quantidade em stock insuficiente.\n", idProduto, idEncomenda);
        return;
    }
    pesoProduto = produtos[indP].peso * quantidade;
    if(pesoProduto + calculaPeso(indE) > PESO_MAX_ENCOMENDA){
        printf("Impossivel adicionar produto %d a encomenda %d. Peso da encomenda excede o maximo de %d.\n", idProduto, idEncomenda, PESO_MAX_ENCOMENDA);
        return;
    }
    adicionaListaProdutos(idProduto, indE, quantidade);
}

/*remove stock a um produto existente*/
void comando_r(){
    int idProduto, quantRetirar, indP;
    
    processaDados();
    idProduto = atoi(dado1);
    quantRetirar = dados[0];

    if((indP = procuraProduto(idProduto)) < 0){
        printf("Impossivel remover stock do produto %d. Produto inexistente.\n", idProduto);
        return;
    }
    if(quantRetirar > produtos[indP].quantStock){
        printf("Impossivel remover %d unidades do produto %d do stock. Quantidade insuficiente.\n", quantRetirar, idProduto);
        return;
    }

    produtos[indP].quantStock -= quantRetirar;
}

/*remove um produto de uma encomenda*/
void comando_R(){
    int idEncomenda, idProduto;
    int indP, indE;
    
    processaDados();
    idEncomenda = atoi(dado1);
    idProduto = dados[0];

    if((indE = procuraEncomenda(idEncomenda)) < 0){
        printf("Impossivel remover produto %d a encomenda %d. Encomenda inexistente.\n", idProduto, idEncomenda);
        return;
    }
    if((indP = procuraProduto(idProduto)) < 0){
        printf("Impossivel remover produto %d a encomenda %d. Produto inexistente.\n", idProduto, idEncomenda);
        return;
    }
    removeListaProdutos(idProduto, indE);
}

/*calcula o custo de uma encomenda*/
void comando_C(){
    int idEncomenda, indE;
    
    processaDados();
    idEncomenda = atoi(dado1);

    if((indE = procuraEncomenda(idEncomenda)) < 0){
        printf("Impossivel calcular custo da encomenda %d. Encomenda inexistente.\n", idEncomenda);
        return;
    }
    printf("Custo da encomenda %d %d.\n", idEncomenda, calculaCusto(indE));
}

/*altera o preco de um produto existente no sistema*/
void comando_p(){
    int idProduto, preco, indP;
    
    processaDados();
    idProduto = atoi(dado1);
    preco = dados[0];

    if((indP = procuraProduto(idProduto)) < 0){
        printf("Impossivel alterar preco do produto %d. Produto inexistente.\n", idProduto);
        return;
    }
    produtos[indP].preco = preco;
}

/*lista a descricao e a quantidade de um produto numa encomenda*/
void comando_E(){
    int j= 0, idEncomenda, idProduto, quantidade = 0;
    int indP, indE;
    
    processaDados();
    idEncomenda = atoi(dado1);
    idProduto = dados[0];

    if((indE = procuraEncomenda(idEncomenda)) < 0){
        printf("Impossivel listar encomenda %d. Encomenda inexistente.\n", idEncomenda);
        return;
    }
    if((indP = procuraProduto(idProduto)) < 0){
        printf("Impossivel listar produto %d. Produto inexistente.\n", idProduto);
        return;
    }

    for(j = 0; j < encomendas[indE].numProdutos; j++){
        if(idProduto == encomendas[indE].listaProdutos[j].id){ /*encontrar o produto na encomenda*/
            printf("%s %d.\n", produtos[indP].descricao, encomendas[indE].listaProdutos[j].quant);
            return;
        }
    }
    printf("%s %d.\n", produtos[indP].descricao, quantidade);
}

/*lista o identificador da encomenda em que o produto dado ocorre mais vezes.
Se houver 2 ou mais encomendas nessa situacao, devera imprimir a encomenda de menor id.*/
void comando_m(){
    int idProduto, i = 0, j = 0, quantMax = 0, idEnc;
    int prodEmEnc = 0;/*flag que indica passa a 1 se o produto estiver em pelo menos uma encomenda*/
    
    processaDados();
    idProduto = atoi(dado1);

    if(procuraProduto(idProduto) < 0){
        printf("Impossivel listar maximo do produto %d. Produto inexistente.\n", idProduto);
        return;
    }
    if(quantEnc == 0) /*se nao houver encomendas sai*/
        return;

    for(i = 0; i < quantEnc; i++){
        for(j = 0; j < encomendas[i].numProdutos; j++){
            if(idProduto == encomendas[i].listaProdutos[j].id){ /*se for o produto certo*/
                prodEmEnc = 1;
                if(encomendas[i].listaProdutos[j].quant > quantMax){ /*se existir nesta encomenda em maior quantidade do que naquelas ja verificadas*/
                    quantMax = encomendas[i].listaProdutos[j].quant;
                    idEnc = encomendas[i].id;
                }
            }
        }
    }
    if(prodEmEnc == 0) /*se o produto nao esta em nenhuma encomenda sai*/
        return;

    printf("Maximo produto %d %d %d.\n", idProduto, idEnc, quantMax);
}

/*lista todos os produtos existentes no sistema por ordem crescente de preco.
Se houver 2 ou mais produtos com o mesmo preco, devera imprimir esses por ordem crescente de id de produto*/
void comando_l(){
    int i;
    sortProdutos();

    printf("Produtos\n");
    for(i = 0; i < quantProd; i++){
        printf("* %s %d %d\n", produtos[i].descricao, produtos[i].preco, produtos[i].quantStock);
    }
}

/*lista todos os produtos de uma encomenda por ordem alfabetica da descricao*/
void comando_L(){
    int idEncomenda, i = 0, indE, indP, idP;
    
    processaDados();
    idEncomenda = atoi(dado1);

    if(idEncomenda >= quantEnc){
        printf("Impossivel listar encomenda %d. Encomenda inexistente.\n", idEncomenda);
        return;
    }

    indE = idEncomenda;
    sortListaProdutos(encomendas[indE].listaProdutos, encomendas[indE].numProdutos);

    printf("Encomenda %d\n", idEncomenda);
    for(i = 0; i < encomendas[indE].numProdutos; i++){
        idP = encomendas[indE].listaProdutos[i].id;
        indP = procuraProduto(idP);
        printf("* %s %d %d\n", produtos[indP].descricao, produtos[indP].preco, encomendas[indE].listaProdutos[i].quant);
    }
}

/*seleciona a funcao a realizar consoante o primeiro caracter do input - comando*/
int main() {
    char comando;

    while ((comando = getchar()) != 'x'){ /*sai do sistema*/
        switch(comando){
            case('a'):
                comando_a();
                break;
            case('q'):
                comando_q();
                break;
            case('N'):
                comando_N();
                break;
            case('A'):
                comando_A();
                break;
            case('r'):
                comando_r();
                break;
            case('R'):
                comando_R();
                break;
            case('C'):
                comando_C();
                break;
            case('p'):
                comando_p();
                break;
            case('E'):
                comando_E();
                break;
            case('m'):
                comando_m();
                break;
            case('l'):
                comando_l();
                break;
            case('L'):
                comando_L();
                break;
        }
    }
    return 0;
}
