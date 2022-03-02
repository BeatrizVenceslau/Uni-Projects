/******************************************************************************\
* Path vector routing protocol.                                                *
\******************************************************************************/

#include <stdlib.h>

#include "routing-simulator.h"
#include <cstdio>

// Message format to send between nodes.
typedef struct {
  cost_t data[MAX_NODES];
  node_t path[MAX_NODES][MAX_NODES];
} message_t;

// State format.
typedef struct {
  int cost[MAX_NODES][MAX_NODES];
  int next_hop[MAX_NODES];
  node_t paths[MAX_NODES][MAX_NODES][MAX_NODES];
} state_t;

void print_Matrix(state_t *state) {
  printf("\n");
  for (int i = get_first_node(); i <= get_last_node(); i++) {
    for (int j = get_first_node(); j <= get_last_node(); j++) {
      printf("%d ", state->cost[i][j]);
    }
    printf("\n");
  }
  for (int src = get_first_node(); src <= get_last_node(); src++) {
    printf("Src: %d\n", src);
    for (int dest = get_first_node(); dest <= get_last_node(); dest++) {
        if (src != dest){
          printf("Dest: %d  --> ", dest);
          for (int hop = get_first_node(); hop <= get_last_node(); hop++) {
              printf(" %d", state->paths[src][dest][hop]);
          }
          printf("\n");
        }
    }
  }
  printf("\n");
}

// Handler for the node to allocate and initialize its state.
void *init_state() {
  state_t *state = (state_t *)calloc(1, sizeof(state_t));

  for (int i = get_first_node(); i <= get_last_node(); i++) {
    for (int j = get_first_node(); j <= get_last_node(); j++) {

      if (i == j) {
        state->cost[i][j] = 0;
      }
      else {
        state->cost[i][j] = COST_INFINITY;
      }

      for (int hop = get_first_node(); hop <= get_last_node(); hop++) {
        // from i to j path is initialized at -1 in every hop
        state->paths[i][j][hop] = -1;
      }
    }

    state->next_hop[i] = -1;
  }
  return state;
}

void set_path(state_t *state, node_t dest, node_t next_hop) {
  //printf("src %d, dest %d\n", get_current_node(), dest);
  print_Matrix(state);
  //printf("next_hop %d\n", next_hop);
  // advances all nodes in path in one unit
  for (int hop = get_last_node(); hop > get_first_node(); hop--) {
    state->paths[get_current_node()][dest][hop] = state->paths[get_current_node()][dest][hop-1];
  }
  // sets the first node in path to next_hop
  state->paths[get_current_node()][dest][0] = next_hop;
  print_Matrix(state);
}

int bellmond_ford(state_t *state) {
  int change = 0;

  // for every destination possible
  for (int i = get_first_node(); i <= get_last_node(); i++) {
    // ignoring itself
    if (i != get_current_node()) {
      printf("i %d\n", i);
      // checks through which neighbour it costs less
      cost_t min = COST_INFINITY;
      node_t next_hop = -1;
      int inPath = 0;

      for (int j = get_first_node(); j <= get_last_node(); j++) {
        inPath = 0;
        // checks only through neighbours
        if (j != get_current_node() && get_link_cost(j) != COST_INFINITY) {
          printf("-----j %d\n", j);
          // checks route to see if it participates in it
          for (int hop = get_first_node(); hop <= get_last_node(); hop++) {
            //printf("i %d, j %d\n", i, j);
            if(state->paths[j][i][hop] == get_current_node()) {
              inPath = 1;
              printf("inPath\n");
            }
          }
          
          // Only recalculates if not in path
          if (inPath != 1) {
            // calculates the minimum cost
            // the cost of getting to the neigbour and from the neighbour to the destination
            cost_t cost = COST_ADD(get_link_cost(j), state->cost[j][i]);

            // checks to see if it is a minimum
            if (cost < min) {
              min = cost;
              next_hop = j;
            }
          }
        }
      }

      // Only updates if not in path
      if (inPath != 1) {
        // check if anything changed
        // if cost changed (then next_hop != -1)
        if (min != state->cost[get_current_node()][i]) {
          // update locally
          state->cost[get_current_node()][i] = min;
          state->next_hop[i] = next_hop;

          // There was change
          change = 1;
        }
        
        // set route
        set_route(i, next_hop, min);

        // set path
        //if path no longer exists
        if (next_hop == -1) {
          // reset hops to -1
          for (int hop = get_first_node(); hop <= get_last_node(); hop++) {
            state->paths[get_current_node()][i][hop] = -1;
          }
        }
        else {
          //printf("call path from %d to %d thru %d\n", get_current_node(), i, next_hop);
          set_path(state, i, next_hop);
        }
      }
    }
  }
  return change;
}

void update(state_t *state) {
  // runs Bellmon Ford and notifies neighbours if there was a change
  if (bellmond_ford(state)) {
    // checks all the nodes 
    for (int i = get_first_node(); i <= get_last_node(); i++) {
      // warns only its neighbours
      if (i != get_current_node() && get_link_cost(i) != COST_INFINITY) {
        message_t *m = (message_t*)malloc(sizeof(message_t));

        //printf("Sending message from %d\n", i);
        for (int j = get_first_node(); j <= get_last_node(); j++) {
          //printf(" to %d: ", j);
          // if the node receiving the message is a next_hop of j deletes connection
          /*if (i == state->next_hop[j]) {
            m->data[j] = COST_INFINITY;
          } else {*/
            m->data[j] = state->cost[get_current_node()][j];
          //}

          // send path to destination j
          for (int hop = get_first_node(); hop <= get_last_node(); hop++) {
            m->path[j][hop] = state->paths[get_current_node()][j][hop];
            //printf("%d, ", m->path[j][hop]);
          }
          //printf("\n");
        }

        send_message(i, m, sizeof(message_t));
      }
    }
    //printf("\n\n");
  }
}

// Notify a node that a neighboring link has changed cost.
void notify_link_change(node_t neighbor, cost_t new_cost) {
  state_t *state = (state_t *)get_state();

  update(state);
}

// Receive a message sent by a neighboring node.
void notify_receive_message(node_t sender, void *message, int size) {
  state_t *state = (state_t *)get_state();
  message_t *m = (message_t*)message;
  
  // Update route costs
  for (int i = get_first_node(); i <= get_last_node(); i++) {
    state->cost[sender][i] = m->data[i];
  }

  // Update paths from sender to every destination i
  //printf("got message");
  for (int i = get_first_node(); i <= get_last_node(); i++) {
    //printf("\n dest %d -->", i);
    for (int hop = get_first_node(); hop <= get_last_node(); hop++) {
      state->paths[sender][i][hop] = m->path[i][hop];
      //printf("%d, ", state->paths[sender][i][hop]);
    }
  }
  //printf("\n\n");

  update(state);
}
