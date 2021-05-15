#numero 93734 _ Maria Beatriz Machado Romao Venceslau

#celula
def cria_celula(valor):
    """
    retorna uma celula, composta por uma lista com o valor que recebe
    :param valor:
    :return [valor]:
    """
    if type(valor)==int and valor in (-1,0,1):
        return [valor]
    raise ValueError('cria_celula: argumento invalido.')

def obter_valor(valor):
    """
    retorna o valor contido na celula que recebe
    :param valor:
    :return valor[0]:
    """
    if eh_celula(valor):
        return valor[0]
    raise ValueError('obter_valor: argumento invalido')

def inverte_estado(valor):
    """
    retorna o valor invertido da celula (1 passa a 0 e 0 passa a 1)
    se for -1 nao faz nada
    :param valor:
    :return valor:
    """
    if eh_celula(valor):
        if obter_valor(valor)==1:
            valor[0]=obter_valor(cria_celula(0))
        elif obter_valor(valor)==0:
            valor[0]=obter_valor(cria_celula(1))
        return valor

def eh_celula(valor):
    """
    Verifica se é celula ou nao
    :param valor:
    :return booleano:
    """
    return type(valor)==list and len(valor)==1 and valor[0] in (-1,0,1)

def celulas_iguais(valor1, valor2):
    """
    Verifica se os valores recebidos sao iguais
    :param valor1:
    :param valor2:
    :return booleano:
    """
    if not eh_celula(valor1) or not eh_celula(valor2):
        return False
    else:
        return valor1 == valor2

def celula_para_str(valor):
    """
    retorna a celula em formato de string
    se for 0 ou 1 retorna o valor em string
    se for -1 retorna 'x'
    :param valor:
    :return string:
    """
    if eh_celula(valor):
        if obter_valor(valor) in (0,1):
            return str(obter_valor(valor))
        return 'x'

#coordenada
def cria_coordenada(linha,coluna):
    """
    retorna a coordenada como um tuplo(linha,coluna)
    :param linha:
    :param coluna:
    :return coordenada:
    """
    if type(linha)==int and linha in (0, 1, 2) and\
        type(coluna)==int and coluna in (0, 1, 2):
        return (linha,coluna)
    raise ValueError('cria_coordenada: argumentos invalidos.')

def coordenada_linha(coordenada):
    """
    retorna o valor da linha da coordenada que recebe
    :param coordenada:
    :return coordenada_linha:
    """
    return coordenada[0]

def coordenada_coluna(coordenada):
    """
    retorna o valor da coluna da coordenada que recebe
    :param coordenada:
    :return coordenada_coluna:
    """
    return coordenada[1]

def eh_coordenada(coordenada):
    """
    verifica se e coordenada valida
    :param coordenada:
    :return booleano:
    """
    return type(coordenada)==tuple and len(coordenada)==2 and\
           type(coordenada_linha(coordenada))==int and coordenada_linha(coordenada) in (0, 1, 2) and\
           type(coordenada_coluna(coordenada))==int and coordenada_coluna(coordenada) in (0, 1, 2)

def coordenadas_iguais(coordenada1, coordenada2):
    """
    verifica se sao coordenadas iguais
    :param coordenada1:
    :param coordenada2:
    :return booleano:
    """
    if not eh_coordenada(coordenada1) or not eh_coordenada(coordenada2):
        return False
    return coordenada1 == coordenada2

def coordenada_para_str(coordenada):
    """
    retorna a coordenada que recebe no formato de string
    :param coordenada:
    :return string:
    """
    return str(coordenada)

#tabuleiro
def tabuleiro_inicial():
    """
    retorna o tabuleiro inicial no tipo dicionario
    no qual as chaves sao as coordenadas e os valores sao as celulas
    :return tabuleiro_inicial:
    """
    tabuleiro_inicial = {}
    celulas= [[-1],[-1],[-1],[0],[0],[-1],[0],[-1]]          # lista das celulas que compoem o tabuleiro inicial
    coordenadas=[cria_coordenada(linha,coluna) for linha in (0,1,2) for coluna in (0,1,2)]
    coordenadas.remove((2,0))                                # remove coordenada para formatar de acordo com o tabuleiro valido
    for i in range(8):
        tabuleiro_inicial[coordenadas[i]] = celulas[i]
    return tabuleiro_inicial

def str_para_tabuleiro(string):
    """
    retorna o tabuleiro, em formato de dicionario, a partir da string que recebe
    transforma entao uma string valida em tabuleiro
    :param string:
    :return tabuleiro:
    """
    if type(string)!=str:
        raise ValueError('str_para_tabuleiro: argumento invalido.')
    tabuleiro = eval(string)
    if not (type(tabuleiro) == tuple and len(tabuleiro) == 3 and
           all(type(parcela) == tuple for parcela in tabuleiro) and
           all(len(parcela) == 2 if i == 2 else len(parcela) == 3 for i, parcela in enumerate(tabuleiro)) and
           all((valor in (0, 1, -1) for parcela in tabuleiro for valor in parcela))):
        raise ValueError('str_para_tabuleiro: argumento invalido.')
    celulas=[]
    for i in range(len(tabuleiro)):
        for j in range(len(tabuleiro[i])):
            celula=cria_celula(tabuleiro[i][j])
            celulas+=[celula]
    coordenadas=[cria_coordenada(linha, coluna) for linha in (0, 1, 2) for coluna in (0, 1, 2)]
    coordenadas.remove((2, 0))
    tabuleiro={}
    for k in range(8):
        tabuleiro[coordenadas[k]]=celulas[k]
    return tabuleiro

