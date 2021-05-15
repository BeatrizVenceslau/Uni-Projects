/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#include "fs.h"
#include "bst.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "sync.h"
#include "hash.h"

static int Buckets;

tecnicofs* new_tecnicofs(int numBuckets){
	int i;
	Buckets = numBuckets;
	tecnicofs*fs = malloc(numBuckets * sizeof(tecnicofs));
	if (!fs) {
		perror("failed to allocate tecnicofs");
		exit(EXIT_FAILURE);
	}
	fs->bstRoot = malloc(numBuckets * sizeof(node*));
	if (!fs->bstRoot) {
		perror("failed to allocate bstRoot");
		exit(EXIT_FAILURE);
	}
	fs->bstLock = malloc(numBuckets * sizeof(syncMech));
	if (!fs->bstLock) {
		perror("failed to allocate bstLock");
		exit(EXIT_FAILURE);
	}
	for(i = 0; i < numBuckets; i++){
		fs->bstRoot[i] = NULL;
		sync_init(&(fs->bstLock[i]));
	}
	return fs;
}

void free_tecnicofs(tecnicofs* fs){
	int i;
	for(i = 0; i < Buckets; i++){
		free_tree(fs->bstRoot[i]);
		sync_destroy(&(fs->bstLock[i]));
	}
	free(fs->bstRoot);
	free(fs->bstLock);
	free(fs);
}

void create(tecnicofs* fs, char *name, int inumber){
	int indice = hash(name, Buckets);
	sync_wrlock(&(fs->bstLock[indice]));
	fs->bstRoot[indice] = insert(fs->bstRoot[indice], name, inumber);
	sync_unlock(&(fs->bstLock[indice]));
}

void delete(tecnicofs* fs, char *name){
	int indice = hash(name, Buckets);
	sync_wrlock(&(fs->bstLock[indice]));
	fs->bstRoot[indice] = remove_item(fs->bstRoot[indice], name);
	sync_unlock(&(fs->bstLock[indice]));
}

int lookup(tecnicofs* fs, char *name){
	int inumber = -1;
	int indice = hash(name, Buckets);
	sync_rdlock(&(fs->bstLock[indice]));
	node* searchNode = search(fs->bstRoot[indice], name);
	if ( searchNode ) {
		inumber = searchNode->inumber;
	}
	sync_unlock(&(fs->bstLock[indice]));
	return inumber;
}

int reName(tecnicofs* fs, char *name, char *newName, int numBuckets){
	int indice1 = hash(name, numBuckets);
	int indice2 = hash(newName, numBuckets);
	int locks = 0;
	
	if(search(fs->bstRoot[indice1], name) != NULL && search(fs->bstRoot[indice2], newName)==NULL){
		if(indice1!=indice2){
			//se nao estiverem na mesma bst
			while (!locks){
				if (syncMech_try_lock(&(fs->bstLock[indice1]))){
					if (syncMech_try_lock(&(fs->bstLock[indice2])))
						locks = 1;
					else  // adquisicao do 2o trinco falhou
						syncMech_unlock(&(fs->bstLock[indice1]));
							// abre 1o trinco e tenta outra vez
				}
			}
		}
		else
			sync_wrlock(&(fs->bstLock[indice1]));
	}

	if(search(fs->bstRoot[indice1], name) != NULL && search(fs->bstRoot[indice2], newName)==NULL){
		fs->bstRoot[indice2] = insert(fs->bstRoot[indice2], newName, search(fs->bstRoot[indice1], name)->inumber);
		fs->bstRoot[indice1] = remove_item(fs->bstRoot[indice2], name);
	}

	if(indice1!=indice2){
		syncMech_unlock(&(fs->bstLock[indice1]));
		syncMech_unlock(&(fs->bstLock[indice2]));
	}
	else
		syncMech_unlock(&(fs->bstLock[indice1]));

	return 0;

}

void print_tecnicofs_tree(FILE * fp, tecnicofs *fs){
	int i;
	for(i = 0; i < Buckets; i++)
		print_tree(fp, fs->bstRoot[i]);
}
