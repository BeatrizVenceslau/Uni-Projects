/******************************************************************************\
* Distance vector routing protocol with reverse path poisoning.                *
\******************************************************************************/

#include <stdlib.h>

#include "routing-simulator.h"
#include <cstdio>

// Message format to send between nodes.
typedef struct {
  cost_t data[MAX_NODES];
} message_t;

// State format.
typedef struct {
  cost_t cost[MAX_NODES][MAX_NODES];
  node_t next_hop[MAX_NODES];
} state_t;

/*void print_Matrix(state_t *state) {
  printf("\n");
  for (int i = get_first_node(); i <= get_last_node(); i++) {
    for (int j = get_first_node(); j <= get_last_node(); j++) {
      printf("%d ", state->cost[i][j]);
    }
    printf("\n");
  }
  printf("\n");
}*/

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
    }
    
    state->next_hop[i] = -1;
  }
  return state;
}

int bellmond_ford(state_t *state) {
  int change = 0;
  
  // for every destination possible
  for (int i = get_first_node(); i <= get_last_node(); i++) {
    // ignoring itself
    if (i != get_current_node()) {
      // checks through which neighbour it costs less
      cost_t min = COST_INFINITY;
      node_t next_hop = -1;

      for (int j = get_first_node(); j <= get_last_node(); j++) {
        // checks only through neighbours
        if (j != get_current_node() && get_link_cost(j) != COST_INFINITY) {
          
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

      // check if anything changed
      if (min != state->cost[get_current_node()][i]) {
        // update locally
        state->cost[get_current_node()][i] = min;
        state->next_hop[i] = next_hop;
        // set route
        set_route(i, next_hop, min);
        change = 1; // There was change
      }
      else {
        set_route(i, next_hop, min);
      }
    }
  }
  return change; // There was no change
}

void update(state_t *state) {
  // runs Bellmon Ford and notifies neighbours if there was a change
  if (bellmond_ford(state)) {
    // checks all the nodes 
    for (int i = get_first_node(); i <= get_last_node(); i++) {
      // warns only its neighbours
      if (i != get_current_node() && get_link_cost(i) != COST_INFINITY) {
        message_t *m = (message_t*)malloc(sizeof(message_t));

        for (int j = get_first_node(); j <= get_last_node(); j++) {
          // if the node receiving the message is a next_hop of j deletes connection
          if (i == state->next_hop[j]) {
            m->data[j] = COST_INFINITY;
          } else {
            m->data[j] = state->cost[get_current_node()][j];
          }
        }

        send_message(i, m, sizeof(message_t));
      }
    }
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

  update(state);
}
