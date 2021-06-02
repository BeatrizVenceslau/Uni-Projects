
drop table analise cascade;
drop table regiao cascade;
drop table concelho cascade;
drop table instituicao cascade;
drop table medico cascade;
drop table consulta cascade;
drop table prescricao cascade;
drop table venda_farmacia cascade;
drop table prescricao_venda cascade;


/*
regiao(num_regiao, nome, num_habitantes)
RI-regiao-1: nome = {Norte, Centro, Lisboa, Alentejo, Algarve}
*/
create table regiao
    (num_regiao int not null unique,
    nome varchar(80) not null,
    num_habitantes int not null,
    constraint pk_regiao primary key(num_regiao),
    constraint chk_nome check(nome in ('Norte','Centro','Lisboa','Alentejo','Algarve')));


/*
concelho(num_concelho, num_regiao, nome, num_habitantes)
num_regiao: FK regiao
RI-concelho-1: nome = {concelhos de portugal connental}
*/
create table concelho 
    (num_concelho int not null,
    num_regiao int not null,
    nome varchar(80) not null,
    num_habitantes int not null,
    constraint pk_concelho primary key(num_concelho,num_regiao),
    constraint fk_regiao foreign key(num_regiao) references regiao(num_regiao));
    -- constraint chk_nome check(nome in ('concelhos excel')));


/*
instituicao(nome, tipo, num_regiao, num_concelho)
num_regiao, num_concelho: FK concelho (regiao, concelho)
RI-num_regiao-1: tipo = {farmacia, laboratorio, clinica, hospital}
*/
create table instituicao
    (nome varchar(80) not null unique,
    tipo varchar(80) not null,
    num_regiao int not null,
    num_concelho int not null,
    constraint pk_instituicao primary key(nome),
    constraint fk_instituicao_concelho foreign key(num_concelho,num_regiao) references concelho(num_concelho,num_regiao),
    constraint chk_tipo check(tipo in ('farmacia', 'laboratorio', 'clinica', 'hospital'))
    );


-- medico(num_cedula, nome, especialidade)
create table medico 
    (num_cedula int not null unique,
    nome varchar(80) not null,
    especialidade varchar(80),
    constraint pk_medico primary key(num_cedula));


/*
consulta(num_cedula, num_doente, data, nome_instituicao)
num_cedula: FK medico
nome_instituicao: FK instituicao
RI-consulta-1: um médico não pode ver doentes ao fim de semana
RI-consulta-2: um doente não pode ter mais de uma consulta por dia na mesma instituição
*/
create table consulta
    (num_cedula int not null,
    num_doente int not null,
    data date not null,
    nome_instituicao varchar(80) not null,
    constraint pk_consulta primary key(num_cedula,num_doente,data),
    constraint fk_consulta_cedula foreign key(num_cedula) references medico(num_cedula),
    constraint fk_consulta_instituicao foreign key(nome_instituicao) references instituicao(nome),
    constraint chk_saturday check(extract (dow from data) < 6),
    constraint chk_sunday check(extract (dow from data) > 0),
    constraint uc_consulta unique(num_doente, data, nome_instituicao));


/*
prescricao(num_cedula, num_doente, data, substancia, quant)
num_cedula, num_doente, data: FK consulta (num_cedula, num_doente, data)
*/
create table prescricao
    (num_cedula int not null,
    num_doente int not null,
    data date not null,
    substancia varchar(80) not null,
    quant int not null,
    constraint pk_prescricao primary key(num_cedula,num_doente,data,substancia),
    constraint fk_prescricao_cedula foreign key(num_cedula,num_doente,data) references consulta(num_cedula,num_doente,data));


/*
analise(num_analise, especialidade, num_cedula, num_doente, data, data_registo, nome, quant, inst)
num_cedula, num_doente, data: FK consulta (num_cedula, num_doente, data)
inst: FK instituicao
RI: a consulta associada pode estar omissa; não estando, a especialidade da consulta tem de ser igual à
do médico.
*/
create table analise
    (num_analise int not null unique,
    especialidade varchar(80) not null,
    num_cedula int,
    num_doente int,
    data date,
    data_registo date not null,
    nome varchar(80) not null,
    quant int not null,
    inst varchar(80) not null,
    constraint pk_analise primary key(num_analise),
    constraint fk_analise_cedula foreign key(num_cedula,num_doente,data) references consulta(num_cedula,num_doente,data),
    constraint fk_analise_inst foreign key(inst) references instituicao(nome));
    -- se consulta omissa: especialidade medico == especialidade


/*
venda_farmacia(num_venda, data_registo, substancia, quant, preco, inst)
inst: FK instituicao
*/
create table venda_farmacia
    (num_venda int not null unique,
    data_registo date not null,
    substancia varchar(80) not null,
    quant int not null,
    preco float(24) not null,
    inst varchar(80) not null,
    constraint pk_venda_farmacia primary key(num_venda),
    constraint fk_num_venda foreign key(inst) references instituicao(nome));


/*
prescricao_venda(num_cedula, num_doente, data, substancia, num_venda)
num_venda: FK venda_farmacia
num_cedula, num_doente, data, substancia: FK prescricao (num_cedula, num_doente, data, substancia)
*/
create table prescricao_venda
    (num_cedula int not null,
    num_doente int not null,
    data date not null,
    substancia varchar(80) not null,
    num_venda int not null,
    constraint pk_prescricao_venda primary key(num_cedula,num_doente,substancia,data, num_venda),
    constraint fk_pres_venda_venda foreign key(num_venda) references venda_farmacia(num_venda),
    constraint fk_pres_venda_cedula foreign key(num_cedula,num_doente,data,substancia) references prescricao(num_cedula,num_doente,data,substancia));







