# takuzu.py: Template para implementação do projeto de Inteligência Artificial 2021/2022.
# Devem alterar as classes e funções neste ficheiro de acordo com as instruções do enunciado.
# Além das funções e classes já definidas, podem acrescentar outras que considerem pertinentes.

# Grupo 05:
# 93694 Carolina Ramos
# 93734 Maria Beatriz Venceslau

import time
from sys import stdin

from search import (
    Problem,
    Node,
    compare_searchers,

    astar_search,
    breadth_first_tree_search,
    depth_first_tree_search,
    greedy_search,
    recursive_best_first_search,
    uniform_cost_search,
    depth_limited_search,
    bidirectional_search,
    iterative_deepening_search,
)


class TakuzuState:
    state_id = 0

    def __init__(self, board):
        self.board = board
        self.id = TakuzuState.state_id
        TakuzuState.state_id += 1

    def __lt__(self, other):
        """ Este método é utilizado em caso de empate na gestão da lista
        de abertos nas procuras informadas. """
        return self.id < other.id


class Board:
    """Representação interna de um tabuleiro de Takuzu."""
    def __init__(self, size, limit, col_ones, col_zeros, row_ones, row_zeros, state):
        self.size = size
        self.limit = limit
        self.col_ones = col_ones
        self.col_zeros = col_zeros
        self.row_ones = row_ones
        self.row_zeros = row_zeros
        self.state = state

    def __str__(self) -> str:
        return '\n'.join('\t'.join(map(str, row)) for row in self.state)

    def get_number(self, row: int, col: int):
        """Devolve o valor na respetiva posição do tabuleiro."""
        
        if row < 0 or col < 0 or row >= self.size or col >= self.size:
            return None
        return self.state[row][col]

    def put_number(self, value: int, row: int, col: int):
        """Altera o estado para ter em consideração a acao que recebe"""

        self.state[row][col] = value

        if value == 1: self.col_ones[col] += 1; self.row_ones[row] += 1
        else: self.col_zeros[col] += 1; self.row_zeros[row] += 1

    def adjacent_vertical_numbers(self, row: int, col: int):
        """Devolve os valores imediatamente abaixo e acima,
        respectivamente."""
        
        return (self.get_number(row+1, col), self.get_number(row-1, col))

    def adjacent_horizontal_numbers(self, row: int, col: int):
        """Devolve os valores imediatamente à esquerda e à direita,
        respectivamente."""
        
        return (self.get_number(row, col-1), self.get_number(row, col+1))
    
    def check_adjacent_numbers(self, values: list, r: int, c: int):
        """Devolve se ação dada resulta num estado com 3 posições
        seguidas com o mesmo valor."""

        actions = []
        for v in values:
            # if it generates an invalid state continues to next value
            if self.adjacent_horizontal_numbers(r, c) == (v, v) \
                or (self.get_number(r+1, c) == v and self.get_number(r+2, c) == v) \
                or (self.get_number(r-1, c) == v and self.get_number(r-2, c) == v) \
                or self.adjacent_vertical_numbers(r, c) == (v, v) \
                or (self.get_number(r, c+1) == v and self.get_number(r, c+2) == v) \
                or (self.get_number(r, c-1) == v and self.get_number(r, c-2) == v):
                continue
            else: actions.append(v)
        return actions

    def check_limits(self, values: list, r: int, c: int):
        """Devolve se ação dada resulta num estado que tenha
        valores diferentes de uns e zeros."""
        
        actions = []
        for v in values:
            # initialize values          
            row_zeros, row_ones = self.row_zeros[r], self.row_ones[r]
            col_zeros, col_ones = self.col_zeros[c], self.col_ones[c]

            # update according to action
            if v == 0: row_zeros += 1; col_zeros += 1
            if v == 1: row_ones += 1; col_ones += 1

            # if it generates an invalid state continues to next value
            if (row_zeros > self.limit) or (col_zeros > self.limit) or \
                (row_ones > self.limit) or (col_ones > self.limit):
                continue
            else: actions.append(v)
        return actions

    def check_duplicate_lines(self, values: list, r: int, c: int):
        """Devolve se ação dada resulta num estado que tenha linhas iguais."""

        actions = []
        for v in values:
            # create temporary state
            temp_state = [[x for x in r] for r in self.state]
            temp_state[r][c] = v

            # if altered line still incomplete ignore
            if 2 in temp_state[r]:
                actions.append(v)
                continue

            # check that alteration does not cause duplicate lines
            if temp_state.count(temp_state[r]) == 1:
                actions.append(v)
        return actions

    def check_duplicate_columms(self, values: list, r: int, c: int):
        """Devolve se ação dada resulta num estado que tenha colunas iguais."""

        actions = []
        for v in values:
            # create temporary state
            temp_state = [[x for x in r] for r in self.state]
            temp_state[r][c] = v

            # transpose
            temp_state = list(zip(*temp_state))

            # if altered columm still incomplete ignore
            if 2 in temp_state[c]:
                actions.append(v)
                continue

            # check that alteration does not cause duplicate columms
            if temp_state.count(temp_state[c]) == 1:
                actions.append(v)
        return actions

    def validate_action(self, v: list, r: int, c: int):
        """Devolve se ação dada resulta num estado válido."""

        v = self.check_adjacent_numbers(v, r, c)
        if not v: return v

        v = self.check_limits(v, r, c)
        if not v: return v

        v = self.check_duplicate_lines(v, r, c)
        if not v: return v

        v = self.check_duplicate_columms(v, r, c)
        return v

    @staticmethod
    def parse_instance_from_stdin():
        """Lê o test do standard input (stdin) que é passado como argumento
        e retorna uma instância da classe Board."""
        
        # read Board size
        size = int(stdin.readline())
        
        # limit for if it is even or odd
        if size%2 == 0: limit = size//2
        else: limit = size//2 + 1

        # read Board and initialize counters
        state = []
        col_ones = []; col_zeros = []
        row_ones = []; row_zeros = []
        for _ in range(size):
            col_zeros.append(0); row_zeros.append(0)
            col_ones.append(0); row_ones.append(0)
            
            board = stdin.readline().split()
            row = list(map(int, board))
            state.append(row)

        # Update counters
        for r in range(size):
            for c in range(size):
                if state[r][c] == 0: col_zeros[c] += 1; row_zeros[r] += 1
                if state[r][c] == 1: col_ones[c] += 1; row_ones[r] += 1
        
        return Board(size, limit, col_ones, col_zeros, row_ones, row_zeros, state)


