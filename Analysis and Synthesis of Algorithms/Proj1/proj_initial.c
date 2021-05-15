#include <stdlib.h>
#include <stddef.h>
#include <stdio.h>

/* 1st try
struct aluno {
    int id;
    int nota;
    int amigos[2];
    struct aluno* proximo;
    struct aluno* anterior;
};

struct aluno *start = NULL;

void inserirLista(int N, int M) {
    int i, a;
    struct aluno *novoInput, *anteriorInput;
 
    cabeca = (alunos *) malloc(sizeof(alunos));
    novoInput = (alunos *) malloc(sizeof(alunos));
    anteriorInput = (alunos *) malloc(sizeof(alunos));
    cabeca = NULL;
    for (i = 0; i < N; i++) {
        if(cabeca == NULL) {
            cabeca -> id = i;
            scanf("%d", &cabeca -> nota);
            cabeca -> proximo = NULL;
            cabeca -> anterior = NULL;
            anteriorInput = cabeca;
        }
        else {
            novoInput -> id = i;
            scanf("%d", &novoInput -> nota);
            novoInput -> proximo = NULL;
            novoInput -> anterior = anteriorInput;
            cabeca = novoInput;
        }
    }
    while (cabeca -> proximo != NULL ) {
        printf("%d", cabeca -> nota);
        cabeca = cabeca -> proximo;
    }    
    free(cabeca);
    free(novoInput);
    free(anteriorInput);
}

int main() {
    int N, M;
    scanf("%d,%d", &N, &M);
    inserirLista(N, M);
}*/

struct aluno {
    int id;
    int nota;
    int amigos[2];
    struct aluno* proximo;
    struct aluno* anterior;
};

struct aluno *cabeca = NULL;
void insert_at_end(int);
int count = 0;

void inserirLista(int N, int M) {
  struct aluno *t, *temp;
  int i, a;

  t = (struct aluno*)malloc(sizeof(struct aluno));
  
    for (i=0; i<N; i++) {
        if (cabeca == NULL) {
            t ->  id = i;
            scanf("%d", &t -> nota);
            cabeca = t;
            cabeca->proximo = NULL;
            printf("entrou cabeca %d",cabeca -> nota);
        }
        else {
            temp = cabeca;
            while (temp->proximo != NULL)
                temp = temp->proximo;

            t ->  id = i;
            scanf("%d", &t -> nota);
            temp->proximo = t;
            t->proximo = NULL;    
        }
    }
}

int main() {
    int N, M;
    struct aluno *temp;
    scanf("%d,%d", &N, &M);
    inserirLista(N, M);
    temp = cabeca;
    while(temp != NULL) {
        printf("main: %d", temp->nota);
        temp->proximo;
    }
}
