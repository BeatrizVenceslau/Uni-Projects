% ist1 93734 -  Maria Beatriz Venceslau - Taguspark
:-[codigo_comum].

%--------------------------------------------------------------------------------------------------

%aplica_R1_triplo(Triplo, N_Triplo), em que Triplo e uma lista de 3 elementos (0, 1 ou variavel).
%Significa que N_Triplo e a lista resultante de aplicar R1 ao Triplo.
%Se o Triplo tiver 2 zeros/uns e uma variavel, esta deve ser substituida por um/zero.
%Se o Triplo tiver 3 zeros/uns o predicado deve devolver false.
aplica_R1_triplo(Triplo, Triplo) :- 
    conta_var(Triplo, Cont_v), 
    between(2, 3, Cont_v).

aplica_R1_triplo(Triplo, Triplo) :- 
    conta_var(Triplo, Cont_v), 
    Cont_v=:=1,
    conta_el(0, Triplo, Cont_el), 
    Cont_el=:=1.

aplica_R1_triplo(Triplo, Triplo) :- 
    conta_var(Triplo, Cont_v), 
    Cont_v=:=0,
    conta_el(1, Triplo, Cont_el), 
    between(1, 2, Cont_el).

aplica_R1_triplo(Triplo, Triplo) :- 
    conta_var(Triplo, Cont_v), 
    Cont_v=:=0,
    conta_el(0, Triplo, Cont_el), 
    between(1, 2, Cont_el).

aplica_R1_triplo(Triplo, Lout) :- 
    conta_var(Triplo, Cont_v), 
    Cont_v=:=1,
    conta_el(0, Triplo, Cont_el), 
    between(2, 3, Cont_el),
    altera_valor(1, Triplo, Lout).

aplica_R1_triplo(Triplo, Lout) :- 
    conta_var(Triplo, Cont_v), 
    Cont_v=:=1,
    conta_el(1, Triplo, Cont_el), 
    between(2, 3, Cont_el),
    altera_valor(0, Triplo, Lout).

%conta_var(Lin, Cont_v), em que Lin e a lista de input e Cont_v e o contador 
%de elementos que sao variaveis.
conta_var(Lin, Cont_v) :- 
    findall(X, (member(X, Lin), var(X)), List_var), 
    length(List_var, Cont_v).

%conta_el(N, Lin, Cont_el), em que N e um inteiro (1 ou 0), Lin a lista de input 
%e Cont_el o contador de elementos com valor igual a N.
conta_el(N, Lin, Cont_el) :- 
    findall(X, (member(X, Lin), X==N), List_el), 
    length(List_el, Cont_el).

%altera_valor(N, Lin, Lout), em que N (0 ou 1) e o valor que substitui 
%a variavel em Lin e Lout e a lista alterada.
altera_valor(_, [], []).
altera_valor(N, [P|R], [N|RLout]) :- 
    var(P), 
    altera_valor(N, R, RLout).
altera_valor(N, [P|R], [P|RLout]) :- 
    not(var(P)), 
    altera_valor(N, R, RLout).

%--------------------------------------------------------------------------------------------------

%aplica_R1_fila_aux(Fila, N_Fila), em que Fila e uma linha/coluna do puzzle.
%Significa que N_Fila e a fila resultante de aplicar R1 a Fila uma so vez,
%do inicio ao fim a cada sub-conjunto de 3 elementos.
%Se a Fila tiver 3 zeros/uns seguidos, o predicado deve devolver false.
aplica_R1_fila_aux(Fila, N1_Fila) :- 
    aplica_R1_fila_aux(Fila, N_Fila, []), 
    reverse(N_Fila, N1_Fila).

aplica_R1_fila_aux([E1, E2, E3], Res_N, Res) :- 
    aplica_R1_triplo([E1, E2, E3], R), 
    reverse(R, N_R), 
    append(N_R, Res, Res_N).

aplica_R1_fila_aux(Fila, N_Fila, Res) :- 
    altera_fila(Fila, [P|Rf_alterada]), !,
    aplica_R1_fila_aux(Rf_alterada, N_Fila, [P|Res]).

%altera_fila(Fila, Fila_alterada) aplica R1 a cada triplo da Fila.
altera_fila(Fila, Fila_alterada) :- 
    cria_triplo(Fila, Triplo_N), 
    cria_resto_lista(Fila, Resto),
    aplica_R1_triplo(Triplo_N, N_triplo),
    append(N_triplo, Resto, Fila_alterada).

