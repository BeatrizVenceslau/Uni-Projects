#!/usr/bin/python3

#TODO---------------------
#--------------------------

from wsgiref.handlers import CGIHandler
from flask import Flask
from flask import render_template, request

## Libs postgres
import psycopg2
import psycopg2.extras

app = Flask(__name__)

## SGBD configs
DB_HOST="db.tecnico.ulisboa.pt"
DB_USER="ist193720" 
DB_DATABASE=DB_USER
DB_PASSWORD="zden5272"
DB_CONNECTION_STRING = "host=%s dbname=%s user=%s password=%s" % (DB_HOST, DB_DATABASE, DB_USER, DB_PASSWORD)


## Runs the function once the root page is requested.
## The request comes with the folder structure setting ~/web as the root
@app.route('/')
def list_options():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    return render_template("index.html", cursor=cursor)
  except Exception as e:
    return str(e) #Renders a page with the error.
  finally:
    cursor.close()
    dbConn.close()



#---------------------------- Instituicao -----------------------------------

@app.route('/listInstituicoes')
def list_intituicoes():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "SELECT * FROM instituicao;"
    cursor.execute(query)
    return render_template("instituicao.html", cursor=cursor, params=request.args)
  except Exception as e:
    return str(e) 
  finally:
    cursor.close()
    dbConn.close()

    
@app.route('/inserirInstituicao',methods=["POST"])
def inserir_instituicao():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "INSERT INTO instituicao(nome, tipo,num_regiao,num_concelho) VALUES(%s,%s,%s,%s);"
    data = (request.form["nome"],request.form["tipo"],request.form["num_regiao"],request.form["num_concelho"])
    cursor.execute(query,data)
    return query
  except Exception as e:
    return str(e)
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()
  
@app.route('/removerInstituicao',methods=["POST"])
def remover_instituicao():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "DELETE FROM instituicao WHERE nome = %s;"
    data = (request.form["nome"])
    cursor.execute(query,data)
    return query
  except Exception as e:
    return str(e)
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()

@app.route('/editarInstituicao', methods=["POST"])
def editar_instituicao():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    # Esta versão é vuneravel a SQL injection
    #query = f'''UPDATE account SET balance={request.form["balance"]} WHERE account_number = '{request.form["account_number"]}';'''
    query = "UPDATE instituicao SET nome = %s, tipo= %s, num_regiao= %s, num_concelho= %s WHERE nome = %s"
    data =(request.form["novo_nome"],request.form["tipo"] , request.form["num_regiao"],request.form["num_concelho"], request.form["nome"])
    cursor.execute(query,data)
    return query
  except Exception as e:
    return str(e) 
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()

#------------------------------ medicos ----------------------------------

@app.route('/listMedicos')
def list_medicos():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "SELECT * FROM medico;"
    cursor.execute(query)
    return render_template("medico.html", cursor=cursor, params=request.args)
  except Exception as e:
    return str(e) 
  finally:
    cursor.close()
    dbConn.close()

    
@app.route('/inserirMedico',methods=["POST"])
def inserir_medico():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "INSERT INTO medico(num_cedula, nome, especialidade) VALUES(%s,%s,%s);"
    data = (request.form["inserir_num_cedula"],request.form["inserir_nome"],request.form["inserir_especialidade"])
    cursor.execute(query,data)
    return query
  except Exception as e:
    return str(e)
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()


# --- Nao funciona
@app.route('/removerMedico',methods=["POST"])
def remover_medico():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "DELETE FROM medico WHERE num_cedula = %s;"
    data = (request.form["remover_num_cedula"])
    cursor.execute(query,data)
    return query
  except Exception as e:
    return str(e)
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()


@app.route('/editarMedico', methods=["POST"])
def editar_medico():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query= "UPDATE medico SET nome= %s, especialidade= %s , num_cedula = %s WHERE num_cedula = %s"
    data =(request.form["editar_nome"], request.form["editar_especialidade"], request.form["novo_num_cedula"], 
          request.form["editar_num_cedula"])
    cursor.execute(query,data)
    return query
  except Exception as e:
    return str(e) 
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()

#----------------------------- analises -----------------------------------

@app.route('/listAnalises')
def list_analises():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "SELECT * FROM analise;"
    cursor.execute(query)
    return render_template("analise.html", cursor=cursor, params=request.args)
  except Exception as e:
    return str(e) 
  finally:
    cursor.close()
    dbConn.close()

@app.route('/inserirAnalise',methods=["POST"])
def inserir_analise():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "INSERT INTO analise(num_analise, especialidade, num_cedula, num_doente, data, data_registo, nome, quant, inst) VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s);"
    data = (request.form["inserir_num_analise"],request.form["inserir_especialidade"],request.form["inserir_num_cedula"],request.form["inserir_num_doente"],request.form["inserir_data"],request.form["inserir_data_registo"],request.form["inserir_nome"],request.form["inserir_quant"],request.form["inserir_inst"])
    cursor.execute(query,data)
    return query
  except Exception as e:
    return str(e)
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()

@app.route('/removerAnalise',methods=["POST"])
def remover_analise():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "DELETE FROM analise WHERE num_analise = %s;"
    data = (request.form["remover_num_analise"])
    cursor.execute(query,data)
    return query
  except Exception as e:
    return str(e)
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()


@app.route('/editarAnalise', methods=["POST"])
def editar_analise():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query= "UPDATE analise SET num_analise= %s, especialidade= %s, num_cedula= %s, num_doente= %s, data= %s, data_registo= %s, nome= %s, quant=%s, inst=%s WHERE num_analise = %s"
    data =(request.form["novo_num_analise"], request.form["editar_especialidade"], request.form["editar_num_cedula"], request.form["editar_num_doente"], request.form["editar_data"], request.form["editar_data_registo"], request.form["editar_nome"], request.form["editar_quant"], request.form["editar_inst"], request.form["editar_num_analise"])
    cursor.execute(query,data)
    return query
  except Exception as e:
    return str(e) 
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()

