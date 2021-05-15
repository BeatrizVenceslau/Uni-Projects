--populate d_tempo 1 em 1 dias de 2017 a 2020
drop function populate_tempo();

create or replace function populate_tempo()
returns void as $$
declare initial_date timestamp;
begin
initial_date = '2017-01-01 00:00:00.00';
while initial_date < '2020-12-31 00:00:00.00'
    loop
    insert into d_tempo(dia,dia_da_semana, semana, mes, trimestre, ano)
        values(extract(day from initial_date), extract(dow from initial_date), --somar 1 cuz domingo???
        extract(week from initial_date), extract(month from initial_date),
        extract(quarter from initial_date),extract(year from initial_date));
        initial_date = initial_date + interval '1 day';
    end loop;
return;
end
$$ language plpgsql;

select populate_tempo();

-- populate d_instituicao from instituicao da E3
insert into d_instituicao(nome,tipo,num_regiao,num_concelho)
    select distinct nome, tipo, num_regiao, num_concelho from instituicao;

--populate f_pres_venda
insert into f_prescricao_venda(id_pres_venda, id_medico, id_data_registo, id_inst)
	select distinct new_presc.num_venda as id_pres_venda, new_presc.num_cedula as id_medico , id_tempo as id_data_registo, id_inst
	from(select distinct num_venda ,venda_farmacia.inst as nome, num_cedula,extract(day from prescricao_venda.data) as dia,
		  extract(month from prescricao_venda.data) as mes,extract(year from prescricao_venda.data) as ano 
					   from prescricao_venda natural join venda_farmacia
					  	where prescricao_venda.num_venda = venda_farmacia.num_venda) as new_presc
	natural join d_tempo
	natural join d_instituicao;

--populate f_analise

insert into f_analise (id_analise, id_medico, num_doente, id_data_registo,id_inst, nome, quant)
    select distinct new_analise.num_analise as id_analise, new_analise.num_cedula as id_medico, new_analise.num_doente as num_doente, id_tempo as id_data_registo,
	id_inst, new_analise.nome_analise as nome, new_analise.quant as quant
	from (select distinct num_analise, num_cedula, num_doente, nome as nome_analise,inst as nome, quant,
						extract(day from analise.data_registo) as dia,extract(month from analise.data_registo) as mes,
						extract(year from analise.data_registo) as ano from analise) as new_analise
	natural join d_tempo
	natural join d_instituicao;