%cria_triplo(Lin, Triplo_N) cria uma lista Triplo_N que contem os primeiros 3 elementos da Lin.
cria_triplo([E1, E2, E3|_], [E1, E2, E3]).

%cria_resto_lista(Lin, Resto) cria uma lista Resto que contem todos os 
%elementos da Lin excepto os primeiros 3.
cria_resto_lista([_, _, _|Rlista], Rlista).

%--------------------------------------------------------------------------------------------------

%aplica_R1_fila(Fila, N_Fila), em que N_Fila e a lista resultante de aplicar aplica_R1_fila_aux 
%a Fila, ate que todas as variaveis de Fila que possam ser substituidas, 
%respeitando R1, estejam preenchidas.
aplica_R1_fila(Fila, N_Fila) :- 
    aplica_R1_fila_aux(Fila, N_Fila_aux),
    Fila==N_Fila_aux, !,
    N_Fila=N_Fila_aux.

aplica_R1_fila(Fila, N_Fila) :- 
    aplica_R1_fila_aux(Fila, N_Fila_aux),
    Fila\==N_Fila_aux, !,
    aplica_R1_fila(N_Fila_aux, N_Fila).

%--------------------------------------------------------------------------------------------------

%aplica_R2_fila(Fila, N_Fila), em que Fila e uma linha/coluna de um puzzle.
%Seja N metade do numero de elementos de Fila.
%R2 implica que se a Fila ja tiver N zeros/uns, todas as outras posicoes de Fila,
%devem ser prenchidas por uns/zeros.
%Se o numero de zeros/uns ultrapassar N, o predicado deve devolver false.
aplica_R2_fila(Fila, _) :- 
    meio_comp_fila(Fila, MComp), 
    conta_el(0, Fila, Conta_el),
    MComp<Conta_el, !, fail.

aplica_R2_fila(Fila, _) :- 
    meio_comp_fila(Fila, MComp), 
    conta_el(1, Fila, Conta_el),
    MComp<Conta_el, !, fail.

aplica_R2_fila(Fila, N_Fila) :- 
    meio_comp_fila(Fila, MComp), 
    conta_el(0, Fila, Conta_el),
    MComp=:=Conta_el, !, 
    altera_valor(1, Fila, N_Fila).

aplica_R2_fila(Fila, N_Fila) :- 
    meio_comp_fila(Fila, MComp), 
    conta_el(1, Fila, Conta_el),
    MComp=:=Conta_el, !,
    altera_valor(0, Fila, N_Fila).

aplica_R2_fila(Fila, N_Fila) :- 
    meio_comp_fila(Fila, MComp), 
    conta_el(0, Fila, Conta_el),
    MComp>Conta_el, !,
    N_Fila=Fila.

aplica_R2_fila(Fila, N_Fila) :- 
    meio_comp_fila(Fila, MComp), 
    conta_el(1, Fila, Conta_el),
    MComp>Conta_el, 
    N_Fila=Fila.

%meio_comp_fila(Fila, MComp) em que MComp e metade do comprimento que a Fila tem.
meio_comp_fila(Fila, MComp) :- 
    length(Fila, Comp_aux), 
    MComp=div(Comp_aux,2).

%--------------------------------------------------------------------------------------------------

%aplica_R1_R2_fila(Fila, N_Fila), em que Fila e uma linha/coluna de um puzzle.
%Significa que N_Fila e a fila resultante de aplicar R1 e R2 a Fila, por esta ordem.
aplica_R1_R2_fila(Fila, N_Fila) :- 
    aplica_R1_fila(Fila, Fila_aux), 
    aplica_R2_fila(Fila_aux, N_Fila).

%--------------------------------------------------------------------------------------------------

%aplica_R1_R2_puzzle(Puz, N_Puz), em que Puz e um puzzle.
%Significa que N_Puz e o puz resultante de aplicar o predicado aplica_R1_R2_fila,
%as linhas e colunas de Puz, por esta ordem.
aplica_R1_R2_puzzle(Puz, N_Puz) :- 
    aplica_R1_R2_fila_puzzle(Puz, N_Puz_Lin),
    mat_transposta(N_Puz_Lin, Puz_Col),
    aplica_R1_R2_fila_puzzle(Puz_Col, N_Puz_Col),
    mat_transposta(N_Puz_Col, N_Puz).

