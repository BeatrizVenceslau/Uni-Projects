#numero 93734 _ Maria Beatriz Machado Romao Venceslau

def eh_tabuleiro(t):
    """
    recebe um argumento de qualquer tipo e devolve True se cumprir os requisitos de tabuleiro e False caso contrário
    """
    if (type(t) != tuple or len(t) != 3):                       #se for um tuplo e tiver 3 parcelas
        return False
    else:
        for i in range(len(t)):
            if (type(t[i]) == tuple and len(t[i])>0):           #se cada parcela for um tuplo
                for j in range(len(t[i])):
                    if type(t[i][j])==int:                      #se os elementos de cada tuplo forem inteiros
                        if (i < 2 and len(t[i]) != 3) or (i == 2 and len(t[2]) != 2):
                            return False            #verifica se 2 primeiros tuplos tem 3 elementos e o segundo tem 2
                        else:
                            if not ((t[i][j] == -1) or (t[i][j] == 0) or (t[i][j] == 1)):
                                return False                    #se houver elementos diferentes de 1, 0, -1
                    else:
                        return False
            else:
                return False
        return True

def tabuleiro_str(t):
    """
    recebe um tabuleiro e devolve a cadeia de caracteres que o representa de forma facil para o utilizador
    """
    if eh_tabuleiro(t)==True:                                   #verifica se recebeu um tabuleiro valido
        t_aux=()
        for i in range(len(t)):
            for j in range(len(t[i])):
                if t[i][j]!=-1:                                 #verifica se há elementos difertentes de -1
                    t_aux=t_aux+(t[i][j],)
                else:                                           #substitui elementos iguais a -1 por x
                    t_aux = t_aux+('x',)
        tab_aux='+-------+\n'                                   #compipla a string
        tab_aux=tab_aux+'|...' + str(t_aux[2])+'...|\n'
        tab_aux=tab_aux+'|..'+str(t_aux[1])+'.'+str(t_aux[5])+'..|\n'
        tab_aux=tab_aux+'|.'+str(t_aux[0])+'.'+str(t_aux[4])+'.'+str(t_aux[7]) + '.|\n'
        tab_aux=tab_aux+'|..' + str(t_aux[3]) + '.' + str(t_aux[6]) + '..|\n'
        tab_aux=tab_aux+'+-------+'

        return tab_aux                                          #retorna tabuleiro na string tab_aux
    else:
        raise ValueError('tabuleiro_str: argumento invalido')

def tabuleiros_iguais(t1,t2):
    """
    recebe dois tabuleiros e devolve True se os tabuleiros são iguais e False caso contrário
    se algum dos argumentos não for um tabuleiro valido retorna erro
    """
    if eh_tabuleiro(t1)==True:
        if eh_tabuleiro(t2)==True:                              #verifica validade dos tabuleiros
            if t1==t2:
                return True                                     #verifica igualdade dos tabuleiros
            else:
                return False
        else:
            raise ValueError('tabuleiros_iguais: um dos argumentos nao e tabuleiro')
    else:
        raise ValueError('tabuleiros_iguais: um dos argumentos nao e tabuleiro')

def porta_x(t,lado):
    """
    recebe um tabuleiro e um carácter 'E' ou 'D' e devolve um novo tabuleiro resultante da aplicacao da 'porta_x'
    ao tabuleiro pelo lado esquerdo e direito respetivamente negando os elementos
    """
    if eh_tabuleiro(t)==True:                                       #valida tabuleiro
        if lado==('E') or lado==('D'):
            return porta_xz(t, lado, 'x')                           #troca os elementos
        else:
            raise ValueError('porta_x: um dos argumentos e invalido')
    else:
        raise ValueError('porta_x: um dos argumentos e invalido')

