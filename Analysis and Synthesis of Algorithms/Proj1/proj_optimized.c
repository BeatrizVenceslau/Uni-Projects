#include <stdlib.h>
#include <stddef.h>
#include <stdio.h>

struct aluno {
    int id;
    int nota;
    int *amigos; 
    struct aluno* proximo;
    struct aluno* anterior;
};

int *amigosModificados;

void inserirLista(struct aluno *cabeca, int nota, int id, int M);
void inserirAmigo(struct aluno *cabeca, int id, int amigo);
int procuraAmigos(struct aluno *cabeca, int notaMax);
int calculaAlgoritmo(struct aluno *cabeca, int notaCmp);
void display(struct aluno *cabeca);

void inserirLista(struct aluno *cabeca, int nota, int id, int M) {
  struct aluno *t, *temp;

    t = (struct aluno*)malloc(sizeof(struct aluno));
    t -> nota = nota;
    t -> id = id + 1;
    t -> amigos = (int*)malloc(M* sizeof(int));
    t -> proximo = NULL;  
    temp = cabeca;
    while (temp->proximo != NULL) {
        temp = temp->proximo;
    }
    t -> anterior = temp;
    temp->proximo = t; 
}

void inserirAmigo(struct aluno *cabeca, int id, int amigo) {
    struct aluno *temp;
    int count = 0;

    temp = cabeca;
    while (temp != NULL) {
        if(temp -> id == id) {
            if (temp -> amigos[count] == 0) { 
                temp -> amigos[count] = amigo;
            }
            else{
                temp -> amigos[count+1] = amigo;
            }
        }
        temp = temp->proximo;
    }
}

int procuraAmigos(struct aluno *cabeca, int notaMax) {
    struct aluno *temp;
    int i, d, t, novaNota;

    printf("entrou\n");
    notaMax = calculaAlgoritmo(cabeca, notaMax);
    
    for (i = 0; amigosModificados[i] != 0; i++) {
        //printf("%d amigo modificado\n", amigosModificados[i]);
        temp = cabeca;
        while(temp != NULL) {
            for(d = 0; temp->amigos[d] != 0; d++) {
                if(temp->amigos[d] == amigosModificados[i]){
                    if (temp ->nota < notaMax) {
                        temp->nota = notaMax;
                        for(t = 0; amigosModificados[t] != 0; t++);
                        amigosModificados[t] = temp -> id;
                    }
                }
            }
            temp = temp->proximo;
        }
    }
    novaNota = calculaAlgoritmo(cabeca, notaMax);
    if(notaMax != novaNota)
        return 1;
    return 0;
}

int calculaAlgoritmo(struct aluno *cabeca, int notaCmp) {
    struct aluno *temp = cabeca;
    int notaMax = 0, id;

    while(temp != NULL) {
        if(temp->nota > notaMax /*&& notaCmp != temp->nota*/) {
            notaMax = temp->nota;
            id = temp -> id;
        }
        temp = temp -> proximo;
    }
    printf("%d\n", notaMax);
    amigosModificados[0] = id;
    printf("entrar procura\n");
    return notaMax;
}

void display(struct aluno *cabeca) {
    struct aluno *temp = cabeca;

    while(temp != NULL) {
        printf("%d\n", temp -> nota);
        temp = temp -> proximo;
    }
}

int main() {
    int N, M, nota, i, d, id, amigo;
    struct aluno *cabeca = (struct aluno*)malloc(sizeof(struct aluno));
    scanf("%d,%d", &N, &M);
    amigosModificados = (int*)malloc(M * sizeof (int));
    scanf("%d", &nota);
    cabeca -> nota = nota;
    cabeca -> id = 1;
    cabeca -> amigos = (int*)malloc(M* sizeof(int));
    cabeca -> proximo = NULL;
    cabeca -> anterior = NULL;
    printf("%d// %d\n",cabeca -> nota, cabeca->id);
    for (i = 1; i < N; i++) {
        scanf("%d", &nota);
        inserirLista(cabeca, nota, i, M);
    }
    for (d = 0; d < M; d++) {
        scanf("%d %d", &id, &amigo);
        inserirAmigo(cabeca, id, amigo);
    }
    printf("entrar calcula\n");
    
    if (procuraAmigos(cabeca, nota) == 1) {
        procuraAmigos(cabeca, nota);
    }
    display(cabeca);
}
