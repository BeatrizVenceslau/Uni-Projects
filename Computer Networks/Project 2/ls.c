/******************************************************************************\
* Link state routing protocol.                                                 *
\******************************************************************************/

#include <stdlib.h>

#include "routing-simulator.h"
#include <cstdio>

typedef struct {
  cost_t link_cost[MAX_NODES];
  int version;
} link_state_t;

// Message format to send between nodes.
typedef struct {
  link_state_t ls[MAX_NODES];
} message_t;

// State format.
typedef struct {
  link_state_t lks[MAX_NODES];
  node_t pred[MAX_NODES];
} state_t;

void print_Matrix(state_t *state) {
  printf("\n");
  for (int i = get_first_node(); i <= get_last_node(); i++) {
    for (int j = get_first_node(); j <= get_last_node(); j++) {
      printf("%d ", state->lks[i].link_cost[j]);
    }
    printf("\n");
  }
  printf("\n");
}

// Handler for the node to allocate and initialize its state.
void *init_state() {
  state_t *state = (state_t *)calloc(1, sizeof(state_t));

  for (int i = get_first_node(); i <= get_last_node(); i++) {
    state->lks[i].version = 0;

    for (int j = get_first_node(); j <= get_last_node(); j++) {

      if (i == get_current_node()) {  // If local node
        state->lks[i].link_cost[j] = get_link_cost(j);
        state->pred[j] = -1;
      }
      else if (i == j) { // If node to itself
        state->lks[i].link_cost[j] = 0;
        state->pred[j] = j;
      }
      else {
        state->lks[i].link_cost[j] = COST_INFINITY;
      }
    }
  }
  return state;
}

void Dijkstra(state_t *state) {
  int dist[MAX_NODES]; // distance represented by the link costs
  int pred[MAX_NODES];  // predecessor to the current node - how we got to it
  int visited[MAX_NODES]; // flag for every node to indicate if it has been visited

  // initialization
  for (int i = get_first_node(); i <= get_last_node(); i++) {
    dist[i] = state->lks[get_current_node()].link_cost[i];
    pred[i] = get_current_node();
    visited[i] = 0;
  }

  // starting point
  dist[get_current_node()] = 0;
  visited[get_current_node()] = 1;


  print_Matrix(state);
  // will check every node
  for (int i = get_first_node(); i <= get_last_node(); i++) {
    int min = COST_INFINITY;
    int next_hop;

    // visits every node
    for (int i = get_first_node(); i <= get_last_node(); i++) {
      // finds the clossest node
      // checks every node not yet visited to see if it can find the min distance/cost
      if (i != get_current_node() && get_link_cost(i) != COST_INFINITY) {
        if (dist[i] < min && !visited[i]) {
          min = dist[i];
          next_hop = i;
        }
      }
    }
    if (min != COST_INFINITY)
      printf("node %d - min cost to neighbor %d : %d\n", i, next_hop, min);

    // visits the node found
    visited[next_hop] = 1;
    // checks every neighbor of next_hop
    for (int i = get_first_node(); i <= get_last_node(); i++) {
      printf("node %d ---- link cost %d = %d\n", get_current_node(), i, get_link_cost(i));
      if (i != get_current_node() && !visited[i]) {
        // gets the min cost path
        printf("from cur_node %d to i = %d, link_cost = %d\n", get_current_node(), i, get_link_cost(i));
        cost_t cost = COST_ADD(min, state->lks[next_hop].link_cost[i]);
        printf("node>> %d : %d + %d = %d\n", get_current_node(), min, state->lks[next_hop].link_cost[i], cost);
        if (cost < dist[i]) {
          dist[i] = cost;
          printf("nova dist %d\n", dist[i]);
          pred[i] = next_hop;
        }
      }
    }
  }

  // Set route if anything changed
  for (int i = get_first_node(); i <= get_last_node(); i++) {
    printf("cost to i %d\n", get_link_cost(i));
    if (i != get_current_node() || dist[i] != state->lks[get_current_node()].link_cost[i]) {
      printf("dist = %d || cost = %d\n", dist[i], state->lks[get_current_node()].link_cost[i]);

      // calculates first next_hop
      node_t node = i;
      node_t hop = i;
      while(node != get_current_node()) {
        hop = node;
        node = pred[node];
      }

      set_route(i, hop, dist[i]);
      printf("from %d to %d, cost:%d\n", get_current_node(), i, dist[i]);
    }
  }
}

void update(state_t *state) {
  // runs Dijkstra
  Dijkstra(state);

  // notifies neighbours
  // checks all the nodes 
  for (int i = get_first_node(); i <= get_last_node(); i++) {
    // warns only its neighbours
    if (i != get_current_node() && get_link_cost(i) != COST_INFINITY) {
      message_t *m = (message_t*)malloc(sizeof(message_t));

      for (int j = get_first_node(); j <= get_last_node(); j++) {
        m->ls[j].version = state->lks[j].version;

        // sends all the link costs
        for (int n = get_first_node(); n <= get_last_node(); n++) {
          m->ls[j].link_cost[n] = state->lks[j].link_cost[n];
        }
      }

      send_message(i, m, sizeof(message_t));
    }
  }
}

// Notify a node that a neighboring link has changed cost.
void notify_link_change(node_t neighbor, cost_t new_cost) {
  state_t *state = (state_t *)get_state();

  state->lks[get_current_node()].link_cost[neighbor] = new_cost;
  state->lks[get_current_node()].version++;

  update(state);
}

// Receive a message sent by a neighboring node.
void notify_receive_message(node_t sender, void *message, int size) {
  state_t *state = (state_t *)get_state();
  message_t *m = (message_t*)message;
  int change = 0;

  // For every node
  for (int i = get_first_node(); i <= get_last_node(); i++) {
    // If the version is not up to date
    if (state->lks[i].version < m->ls[i].version) {
      state->lks[i].version = m->ls[i].version;

      // Update link costs
      for (int n = get_first_node(); n <= get_last_node(); n++) {
        state->lks[i].link_cost[n] = m->ls[i].link_cost[n];
      }

      change = 1;
    }
  }

  // If there were changes made will update state
  if (change) {
    update(state);
  }
}