def porta_z(t,lado):
    """
    recebe um tabuleiro e um carácter 'E' ou 'D' e devolve um novo tabuleiro resultante da aplicacao da 'porta_z'
    ao tabuleiro pelo lado esquerdo ou direito respetivamente negando os elementos
    """
    if eh_tabuleiro(t)==True:                                       #valida tabuleiro
        if lado==('E') or lado==('D'):
            return porta_xz(t,lado,'z')                             #troca os elementos
        else:
            raise ValueError('porta_z: um dos argumentos e invalido')
    else:
        raise ValueError('porta_z: um dos argumentos e invalido')

def porta_xz(t,lado,porta):
    """
    recebe o tuplo o lado e a porta e aplica as funcoes necessarias
    """
    if lado=='E':                                                       #verifica lado
        if porta=='x':
            return troca_e(t, 'x')                                      #troca elementos porta x lado E
        else:
            return troca_e(t,'z')                                       #troca elementos porta z lado E
    else:
        if porta == 'x':
            return troca_d(t, 'x')                                      #troca elementos porta x lado D
        else:
            return troca_d(t, 'z')                                      #troca elementos porta z lado D

def troca_e(t,porta):
    """
    recebe o tuplo e a porta e realiza as trocas da esquerda
    """
    a = ()
    b = ()
    for i in range(len(t)):
        for j in range(len(t[i])):
            if porta=='x':
                if i == 1:                                              #troca 1 para 0 e viceversa no segundo tuplo
                    if t[i][j] == 1:
                        b = b + (0,)
                    elif t[i][j] == 0:
                        b = b + (1,)
                    else:
                        b = b + (-1,)
                if i == 0:
                    a=t[0]
            else:
                if i == 0:                                              #troca 1 para 0 e viceversa no primeiro tuplo
                    if t[i][j] == 1:
                        a = a + (0,)
                    elif t[i][j] == 0:
                        a = a + (1,)
                    else:
                        a = a + (-1,)
                if i==1:
                    b=t[1]
    return ((a,)+(b,)+(t[2],))

def troca_d(t, porta):
    """
    recebe o tuplo e a porta e realiza as trocas da direita
    """
    a = ()
    b = ()
    if porta == 'x':                                    #porta x
        for i in range(len(t)):                         #troca 1 para 0 e viceversa no segundo elemento de cada tuplo
            if t[i][-2] == 1:
                a = a + (0,)
            elif t[i][-2] == 0:
                a = a + (1,)
            else:
                a = a + (-1,)
        tuplo = (((t[0][-3],a[0],t[0][-1]),) + ((t[1][-3],a[1],t[1][-1]),) + ((a[2],t[2][-1]),))
    else:                                                #porta z
        for i in range(len(t)):                          #troca 1 para 0 e viceversa no segundo elemento de cada tuplo
            if t[i][-1] == 1 :
                b = b + (0,)
            elif t[i][-1] == 0:
                b = b + (1,)
            else:
                b = b + (-1,)
        tuplo = (((t[0][-3], t[0][-2], b[0]),) + ((t[1][-3], t[1][-2], b[1]),) + ((t[2][-2], b[2]),))
    return tuplo

def porta_h(t,lado):
    """
    recebe um tabuleiro e um carácter 'E' ou 'D' e devolve um novo tabuleiro resultante de aplicar a 'porta_h'
    ao tabuleiro pelo lado esquerdo ou direito respetivamente, trocando os elementos de posicao
    """
    a=()
    b=()
    c=()
    if lado==('E') or lado==('D'):
        if eh_tabuleiro(t)==True:                               #verifica se é tabuleiro valido e lado E ou D
            for i in range(len(t)):
                for j in range(len(t[i])):
                    if lado=='E':                               #lado E
                        a=t[1]                                  #troca os tuplos com indice 0 e 1 de lugar
                        b=t[0]
                        c=t[2]
                    else:                                       #lado D
                        a=t[0][-3],t[0][-1],t[0][-2]            #troca os elementos da posicao -2 e -1 de lugar
                        b=t[1][-3],t[1][-1],t[1][-2]
                        c=t[2][-1],t[2][-2]
            return ((a,)+(b,)+(c,))
        else:
            raise ValueError('porta_h: um dos argumentos e invalido')
    else:
        raise ValueError('porta_h: um dos argumentos e invalido')
