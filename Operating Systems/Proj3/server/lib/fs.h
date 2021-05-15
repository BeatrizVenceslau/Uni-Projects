/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#ifndef FS_H
#define FS_H
#include "bst.h"
#include "sync.h"

typedef struct tecnicofs {
    node** bstRoot;
    syncMech* bstLock;
} tecnicofs;

tecnicofs* new_tecnicofs(int numBuckets);
void free_tecnicofs(tecnicofs* fs);
void create(tecnicofs* fs, char *name, int inumber);
void delete(tecnicofs* fs, char *name);
int lookup(tecnicofs* fs, char *name);
int reName(tecnicofs* fs, char *name, char *newName, int numBuckets);
void print_tecnicofs_tree(FILE * fp, tecnicofs *fs);

#endif /* FS_H */
