--1 -falta por no populate a data corrente ou alterar para as no comentario

select num_concelho, num_regiao
from venda_farmacia natural join instituicao
where data_registo = current_date -- dia '2020-11-15'
and venda_farmacia.inst = instituicao.nome
group by num_concelho, num_regiao
having sum(quant) >= all
	(select sum(quant)
	from venda_farmacia natural join instituicao
	where data_registo = current_date -- dia '2020-11-15'
	and venda_farmacia.inst = instituicao.nome
	group by num_concelho, num_regiao);

--2
with aux as (select count(*) as counter,num_regiao, num_cedula
        from instituicao natural join 
            (select *
            from prescricao natural join consulta
            where data>= '2019-01-01' and data<='2019-06-30') as s
        where s.nome_instituicao = instituicao.nome
        group by num_regiao, num_cedula)

select a.num_regiao, a.num_cedula
from aux a inner join ( select num_regiao, max(counter) counter
    from aux b
    group by num_regiao
) b on a.num_regiao = b.num_regiao and a.counter = b.counter;

--3
with tabela as (select num_cedula, substancia, num_concelho, nome from prescricao_venda natural join instituicao natural join venda_farmacia
		where instituicao.nome = venda_farmacia.inst 
		and prescricao_venda.substancia = 'aspirina' 
		and extract(year from prescricao_venda.data) = ( select extract( year from current_date)) 
		and prescricao_venda.data < current_date
		and instituicao.tipo = 'farmacia'
		and num_concelho = 4 and num_regiao = 0),
	array_farmacias as (select array_agg ( nome) as nome from instituicao where tipo = 'farmacia' and num_concelho = 4 and num_regiao = 0 ),
	array_por_ced as (select tabela.num_cedula, array_agg ( nome) farma from tabela
		group by tabela.num_cedula)
select num_cedula from array_por_ced, array_farmacias where array_por_ced.farma <@ array_farmacias.nome and array_por_ced.farma @> array_farmacias.nome;


--4 --nao existem analises nem prescricoes em dezembro 
with fez_analise as (select * from analise
		where extract(month from analise.data_registo) = ( select extract( month from current_date))
		and analise.data_registo <= current_date),
	fez_prescricao as (select * from prescricao_venda
		where extract(month from prescricao_venda.data) = ( select extract( month from current_date))
		and prescricao_venda.data <= current_date)
select num_doente from fez_analise except select num_doente from fez_prescricao;