class Takuzu(Problem):
    def __init__(self, board: Board):
        """O construtor especifica o estado inicial."""
        self.initial = TakuzuState(board)

    def actions(self, state: TakuzuState):
        """Retorna uma lista de ações que podem ser executadas a
        partir do estado passado como argumento."""

        actions = []
        for r in range(state.board.size):
            for c in range(state.board.size):
                if state.board.get_number(r, c) == 2:
                    position_actions = state.board.validate_action([0, 1], r, c)

                    if len(position_actions) == 1:
                        return [(position_actions[0], r, c)]

                    # if already found 2 possible actions in an erlier position no update
                    if not actions and len(position_actions) == 2:
                        actions = [(0, r, c), (1, r, c)]
        return actions

    def result(self, state: TakuzuState, action):
        """Retorna o estado resultante de executar a 'action' sobre
        'state' passado como argumento. A ação a executar deve ser uma
        das presentes na lista obtida pela execução de
        self.actions(state)."""
        
        # Creates temp state since the actual state can only be changed once sure which action to choose
        board = Board(state.board.size, state.board.limit,
                    [v for v in state.board.col_ones], [v for v in state.board.col_zeros],
                    [v for v in state.board.row_ones], [v for v in state.board.row_zeros],
                    [[x for x in r] for r in state.board.state])
        
        temp_state = TakuzuState(board)
        val, row, col = action
        temp_state.board.put_number(val, row, col)
        return temp_state

    def goal_test(self, state: TakuzuState):
        """Retorna True se e só se o estado passado como argumento é
        um estado objetivo. Deve verificar se todas as posições do tabuleiro
        estão preenchidas com uma sequência de números adjacentes."""

        # check board is complete
        for x in state.board.state:
            if 2 in x: return False            
        return True

    def h(self, node: Node):
        """Função heuristica utilizada para a procura A*.
        A heuristica de cada estado corresponde ao numero de ações possíveis que se podem executar.
        Quanto menor o valor mais certeza se tem (apenas uma ação possivel em muitas posições),
        e mais perto estamos de preencher o tabuleiro"""

        # initializes the total value of actions possible at 0
        actions_h = 0
        for r in range(node.state.board.size):
            for c in range(node.state.board.size):
                if node.state.board.get_number(r, c) == 2:
                    last_actions = actions_h
                    # adds the number of possible actions for this position
                    actions_h += len(node.state.board.validate_action([0,1], r, c))

                    # if there are no possible actions for any position than the state is impossible
                    if last_actions == actions_h:
                        # returns very high value for the heuristic
                        return 3*node.state.board.size
        return actions_h


if __name__ == "__main__":
    start_time = time.time()

    # Ler o ficheiro do standard input,
    board = Board.parse_instance_from_stdin()

    # Criar uma instância do problema
    problem = Takuzu(board)
    
    # Extrair resultados para relatório
    # compare_searchers(problems=[problem], header=['Searcher', 'Takuzu'])

    # Usar uma técnica de procura para resolver a instância,
    # Retirar a solução a partir do nó resultante,
    goal_node = depth_first_tree_search(problem)
    # goal_node = astar_search(problem)

    # Imprimir para o standard output no formato indicado.
    print(goal_node.state.board, sep="")
    # print("%s" % (time.time() - start_time))