@app.route('/alineae', methods=["POST"])
def listGlicemia():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query= '''with analises as (select analise.nome as nome_analise, quant, num_doente, inst
                from analise),
                todos as (select * from analises natural join instituicao
                    where instituicao.nome = analises.inst and nome_analise like 'glicemia'),
                maxs as (select max(quant), num_concelho from todos group by num_concelho),
                mins as (select min(quant), num_concelho from todos group by num_concelho),
                F as (select num_doente, quant, mins.num_concelho from todos inner join mins
          on mins.min = todos.quant),
                D as (select num_doente, quant, maxs.num_concelho from todos inner join maxs
          on maxs.max = todos.quant),
                uniao as (select * from F union (select * from D) )

          select * from uniao;'''
    cursor.execute(query)
    return render_template("querye.html", cursor=cursor, params=request.args)
    #return render_template("prescricao.html", cursor=cursor, params=request.args)
  except Exception as e:
    return str(e) 
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()



#---------------------------- prescricoes ---------------------------------

@app.route('/listPrescricoes')
def list_prescricoes():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "SELECT * FROM prescricao;"
    cursor.execute(query)
    return render_template("prescricao.html", cursor=cursor, params=request.args)
  except Exception as e:
    return str(e) 
  finally:
    cursor.close()
    dbConn.close()

@app.route('/inserirPrescricao',methods=["POST"])
def inserir_prescricao():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "INSERT INTO prescricao(num_cedula, num_doente, data, substancia, quant) VALUES(%s,%s,%s,%s,%s);"
    data = (request.form["inserir_num_cedula"],request.form["inserir_num_doente"],request.form["inserir_data"], request.form["inserir_substancia"],request.form["inserir_quantidade"])
    cursor.execute(query,data)
    return query
  except Exception as e:
    return str(e)
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()

#--- rever erro de constraint
@app.route('/removerPrescricao',methods=["POST"])
def remover_prescricao():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "DELETE FROM prescricao WHERE num_cedula = %s and num_doente = %s and data = %s and substancia = %s;"
    data = (request.form["remover_num_cedula"],request.form["remover_num_doente"],request.form["remover_data"],request.form["remover_substancia"])
    cursor.execute(query,data)
    return query
  except Exception as e:
    return str(e)
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()

#--- preciso substancia anterior???
@app.route('/editarPrescricao', methods=["POST"])
def editar_prescricao():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query= "UPDATE prescricao SET num_cedula = %s, num_doente = %s,data = %s,substancia = %s, quant = %s WHERE (num_cedula = %s and num_doente = %s and data = %s and substancia = %s)"
    data =(request.form["novo_num_cedula"], request.form["novo_num_doente"],request.form["nova_data"], request.form["nova_substancia"],request.form["editar_quantidade"],request.form["editar_num_cedula"], request.form["editar_num_doente"],request.form["editar_data"], request.form["editar_substancia"])
    cursor.execute(query,data)
    return query
  except Exception as e:
    return str(e) 
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()


@app.route('/listarSub', methods=["POST"])
def listSub():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "SELECT substancia FROM prescricao WHERE num_cedula= %s and extract(month from prescricao.data)= %s and extract(year from prescricao.data)= %s ;"
    data= (request.form["listar_num_cedula"], request.form["mes"], request.form["ano"])
    cursor.execute(query,data)
    return render_template("queryd.html", cursor=cursor, params=request.args)
    #return render_template("prescricao.html", cursor=cursor, params=request.args)
  except Exception as e:
    return str(e) 
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()

#----------------------------vendas-----------------------
@app.route('/listVendas')
def listVendas():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "SELECT * FROM venda_farmacia;"
    cursor.execute(query)
    return render_template("vendas.html", cursor=cursor, params=request.args)
  except Exception as e:
    return str(e) 
  finally:
    cursor.close()
    dbConn.close()

@app.route('/registarVenda',methods=["POST"])
def registarVenda():
  dbConn=None
  cursor=None
  try:
    dbConn = psycopg2.connect(DB_CONNECTION_STRING)
    cursor = dbConn.cursor(cursor_factory = psycopg2.extras.DictCursor)
    query = "select * from prescricao where num_cedula = %s and num_doente= %s and data = %s and substancia = %s and quant= %s;"
    data=(request.form["cedula"],request.form["doente"],request.form["data"],request.form["substancia"], request.form["quantidade"])
    
    query1 = "INSERT INTO venda_farmacia(num_venda, data_registo, substancia, quant, preco, inst) VALUES(%s,%s,%s,%s,%s,%s);"
    data1 = (request.form["num_venda"],request.form["data_registo"],request.form["substancia"],request.form["quantidade"], request.form["preco"], request.form["inst"])
    query2= "INSERT INTO prescricao_venda(num_cedula, num_doente, data, substancia, num_venda) VALUES(%s,%s,%s,%s,%s);"
    data2= (request.form["cedula"],request.form["doente"],request.form["data"],request.form["substancia"], request.form["num_venda"])
    
    cursor.execute(query,data)
    
    fs=cursor.fetchone()
    
    if(fs != None):
      cursor.execute(query1,data1)
      cursor.execute(query2,data2)
      return query2
    else:
      cursor.execute(query1,data1) 
      return query1
    
    #return render_template("alinea3.html", cursor=cursor, params=request.args)
  except Exception as e:
    return str(e)
  finally:
    dbConn.commit()
    cursor.close()
    dbConn.close()




CGIHandler().run(app)