import xlrd
import sys

#num concelho, num regiao, nome concelho , habitantes
reg0=0,reg1=0,reg2=0,reg3=0,reg4=0
file = ("C:\Projetos3Ano\BD\Projeto\Proj3\DatabasersFlexers\Lista_Concelhos")

colnum_nome_concelho=3
colnum_regiao=1

size=278

wb= xlrd.open_workbook(file)
sheet= wb.sheet_by_index(0);

def createNumRegiao(regiao):
    if regiao == "NORTE":
        code=0
    elif regiao == "CENTRO":
        code=1
    elif regiao == "LISBOA":
        code=2
    elif regiao == "ALENTEJO":
        code=3
    else code =4
    return code

def createNumConc(num_regiao):
    if num_regiao==0:
        return reg0+=1
    elif num_regiao==1:
        return reg1+=1
    elif num_regiao==2:
        return reg2+=1
    elif num_regiao==3:
        return reg3+=1
    else:
        return reg4+=1


for i in range(size):
    regiao = sheet.cell_value(colnum_regiao,i)
    nome =sheet.cell_value(colnum_nome_concelho,i)
    num_regiao= createNumRegiao(regiao)
    numconc= createNumConc(num_regiao)
    numHabitantes=1000
    print("insert into concelho(num_concelho, num_regiao, nome, num_habitantes) values(%d, %d, %s, %d)" %(numconc,num_regiao,nome, num_habitantes));