%aplica_R1_R2_fila_puzzle(Puz, N_Puz) em que N_Puz e o puzzle resultante de aplicar
%aplica_R1_R2_fila a cada uma das linhas de Puz.
aplica_R1_R2_fila_puzzle(Puz, N_Puz) :- 
    aplica_R1_R2_fila_puzzle(Puz, N_Puz, []).

aplica_R1_R2_fila_puzzle([], Res, Res).

aplica_R1_R2_fila_puzzle([P|R], N_Puz, Res) :- 
    aplica_R1_R2_fila(P, N_Linha),
    append(Res, [N_Linha], Res_aux),
    aplica_R1_R2_fila_puzzle(R, N_Puz, Res_aux).

%--------------------------------------------------------------------------------------------------

%inicializa(Puz, N_Puz), em que Puz e um puzzle.
%Significa que N_Puz e o puz reultante de inicializar Puz.
inicializa(Puz, N_Puz) :- 
    aplica_R1_R2_puzzle(Puz, N_Puz_aux),
    Puz\==N_Puz_aux, !,
    inicializa(N_Puz_aux, N_Puz).

inicializa(Puz, Puz).

%--------------------------------------------------------------------------------------------------

%verifica_R3(Puz) significa que no puzzle Puz todas as linhas sao diferentes entre si
%e todas as colunas sao diferentes entre si.
verifica_R3(Puz) :-
    mat_transposta(Puz, Puz_T),
    verifica_R3_aux(Puz),
    verifica_R3_aux(Puz_T).

%verifica_R3_aux(Puz) faz as verificacoes necessarias para que a R3 se verifique.
verifica_R3_aux([]).

verifica_R3_aux([P|R]) :- 
    conta_var(P, Cont_v), 
    Cont_v==0, !,
    linhas_dif([P|R]),
    verifica_R3_aux(R).

verifica_R3_aux([_|R]) :-
    verifica_R3_aux(R).

%linhas_dif(Puz) verifica se duas linhas do Puz sao diferentes.
linhas_dif([L1, L2|_]) :- 
    L1\==L2.

%--------------------------------------------------------------------------------------------------

%propaga_posicoes(Posicoes, Puz, N_Puz), em que Posicoes e uma lista de posicoes
%e Puz e um puzzle. Significa que N_Puz e o resultado de propagar, recursivamente,
%(as mudancas de) as posicoes de Posicoes.
propaga_posicoes([], Puz, Puz).

propaga_posicoes([(L, C)|R], Puz, N_Puz) :- 
    posicao_lista(L, Puz, Lin),                  /*Obtem Linha L do puzzle*/
    aplica_R1_R2_fila(Lin, N_Lin),               /*Aplica logica R1 e R2 - Retorna nova linha calculada*/
    mat_muda_linha(Puz, L, N_Lin, N_Puz_Lin),    /*Substiui linha existente pela nova linha determinada por aplica R1 e R2*/
    posicoes_alteradas(Lin, Lin, N_Lin, C_alt),  /*Identifica posicoes alteradas na linha*/
    cria_coor_lin(L, C_alt, Pos_L),              /*Cria tuplo com coordenadas*/

    mat_transposta(N_Puz_Lin, Puz_aux),          /*Transpoe matriz para analise da coluna*/
    
    posicao_lista(C, Puz_aux, Col),              /*Obtem Linha do puzzle correpsondente a coluna C*/
    aplica_R1_R2_fila(Col, N_Col),               /*Aplica logica R1 e R2 - Retorna nova linha calculada corrrespondente a coluna C*/
    mat_muda_linha(Puz_aux, C, N_Col, N_Puz_Col),/*Substiui linha existente pela nova linha determinada por aplica R1 e R2*/
    posicoes_alteradas(Col, Col, N_Col, L_alt),  /*Identifica posicoes alteradas na linha*/
    cria_coor_col(C, L_alt, Pos_C),              /*Cria tuplo com coordenadas corrigidas para corresponder a coluna*/

    mat_transposta(N_Puz_Col, N_Puz_aux),        /*Repoe matriz na posicao original*/
    
    append(Pos_L, Pos_C, N_Pos),                 /*Junta numa lista as coordenadas das posicoes alteradas - linhas e colunas*/
    append(N_Pos, R, Posicoes),                  /*Integra posicoes novas com posicoes ainda nao analizadas*/
    propaga_posicoes(Posicoes, N_Puz_aux, N_Puz). 

