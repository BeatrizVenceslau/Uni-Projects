---query 1
select ano, mes, especialidade,count(*) as num_glicemia
from (select especialidade, num_cedula as id_medico
from medico) as new_medico
natural join (select id_data_registo as id_tempo, nome, id_medico from f_analise) as new_f_analise
natural join d_tempo
where new_f_analise.nome = 'glicemia' and d_tempo.ano between '2017' and '2020'
group by 
	cube((especialidade),(mes),(ano));


--query 2
with substancias as (select substancia, quant,d_instituicao.num_concelho as num_concelho, d_tempo.mes as mes, d_tempo.dia_da_semana as dia_da_semana, d_tempo.id_tempo as id_tempo
					   from prescricao_venda natural join f_prescricao_venda 
					   natural join venda_farmacia
					   natural join d_instituicao
					   natural join d_tempo
					   where f_prescricao_venda.id_pres_venda = prescricao_venda.num_venda and 
					   prescricao_venda.num_venda = venda_farmacia.num_venda and d_instituicao.nome = venda_farmacia.inst
					   and d_tempo.id_tempo = f_prescricao_venda.id_data_registo and d_tempo.trimestre = '1' and d_tempo.ano = '2020'
				   	   and d_instituicao.num_regiao = '2'),
		prescricoes_por_dia as (select id_tempo,substancia, count(*)as num from substancias group by substancia,id_tempo)

select num_concelho, mes, dia_da_semana, sum(quant) as quant_substancia,null as substancia, null as media
from substancias
group by rollup(num_concelho, mes, dia_da_semana)
union 
select null,null, null, null, substancia, avg(num) as media 
from prescricoes_por_dia
group by substancia;