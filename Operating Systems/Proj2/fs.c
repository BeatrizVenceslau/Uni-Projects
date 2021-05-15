/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#include "fs.h"
#include "lib/bst.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "sync.h"
#include "lib/hash.h"

static int Buckets;

int obtainNewInumber(tecnicofs* fs) {
	int newInumber = ++(fs->nextINumber);
	return newInumber;
}

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
	fs->nextINumber = 0;
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
	int inumber = 0;
	int indice = hash(name, Buckets);
	sync_rdlock(&(fs->bstLock[indice]));
	node* searchNode = search(fs->bstRoot[indice], name);
	if ( searchNode ) {
		inumber = searchNode->inumber;
	}
	sync_unlock(&(fs->bstLock[indice]));
	return inumber;
}

void print_tecnicofs_tree(FILE * fp, tecnicofs *fs){
	int i;
	for(i = 0; i < Buckets; i++)
		print_tree(fp, fs->bstRoot[i]);
}
