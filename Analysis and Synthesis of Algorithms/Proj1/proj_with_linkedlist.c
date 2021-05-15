/*https://www.tutorialspoint.com/data_structures_algorithms/doubly_linked_list_program_in_c.htm*/
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

struct aluno {
   int id;
   int nota;
   int amigos[2];
	
   struct aluno *next;
   struct aluno *prev;
};

//this link always point to first Link
struct aluno *head = NULL;

//this link always point to last Link 
struct aluno *last = NULL;

struct aluno *current = NULL;

//is list empty
bool isEmpty() {
   return head == NULL;
}

int length() {
   int length = 0;
   struct aluno *current;
	
   for(current = head; current != NULL; current = current->next){
      length++;
   }
	
   return length;
}

//display the list in from first to last
void printList() {

   //start from the beginning
   struct aluno *ptr = head;
	
   //navigate till the end of the list
   while(ptr != NULL) {        
      printf("%d, %d ",ptr->nota,ptr->id);
      ptr = ptr->next;
   }
}

//insert link at the end
void insertEnd(int nota, int id) {

   //create a link
   struct aluno *link = (struct aluno*) malloc(sizeof(struct aluno));
   link->nota = nota;
   link->id = id;
	
   if(isEmpty()) {
      //make it the last link
      last = link;
   } else {
      //make link a new last link
      last->next = link;     
      
      //mark old last aluno as prev of new link
      link->prev = last;
   }

   //point last to new last aluno
   last = link;
}

//delete a link with given nota
struct aluno* delete(int nota) {

   //start from the first link
   struct aluno* current = head;
   struct aluno* previous = NULL;
	
   //if list is empty
   if(head == NULL) {
      return NULL;
   }

   //navigate through list
   while(current->nota != nota) {
      //if it is last aluno
		
      if(current->next == NULL) {
         return NULL;
      } else {
         //store reference to current link
         previous = current;
			
         //move to next link
         current = current->next;             
      }
   }

   //found a match, update the link
   if(current == head) {
      //change first to point to next link
      head = head->next;
   } else {
      //bypass the current link
      current->prev->next = current->next;
   }    

   if(current == last) {
      //change last to point to prev link
      last = current->prev;
   } else {
      current->next->prev = current->prev;
   }
	
   return current;
}

void main() {

   int N, M;
   scanf("%d,%d", &N, &M); 

   printList();
}