drop table d_tempo cascade;
drop table d_instituicao cascade;
drop table f_prescricao_venda cascade;
drop table f_analise cascade;


create table d_tempo (
    id_tempo serial not null,  ---auto-incrementa 
    dia smallint not null,
    dia_da_semana smallint not null,
    semana smallint not null,
    mes smallint not null,
    trimestre smallint not null,
    ano integer not null,
    constraint pk_d_tempo primary key(id_tempo));

create table d_instituicao(
    id_inst serial not null,
    nome varchar(255) not null,
    tipo varchar(255) not null,
    num_regiao integer not null,
    num_concelho integer not null,
    constraint pk_d_inst primary key(id_inst),
    constraint fk_nome_inst foreign key(nome) references instituicao(nome),
    constraint fk_regiao_inst foreign key(num_regiao) references regiao(num_regiao),
    constraint fk_concelho_inst foreign key(num_concelho,num_regiao) references concelho(num_concelho,num_regiao));

create table f_prescricao_venda(
    id_pres_venda integer not null,
    id_medico integer not null,
    id_data_registo integer not null,
    id_inst integer not null,
    constraint pk_f_presc_venda primary key(id_pres_venda),
    constraint fk_f_presc_venda foreign key(id_pres_venda) references prescricao_venda(num_venda),
    constraint fk_f_presc_id_medico foreign key(id_medico) references medico(num_cedula),
    constraint fk_f_presc_id_data_registo foreign key(id_data_registo) references d_tempo(id_tempo),
    constraint fk_f_presc_id_inst foreign key(id_inst) references d_instituicao(id_inst));

create table f_analise(
    id_analise integer not null,
    id_medico integer,
    num_doente integer not null,
    id_data_registo integer not null,
    id_inst integer not null,
    nome varchar(255) not null,
    quant integer not null,
    constraint pk_f_analise primary key(id_analise),
    constraint fk_f_analise_id_analise foreign key(id_analise) references analise(num_analise),
    constraint fk_f_analise_id_medico foreign key(id_medico) references medico(num_cedula),
    constraint fk_f_analise_id_data_registo foreign key(id_data_registo) references d_tempo(id_tempo),
    constraint fk_f_analise_id_inst foreign key(id_inst) references d_instituicao(id_inst));
