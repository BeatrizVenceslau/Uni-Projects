/*https://www.tutorialspoint.com/data_structures_algorithms/hash_table_program_in_c.htm*/
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

struct DataItem {
   int data;   
   int nota;
};

int hashCode(int nota) {
   return nota % N;
}

struct DataItem *search(int nota) {
   //get the hash 
   int hashIndex = hashCode(nota);  
	
   //move in array until an empty 
   while(hashArray[hashIndex] != NULL) {
	
      if(hashArray[hashIndex]->nota == nota)
         return hashArray[hashIndex]; 
			
      //go to next cell
      ++hashIndex;
		
      //wrap around the table
      hashIndex %= N;
   }        
	
   return NULL;        
}

void insert(int nota,int data) {

   struct DataItem *item = (struct DataItem*) malloc(Nof(struct DataItem));
   item->data = data;  
   item->nota = nota;

   //get the hash 
   int hashIndex = hashCode(nota);

   //move in array until an empty or deleted cell
   while(hashArray[hashIndex] != NULL && hashArray[hashIndex]->nota != -1) {
      //go to next cell
      ++hashIndex;
		
      //wrap around the table
      hashIndex %= N;
   }
	
   hashArray[hashIndex] = item;
}

struct DataItem* delete(struct DataItem* item) {
   int nota = item->nota;

   //get the hash 
   int hashIndex = hashCode(nota);

   //move in array until an empty
   while(hashArray[hashIndex] != NULL) {
	
      if(hashArray[hashIndex]->nota == nota) {
         struct DataItem* temp = hashArray[hashIndex]; 
			
         //assign a dummy item at deleted position
         hashArray[hashIndex] = dummyItem; 
         return temp;
      }
		
      //go to next cell
      ++hashIndex;
		
      //wrap around the table
      hashIndex %= N;
   }      
	
   return NULL;        
}

void display() {
   int i = 0;
	
   for(i = 0; i<N; i++) {
	
      if(hashArray[i] != NULL)
         printf(" (%d,%d)",hashArray[i]->nota,hashArray[i]->data);
      else
         printf(" ~~ ");
   }
	
   printf("\n");
}

int main() {
    int N, M;
    scanf("%d,%d", &N, &M);

    struct DataItem* hashArray[N]; 
    struct DataItem* dummyItem;
    struct DataItem* item;

   dummyItem = (struct DataItem*) malloc(sizeof(struct DataItem));
   dummyItem->data = -1;  
   dummyItem->nota = -1; 

   insert(1, 20);
   insert(2, 70);
   insert(42, 80);
   insert(4, 25);
   insert(12, 44);
   insert(14, 32);
   insert(17, 11);
   insert(13, 78);
   insert(37, 97);

   display();
   item = search(37);

   if(item != NULL) {
      printf("Element found: %d\n", item->data);
   } else {
      printf("Element not found\n");
   }

   delete(item);
   item = search(37);

   if(item != NULL) {
      printf("Element found: %d\n", item->data);
   } else {
      printf("Element not found\n");
   }
}