def tabuleiro_dimensao(tabuleiro):
    """
    retorna a dimensao de um tabuleiro valido (estaticamente definida como 3)
    :param tabuleiro:
    :return inteiro:
    """
    return 3

def tabuleiro_celula(tabuleiro,coordenada):
    """
    retorna a celula associada a coordenada que recebe, no tabuleiro que recebe
    :param tabuleiro:
    :param coordenada:
    :return celula:
    """
    return tabuleiro[(coordenada_linha(coordenada),coordenada_coluna(coordenada))]

def tabuleiro_substitui_celula(tabuleiro,celula,coordenada):
    """
    retorna o tabuleiro, em formato de dicionario, no qual subtitui na coordenada
    recebida a celula original pela celula que recebe
    :param tabuleiro:
    :param celula:
    :param coordenada:
    :return tabuleiro:
    """
    if not (eh_tabuleiro(tabuleiro) and eh_celula(celula) and eh_coordenada(coordenada)):
        raise ValueError('tabuleiro_substitui_celula: argumento invalido')
    tabuleiro[(coordenada_linha(coordenada),coordenada_coluna(coordenada))]=celula
    return tabuleiro

def tabuleiro_inverte_estado(tabuleiro,coordenada):
    """
    retorna o tabuleiro, em formato de dicionario, que resulta de inverter o estado da celula
    presente na coordenada que recebe
    :param tabuleiro:
    :param coordenada:
    :return tabuleiro:
    """
    if not (eh_tabuleiro(tabuleiro) and eh_coordenada(coordenada)):
        raise ValueError('tabuleiro_inverte_estado: argumentos invalidos.')
    return tabuleiro_substitui_celula(tabuleiro,inverte_estado(tabuleiro_celula(tabuleiro,coordenada)),coordenada)

def eh_tabuleiro(tabuleiro):
    """
    Valida o tabuleiro
    :param tabuleiro:
    :return booleano:
    """
    if not (type(tabuleiro)==dict and tabuleiro_dimensao(tabuleiro)==3):
        return False
    for coordenada in tabuleiro:
        if eh_coordenada(coordenada):
            return eh_celula(tabuleiro[coordenada])
        return False

def tabuleiros_iguais(tab1,tab2):
    """
    Valida a igualdade de dois tabuleiros
    :param tab1:
    :param tab2:
    :return booleano:
    """
    if not (eh_tabuleiro(tab1) and eh_tabuleiro(tab2)):
        return False
    return tab1 == tab2

def tabuleiro_para_str(tabuleiro):
    """
    retorna o tabuleiro em formato compreensivel aos olhos do utilizador
    :param tabuleiro:
    :return string:
    """
    if not eh_tabuleiro(tabuleiro):
        raise ValueError('tabuleiro_para_str: argumento invalido')
    tab =  '+-------+\n'
    tab += '|...{}...|\n'.format(celula_para_str(tabuleiro_celula(tabuleiro,(0,2))))
    tab += '|..{}.{}..|\n'.format(celula_para_str(tabuleiro_celula(tabuleiro,(0,1))),celula_para_str(tabuleiro_celula(tabuleiro,(1,2))))
    tab += '|.{}.{}.{}.|\n'.format(celula_para_str(tabuleiro_celula(tabuleiro,(0,0))),celula_para_str(tabuleiro_celula(tabuleiro,(1,1))),celula_para_str(tabuleiro_celula(tabuleiro,(2,2))))
    tab += '|..{}.{}..|\n'.format(celula_para_str(tabuleiro_celula(tabuleiro,(1,0))),celula_para_str(tabuleiro_celula(tabuleiro,(2,1))))
    tab += '+-------+'
    return tab

#portas
def porta_x(tabuleiro,lado):
    """
    retorna o tabuleiro em dicionario transformado por acao da porta_x no lado que recebe
    tendo as celulas sido invertidas consoante a sua coordenada
    :param tabuleiro:
    :param lado:
    :return tabuleiro:
    """
    if not eh_tabuleiro(tabuleiro) or lado not in ('D', 'E'):
        raise ValueError('porta_x: argumentos invalidos.')
    elif lado=='D':
        tabuleiro=tabuleiro_inverte_estado(tabuleiro,cria_coordenada(0,1))
        tabuleiro=tabuleiro_inverte_estado(tabuleiro,cria_coordenada(1, 1))
        tabuleiro=tabuleiro_inverte_estado(tabuleiro,cria_coordenada(2, 1))
    else:
        tabuleiro=tabuleiro_inverte_estado(tabuleiro,cria_coordenada(1,0))
        tabuleiro=tabuleiro_inverte_estado(tabuleiro,cria_coordenada(1, 1))
        tabuleiro=tabuleiro_inverte_estado(tabuleiro,cria_coordenada(1, 2))
    return tabuleiro