%posicao_lista(I, Lista, Conteudo) em que I e o indice do Conteudo na Lista
%e o Conteudo e o elemento que esta no indice I da Lista.
posicao_lista(I, Lista, Conteudo) :- 
    nth1(I, Lista, Conteudo).

%posicoes_alteradas(Fila_O, Fila, N_Fila, L_Coor) em que Fila_O e uma copia da Fila 
%e L_Coor e a lista de coordenadas alteradas na Fila pelo predicado propaga_posicoes.
posicoes_alteradas(Fila_O, Fila, N_Fila, L_Coor) :-
    posicoes_alteradas(Fila_O, Fila, N_Fila, L_Coor, []).

posicoes_alteradas(_, [], [], Res, Res).

posicoes_alteradas(Fila_O, [P|R], [P1|R1], L_Coor, Res) :- 
    var(P), var(P1), !,
    posicoes_alteradas(Fila_O, R, R1, L_Coor, Res).

posicoes_alteradas(Fila_O, [P|R], [P1|R1], L_Coor, Res) :- 
    P==P1, !,
    posicoes_alteradas(Fila_O, R, R1, L_Coor, Res).

posicoes_alteradas(Fila_O, [P|R], [PN|RN], L_Coor, Res) :- 
    var(P), not(var(PN)),
    length(Fila_O, Len_Fila_O),
    length(R, Len_R_Fila),
    Coor is Len_Fila_O-Len_R_Fila,
    append(Res, [Coor], Res_aux),
    posicoes_alteradas(Fila_O, R, RN, L_Coor, Res_aux).

%cria_coor_lin(L, L_Coor, L_Pos) em que L e o indice da linha em que
%as colunas em L_Coor foram alteradas, e L_Pos e a lista de Posicoes.
cria_coor_lin(L, L_Coor, L_Pos) :-
    cria_coor_lin(L, L_Coor, L_Pos, []).

cria_coor_lin(_, [], Res, Res).

cria_coor_lin(L, [P|R], L_Pos, Res) :-
    append([(L, P)], Res, Res_aux),
    cria_coor_lin(L, R, L_Pos, Res_aux).

%cria_coor_col(C, L_Coor, L_Pos) em que C e o indice da coluna em que
%as linhas em L_Coor foram alteradas, e L_Pos e a lista de Posicoes.
cria_coor_col(L, L_Coor, L_Pos) :-
    cria_coor_col(L, L_Coor, L_Pos, []).

cria_coor_col(_, [], Res, Res).

cria_coor_col(C, [P|R], L_Pos, Res) :-
    append([(P, C)], Res, Res_aux),
    cria_coor_col(C, R, L_Pos, Res_aux).

%--------------------------------------------------------------------------------------------------

%resolve(Puz, Sol) significa que o puzzle Sol e (um)a solucao de Puz.
resolve(Puz, N_Puz) :-
    inicializa(Puz, Puz_Inic),
    primeira_var(Puz_Inic, (L, C)),
    resolve_aux(Puz_Inic, N_Puz, (L, C)),
    flatten(N_Puz, L_Puz),
    conta_var(L_Puz, Cont_v),
    Cont_v==0, !.

resolve(Puz, Sol) :- 
    inicializa(Puz, Puz_Inic),
    primeira_var(Puz_Inic, (L, C)),
    resolve_aux(Puz_Inic, N_Puz, (L, C)),
    flatten(N_Puz, L_Puz),
    conta_var(L_Puz, Cont_v),
    Cont_v\==0,
    resolve(N_Puz, Sol).

%resolve_aux(Puz, N_Puz) significa que N_Puz e o reultado de resolver o puzzle Puz.
resolve_aux(Puz, N_Puz, (L, C)) :-
    mat_muda_posicao(Puz, (L, C), 0, N_Puz_aux),
    propaga_posicoes([(L, C)], N_Puz_aux, N_Puz),
    verifica_R3(N_Puz), !.

resolve_aux(Puz, N_Puz, (L, C)) :-
    mat_muda_posicao(Puz, (L, C), 1, N_Puz_aux),
    propaga_posicoes([(L, C)], N_Puz_aux, N_Puz),
    verifica_R3(N_Puz).

%primeira_var(Puz, (L, C)) signifia que a posicao (L, C), e a posicao da primeira
%variavel do puzzle Puz.
primeira_var(Puz, (L, C)) :-
    mat_ref(Puz, (L, C), cont).
