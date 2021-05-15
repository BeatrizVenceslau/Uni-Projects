
--RI-100: ​um médico não pode dar mais de 100 consultas por semana na mesma  instituição
drop trigger if exists verificar_medico_consultas_trigger on consulta;

create or replace function verificar_medico_consultas() returns trigger AS $$
declare num_consultas int;


BEGIN
    select count(*) into num_consultas   
    from consulta
    where consulta.nome_instituicao = new.nome_instituicao 
    and consulta.num_cedula = new.num_cedula
    and extract(week from cast(new.data as date)) = ( select extract(week from cast(consulta.data as date)))
    and extract(year from cast(new.data as date)) = ( select extract(year from cast(consulta.data as date)));

    if(num_consultas = 100) then
      raise exception 'RI-100: ​o médico % não pode dar mais de 100 consultas por semana na mesma instituição', new.num_cedula;
    else
    end if;
    return new;

end
$$ Language plpgsql;

create trigger verificar_medico_consultas_trigger before insert on consulta
for each row execute procedure verificar_medico_consultas();



--RI-análise:​ numa análise,a consulta associada pode estar omissa;
--não estando, a especialidade da consulta tem de ser igual à do médico.

drop trigger if exists verificar_especialidade_trigger on analise;

create or replace function verificar_especialidade() returns trigger AS $$
declare esp varchar (255);


BEGIN
    select especialidade into esp from medico where num_cedula= new.num_cedula;
    if(new.num_cedula is NULL or new.num_doente is NULL or new.data is NULL ) then
        new.especialidade = NULL;
    else
        new.especialidade= esp;
    end if;
    return new;

END
$$ Language plpgsql;

create trigger verificar_especialidade_trigger before insert on analise
for each row execute procedure verificar_especialidade();