def porta_z(tabuleiro,lado):
    """
    retorna o tabuleiro em dicionario taransformado por acao da porta_z no lado que recebe
    tendo as celulas sido invertidas consoante a sua coordenada
    :param tabuleiro:
    :param lado:
    :return tabuleiro:
    """
    if not eh_tabuleiro(tabuleiro) or lado not in ('D', 'E'):
        raise ValueError('porta_z: argumentos invalidos.')
    elif lado=='D':
        tabuleiro=tabuleiro_inverte_estado(tabuleiro,cria_coordenada(0,2))
        tabuleiro=tabuleiro_inverte_estado(tabuleiro,cria_coordenada(1,2))
        tabuleiro=tabuleiro_inverte_estado(tabuleiro,cria_coordenada(2,2))
    else:
        tabuleiro=tabuleiro_inverte_estado(tabuleiro,cria_coordenada(0,2))
        tabuleiro=tabuleiro_inverte_estado(tabuleiro,cria_coordenada(0,1))
        tabuleiro=tabuleiro_inverte_estado(tabuleiro,cria_coordenada(0,0))
    return tabuleiro

def porta_h(tabuleiro,lado):
    """
    retorna o tabuleiro em dicionario taransformado por acao da porta_h
    tendo as celulas sido trocadas entre si consoante o lado que recebe
    :param tabuleiro:
    :param lado:
    :return tabuleiro:
    """
    if not eh_tabuleiro(tabuleiro) or lado not in ('D', 'E'):
        raise ValueError('porta_h: argumentos invalidos.')
    else:
        if lado=='D':
            for linha in (0,1,2):
                coor_temp=tabuleiro_celula(tabuleiro,cria_coordenada(linha,1))
                cel_subtituir=tabuleiro_celula(tabuleiro, cria_coordenada(linha, 2))
                tabuleiro=tabuleiro_substitui_celula(tabuleiro,cel_subtituir,cria_coordenada(linha,1))
                tabuleiro=tabuleiro_substitui_celula(tabuleiro,coor_temp,cria_coordenada(linha,2))
        else:
            for coluna in (0,1,2):
                coor_temp=tabuleiro_celula(tabuleiro,cria_coordenada(0,coluna))
                cel_subtituir=tabuleiro_celula(tabuleiro, cria_coordenada(1,coluna))
                tabuleiro=tabuleiro_substitui_celula(tabuleiro,cel_subtituir,cria_coordenada(0,coluna))
                tabuleiro=tabuleiro_substitui_celula(tabuleiro,coor_temp,cria_coordenada(1,coluna))
        return tabuleiro

#jogo
def hello_quantum(string):
    """
    permite jogar o jogo hello_quantum a partir de uma string que contem
    uma string do tabuleiro objetivo e o num maximo de jogadas que o utilizador pode usar
    pede ao utilizador um input da porta e lado que deseja executar de modo a alcancar
    o tabuleiro objetivo partindo do tabuleiro_inicial
    retorna True se o utilizador conseguir completar a tarefa dentro das jogadas possiveis
    :param string:
    :return tabuleiro x string x booleano:
    """
    num_jogadas = 0
    tabuleiro=tabuleiro_inicial()
    tabuleiro_objetivo_str,max_jogadas_str=string.split(':')
    tabuleiro_objetivo=str_para_tabuleiro(tabuleiro_objetivo_str)
    max_jogadas=eval(max_jogadas_str)

    print('Bem-vindo ao Hello Quantum!\nO seu objetivo e chegar ao tabuleiro:')
    print(tabuleiro_para_str(tabuleiro_objetivo))
    print('Comecando com o tabuleiro que se segue:')
    print(tabuleiro_para_str(tabuleiro))

    while not (tabuleiros_iguais(tabuleiro, tabuleiro_objetivo)):
        if num_jogadas == max_jogadas:
            return False
        else:
            num_jogadas += 1
            porta=str(input('Escolha uma porta para aplicar (X, Z ou H): '))
            lado=input('Escolha um qubit para analisar (E ou D): ')
            if porta=='X':
                tabuleiro=porta_x(tabuleiro,lado)
            elif porta=='Z':
                tabuleiro=porta_z(tabuleiro,lado)
            elif porta=='H':
                tabuleiro=porta_h(tabuleiro,lado)
            else:
                raise ValueError('hello_quantum: argumentos invalidos')
            print(tabuleiro_para_str(tabuleiro))
    print(('Parabens, conseguiu converter o tabuleiro em {} jogadas!').format(str(num_jogadas)))
    return True
