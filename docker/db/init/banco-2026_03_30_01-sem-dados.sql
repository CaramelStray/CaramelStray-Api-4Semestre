--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE SCHEMA tiger;
ALTER SCHEMA tiger OWNER TO user_dev;

CREATE SCHEMA tiger_data;
ALTER SCHEMA tiger_data OWNER TO user_dev;

CREATE SCHEMA topology;
ALTER SCHEMA topology OWNER TO user_dev;

CREATE EXTENSION IF NOT EXISTS fuzzystrmatch WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS postgis_tiger_geocoder WITH SCHEMA tiger;
CREATE EXTENSION IF NOT EXISTS postgis_topology WITH SCHEMA topology;

SET default_tablespace = '';
SET default_table_access_method = heap;

--
-- Name: tb_cad_ativo; Type: TABLE; Schema: public; Owner: user_dev
--
CREATE TABLE public.tb_cad_ativo (
    codigo integer NOT NULL,
    codigo_catalogo_ativo integer NOT NULL,
    numero_serie character varying(255),
    lote character varying(255),
    descricao text,
    codigo_funcionario_responsavel integer,
    status character varying(100)
);
ALTER TABLE public.tb_cad_ativo OWNER TO user_dev;

ALTER TABLE public.tb_cad_ativo ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_ativo_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

--
-- Name: tb_cad_ativo_estoque; Type: TABLE; Schema: public; Owner: user_dev
--
CREATE TABLE public.tb_cad_ativo_estoque (
    codigo_catalogo_ativo integer NOT NULL,
    quantidade integer DEFAULT 0 NOT NULL
);
ALTER TABLE public.tb_cad_ativo_estoque OWNER TO user_dev;

--
-- Name: tb_cad_catalogo_ativo; Type: TABLE; Schema: public; Owner: user_dev
--
CREATE TABLE public.tb_cad_catalogo_ativo (
    codigo integer NOT NULL,
    descricao_produto character varying(255) NOT NULL,
    modelo character varying(255),
    marca character varying(255),
    descricao text,
    especificacao text,
    tipo character varying(100)
);
ALTER TABLE public.tb_cad_catalogo_ativo OWNER TO user_dev;

ALTER TABLE public.tb_cad_catalogo_ativo ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_catalogo_ativo_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

--
-- Name: tb_cad_catalogo_maquina; Type: TABLE; Schema: public; Owner: user_dev
--
CREATE TABLE public.tb_cad_catalogo_maquina (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL,
    especificacao text,
    limite_manutencao character varying(100)
);
ALTER TABLE public.tb_cad_catalogo_maquina OWNER TO user_dev;

CREATE TABLE public.tb_cad_catalogo_maquina_checklist_padrao (
    codigo_catalogo_maquina integer NOT NULL,
    codigo_tarefa integer NOT NULL
);
ALTER TABLE public.tb_cad_catalogo_maquina_checklist_padrao OWNER TO user_dev;

ALTER TABLE public.tb_cad_catalogo_maquina ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_catalogo_maquina_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_cad_catalogo_maquina_ferramenta_modelo (
    codigo_catalogo_maquina integer NOT NULL,
    codigo_catalogo_ativo integer NOT NULL,
    quantidade_necessaria integer DEFAULT 1 NOT NULL
);
ALTER TABLE public.tb_cad_catalogo_maquina_ferramenta_modelo OWNER TO user_dev;

CREATE TABLE public.tb_cad_catalogo_software (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL
);
ALTER TABLE public.tb_cad_catalogo_software OWNER TO user_dev;

CREATE TABLE public.tb_cad_catalogo_software_checklist_padrao (
    codigo_catalogo_software integer NOT NULL,
    codigo_catalogo_tarefa integer NOT NULL,
    obrigatorio boolean NOT NULL
);
ALTER TABLE public.tb_cad_catalogo_software_checklist_padrao OWNER TO user_dev;

ALTER TABLE public.tb_cad_catalogo_software ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_catalogo_software_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_cad_catalogo_tarefa (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL,
    categoria character varying(100)
);
ALTER TABLE public.tb_cad_catalogo_tarefa OWNER TO user_dev;

ALTER TABLE public.tb_cad_catalogo_tarefa ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_catalogo_tarefa_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_cad_cliente (
    codigo integer NOT NULL,
    ativo boolean NOT NULL,
    cidade character varying(100),
    classificacao_distancia character varying(100),
    data_cadastro timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    documento character varying(30),
    email_contato character varying(255),
    estado_regiao character varying(100),
    fuso_horario character varying(100),
    descricao_empresa character varying(255) NOT NULL,
    descricao_responsavel character varying(255),
    pais character varying(100),
    telefone_contato character varying(30),
    codigo_usuario integer,
    observacao text,
    rua character varying(255),
    numero character varying(50),
    internacional boolean DEFAULT false
);
ALTER TABLE public.tb_cad_cliente OWNER TO user_dev;

ALTER TABLE public.tb_cad_cliente ALTER COLUMN codigo ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tb_cad_cliente_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_cad_cliente_contato (
    codigo integer NOT NULL,
    codigo_cliente integer NOT NULL,
    nome character varying(255) NOT NULL,
    email character varying(255),
    telefone character varying(30)
);
ALTER TABLE public.tb_cad_cliente_contato OWNER TO user_dev;

ALTER TABLE public.tb_cad_cliente_contato ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_cliente_contato_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_cad_contrato (
    codigo integer NOT NULL,
    codigo_cliente integer NOT NULL,
    data_inicio date,
    data_fim date,
    status character varying(100),
    periodo_manutencao_preventiva integer,
    conexao_internet boolean DEFAULT false,
    vencimento_manutencao_preventiva date
);
ALTER TABLE public.tb_cad_contrato OWNER TO user_dev;

ALTER TABLE public.tb_cad_contrato ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_contrato_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_cad_funcionario (
    codigo integer NOT NULL,
    codigo_usuario integer,
    nome character varying(255) NOT NULL,
    cpf character varying(20),
    cargo character varying(100),
    telefone character varying(30),
    latitude numeric(10,6),
    longitude numeric(10,6)
);
ALTER TABLE public.tb_cad_funcionario OWNER TO user_dev;

ALTER TABLE public.tb_cad_funcionario ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_funcionario_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_cad_funcionario_habilidade (
    codigo_funcionario integer NOT NULL,
    codigo_habilidade integer NOT NULL,
    nivel integer,
    data_validade date
);
ALTER TABLE public.tb_cad_funcionario_habilidade OWNER TO user_dev;

CREATE TABLE public.tb_cad_habilidade (
    codigo integer NOT NULL,
    descricao character varying(100) NOT NULL,
    observacao character varying(255)
);
ALTER TABLE public.tb_cad_habilidade OWNER TO user_dev;

ALTER TABLE public.tb_cad_habilidade ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_habilidade_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_cad_perfil (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL
);
ALTER TABLE public.tb_cad_perfil OWNER TO user_dev;

ALTER TABLE public.tb_cad_perfil ALTER COLUMN codigo ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tb_cad_perfil_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_cad_perfil_permissao (
    codigo_perfil integer NOT NULL,
    codigo_permissao integer NOT NULL
);
ALTER TABLE public.tb_cad_perfil_permissao OWNER TO user_dev;

CREATE TABLE public.tb_cad_permissao (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL
);
ALTER TABLE public.tb_cad_permissao OWNER TO user_dev;

ALTER TABLE public.tb_cad_permissao ALTER COLUMN codigo ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tb_cad_permissao_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_cad_tipo_manutencao (
    codigo integer NOT NULL,
    descricao character varying(100) NOT NULL,
    observacao character varying(255)
);
ALTER TABLE public.tb_cad_tipo_manutencao OWNER TO user_dev;

ALTER TABLE public.tb_cad_tipo_manutencao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_tipo_manutencao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_cad_tipo_manutencao_habilidade (
    codigo_tipo_manutencao integer NOT NULL,
    codigo_habilidade integer NOT NULL,
    obrigatoria boolean DEFAULT true NOT NULL
);
ALTER TABLE public.tb_cad_tipo_manutencao_habilidade OWNER TO user_dev;

CREATE TABLE public.tb_cad_usuario (
    codigo integer NOT NULL,
    ativo boolean NOT NULL,
    data_criacao timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP,
    email character varying(255) NOT NULL,
    senha character varying(255) NOT NULL
);
ALTER TABLE public.tb_cad_usuario OWNER TO user_dev;

ALTER TABLE public.tb_cad_usuario ALTER COLUMN codigo ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tb_cad_usuario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_cad_usuario_perfil (
    codigo_usuario integer NOT NULL,
    codigo_perfil integer NOT NULL
);
ALTER TABLE public.tb_cad_usuario_perfil OWNER TO user_dev;

CREATE TABLE public.tb_srv_maquina_checklist_ferramenta_manutencao (
    codigo integer NOT NULL,
    codigo_historico_manutencao integer NOT NULL,
    codigo_catalogo_ativo integer NOT NULL,
    codigo_ativo integer,
    levou boolean DEFAULT false NOT NULL
);
ALTER TABLE public.tb_srv_maquina_checklist_ferramenta_manutencao OWNER TO user_dev;

ALTER TABLE public.tb_srv_maquina_checklist_ferramenta_manutencao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_checklist_ferramenta_manutencao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_srv_maquina_checklist_manutencao (
    codigo integer NOT NULL,
    codigo_historico_manutencao integer NOT NULL,
    codigo_tarefa integer NOT NULL,
    realizado boolean,
    observacao_tarefa text
);
ALTER TABLE public.tb_srv_maquina_checklist_manutencao OWNER TO user_dev;

ALTER TABLE public.tb_srv_maquina_checklist_manutencao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_checklist_manutencao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_srv_maquina_contrato (
    codigo integer NOT NULL,
    codigo_contrato integer NOT NULL,
    codigo_catalogo_maquina integer NOT NULL,
    numero_serie character varying(255),
    data_instalacao date,
    manutencao_feita character varying(255),
    trabalho_em_altura boolean DEFAULT false NOT NULL,
    latitude numeric(10,6),
    longitude numeric(10,6)
);
ALTER TABLE public.tb_srv_maquina_contrato OWNER TO user_dev;

ALTER TABLE public.tb_srv_maquina_contrato ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_contrato_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_srv_maquina_funcionario_manutencao (
    codigo_historico_manutencao integer NOT NULL,
    codigo_funcionario integer NOT NULL
);
ALTER TABLE public.tb_srv_maquina_funcionario_manutencao OWNER TO user_dev;

CREATE TABLE public.tb_srv_maquina_historico_manutencao (
    codigo integer NOT NULL,
    codigo_maquina_contrato integer NOT NULL,
    codigo_software_instalado integer,
    codigo_tipo_manutencao integer NOT NULL,
    status character varying(100),
    criticidade character varying(50),
    vencimento timestamp without time zone,
    data_agendamento timestamp without time zone,
    data_inicio_execucao timestamp without time zone,
    data_fim_execucao timestamp without time zone,
    observacao_geral text,
    codigo_contrato_ativo integer -- Coluna movida para dentro do CREATE
);

CREATE TABLE public.tb_srv_contrato_ativo (
    codigo integer NOT NULL,
    codigo_contrato integer NOT NULL,
    codigo_catalogo_ativo integer,
    codigo_catalogo_maquina integer,
    numero_serie character varying(255),
    quantidade integer DEFAULT 1,
    localizacao_no_site character varying(255),
    observacao text
);

ALTER TABLE public.tb_srv_contrato_ativo ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_contrato_ativo_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

ALTER TABLE ONLY public.tb_srv_contrato_ativo
    ADD CONSTRAINT tb_srv_contrato_ativo_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_srv_contrato_ativo
    ADD CONSTRAINT fk_contrato_ativo_contrato FOREIGN KEY (codigo_contrato) REFERENCES public.tb_cad_contrato(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_contrato_ativo
    ADD CONSTRAINT fk_contrato_ativo_catalogo_ativo FOREIGN KEY (codigo_catalogo_ativo) REFERENCES public.tb_cad_catalogo_ativo(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_contrato_ativo
    ADD CONSTRAINT fk_contrato_ativo_catalogo_maquina FOREIGN KEY (codigo_catalogo_maquina) REFERENCES public.tb_cad_catalogo_maquina(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_historico_manutencao
    ADD CONSTRAINT fk_historico_manutencao_contrato_ativo FOREIGN KEY (codigo_contrato_ativo) REFERENCES public.tb_srv_contrato_ativo(codigo) ON DELETE CASCADE;

ALTER TABLE public.tb_srv_maquina_historico_manutencao OWNER TO user_dev;

ALTER TABLE public.tb_srv_maquina_historico_manutencao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_historico_manutencao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_srv_maquina_observacao_manutencao (
    codigo integer NOT NULL,
    codigo_historico_manutencao integer NOT NULL,
    codigo_funcionario integer NOT NULL,
    observacao text,
    data_criacao timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);
ALTER TABLE public.tb_srv_maquina_observacao_manutencao OWNER TO user_dev;

ALTER TABLE public.tb_srv_maquina_observacao_manutencao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_observacao_manutencao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_srv_maquina_software_instalado (
    codigo integer NOT NULL,
    codigo_maquina_contrato integer NOT NULL,
    codigo_software integer NOT NULL,
    chave_licenca character varying(255),
    versao_instalada character varying(100)
);
ALTER TABLE public.tb_srv_maquina_software_instalado OWNER TO user_dev;

ALTER TABLE public.tb_srv_maquina_software_instalado ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_software_instalado_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE public.tb_srv_maquina_troca_imprevista_manutencao (
    codigo integer NOT NULL,
    codigo_historico_manutencao integer NOT NULL,
    codigo_funcionario integer NOT NULL,
    codigo_ativo_utilizado integer NOT NULL,
    observacao text,
    data_criacao timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);
ALTER TABLE public.tb_srv_maquina_troca_imprevista_manutencao OWNER TO user_dev;

ALTER TABLE public.tb_srv_maquina_troca_imprevista_manutencao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_troca_imprevista_manutencao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

--
-- CONSTRAINTS
--

ALTER TABLE ONLY public.tb_cad_catalogo_maquina_checklist_padrao
    ADD CONSTRAINT pk_checklist_padrao_maquina PRIMARY KEY (codigo_catalogo_maquina, codigo_tarefa);

ALTER TABLE ONLY public.tb_cad_catalogo_software_checklist_padrao
    ADD CONSTRAINT pk_checklist_padrao_software PRIMARY KEY (codigo_catalogo_software, codigo_catalogo_tarefa);

ALTER TABLE ONLY public.tb_cad_catalogo_maquina_ferramenta_modelo
    ADD CONSTRAINT pk_ferramenta_modelo_maquina PRIMARY KEY (codigo_catalogo_maquina, codigo_catalogo_ativo);

ALTER TABLE ONLY public.tb_cad_funcionario_habilidade
    ADD CONSTRAINT pk_funcionario_habilidade PRIMARY KEY (codigo_funcionario, codigo_habilidade);

ALTER TABLE ONLY public.tb_srv_maquina_funcionario_manutencao
    ADD CONSTRAINT pk_funcionario_manutencao PRIMARY KEY (codigo_historico_manutencao, codigo_funcionario);

ALTER TABLE ONLY public.tb_cad_tipo_manutencao_habilidade
    ADD CONSTRAINT pk_tipo_manutencao_habilidade PRIMARY KEY (codigo_tipo_manutencao, codigo_habilidade);

ALTER TABLE ONLY public.tb_cad_ativo_estoque
    ADD CONSTRAINT tb_cad_ativo_estoque_pkey PRIMARY KEY (codigo_catalogo_ativo);

ALTER TABLE ONLY public.tb_cad_ativo
    ADD CONSTRAINT tb_cad_ativo_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_catalogo_ativo
    ADD CONSTRAINT tb_cad_catalogo_ativo_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_catalogo_maquina
    ADD CONSTRAINT tb_cad_catalogo_maquina_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_catalogo_software
    ADD CONSTRAINT tb_cad_catalogo_software_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_catalogo_tarefa
    ADD CONSTRAINT tb_cad_catalogo_tarefa_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_cliente
    ADD CONSTRAINT tb_cad_cliente_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_cliente_contato
    ADD CONSTRAINT tb_cad_cliente_contato_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_contrato
    ADD CONSTRAINT tb_cad_contrato_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_funcionario
    ADD CONSTRAINT tb_cad_funcionario_codigo_usuario_key UNIQUE (codigo_usuario);

ALTER TABLE ONLY public.tb_cad_funcionario
    ADD CONSTRAINT tb_cad_funcionario_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_habilidade
    ADD CONSTRAINT tb_cad_habilidade_descricao_key UNIQUE (descricao);

ALTER TABLE ONLY public.tb_cad_habilidade
    ADD CONSTRAINT tb_cad_habilidade_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_perfil_permissao
    ADD CONSTRAINT tb_cad_perfil_permissao_pkey PRIMARY KEY (codigo_perfil, codigo_permissao);

ALTER TABLE ONLY public.tb_cad_perfil
    ADD CONSTRAINT tb_cad_perfil_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_permissao
    ADD CONSTRAINT tb_cad_permissao_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_tipo_manutencao
    ADD CONSTRAINT tb_cad_tipo_manutencao_descricao_key UNIQUE (descricao);

ALTER TABLE ONLY public.tb_cad_tipo_manutencao
    ADD CONSTRAINT tb_cad_tipo_manutencao_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_usuario_perfil
    ADD CONSTRAINT tb_cad_usuario_perfil_pkey PRIMARY KEY (codigo_usuario, codigo_perfil);

ALTER TABLE ONLY public.tb_cad_usuario
    ADD CONSTRAINT tb_cad_usuario_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_srv_maquina_checklist_ferramenta_manutencao
    ADD CONSTRAINT tb_srv_maquina_checklist_ferramenta_manutencao_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_srv_maquina_checklist_manutencao
    ADD CONSTRAINT tb_srv_maquina_checklist_manutencao_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_srv_maquina_contrato
    ADD CONSTRAINT tb_srv_maquina_contrato_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_srv_maquina_historico_manutencao
    ADD CONSTRAINT tb_srv_maquina_historico_manutencao_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_srv_maquina_observacao_manutencao
    ADD CONSTRAINT tb_srv_maquina_observacao_manutencao_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_srv_maquina_software_instalado
    ADD CONSTRAINT tb_srv_maquina_software_instalado_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_srv_maquina_troca_imprevista_manutencao
    ADD CONSTRAINT tb_srv_maquina_troca_imprevista_manutencao_pkey PRIMARY KEY (codigo);

ALTER TABLE ONLY public.tb_cad_usuario
    ADD CONSTRAINT uk632kb8n9udn5mba0exa9qs501 UNIQUE (email);

--
-- FOREIGN KEYS
--

ALTER TABLE ONLY public.tb_cad_usuario_perfil
    ADD CONSTRAINT fk6f1v3k7v61mt1n6u6lpt2x0e3 FOREIGN KEY (codigo_usuario) REFERENCES public.tb_cad_usuario(codigo);

ALTER TABLE ONLY public.tb_cad_perfil_permissao
    ADD CONSTRAINT fk9xue5r7rqj3ujnmeb7gvwnhnu FOREIGN KEY (codigo_perfil) REFERENCES public.tb_cad_perfil(codigo);

ALTER TABLE ONLY public.tb_cad_ativo
    ADD CONSTRAINT fk_ativo_catalogo FOREIGN KEY (codigo_catalogo_ativo) REFERENCES public.tb_cad_catalogo_ativo(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_ativo_estoque
    ADD CONSTRAINT fk_ativo_estoque_catalogo FOREIGN KEY (codigo_catalogo_ativo) REFERENCES public.tb_cad_catalogo_ativo(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_ativo
    ADD CONSTRAINT fk_ativo_funcionario FOREIGN KEY (codigo_funcionario_responsavel) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_checklist_ferramenta_manutencao
    ADD CONSTRAINT fk_checklist_ferramenta_ativo FOREIGN KEY (codigo_ativo) REFERENCES public.tb_cad_ativo(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_checklist_ferramenta_manutencao
    ADD CONSTRAINT fk_checklist_ferramenta_catalogo_ativo FOREIGN KEY (codigo_catalogo_ativo) REFERENCES public.tb_cad_catalogo_ativo(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_checklist_ferramenta_manutencao
    ADD CONSTRAINT fk_checklist_ferramenta_historico FOREIGN KEY (codigo_historico_manutencao) REFERENCES public.tb_srv_maquina_historico_manutencao(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_checklist_manutencao
    ADD CONSTRAINT fk_checklist_manutencao_historico FOREIGN KEY (codigo_historico_manutencao) REFERENCES public.tb_srv_maquina_historico_manutencao(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_checklist_manutencao
    ADD CONSTRAINT fk_checklist_manutencao_tarefa FOREIGN KEY (codigo_tarefa) REFERENCES public.tb_cad_catalogo_tarefa(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_catalogo_maquina_checklist_padrao
    ADD CONSTRAINT fk_checklist_padrao_maquina_catalogo FOREIGN KEY (codigo_catalogo_maquina) REFERENCES public.tb_cad_catalogo_maquina(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_catalogo_maquina_checklist_padrao
    ADD CONSTRAINT fk_checklist_padrao_maquina_tarefa FOREIGN KEY (codigo_tarefa) REFERENCES public.tb_cad_catalogo_tarefa(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_catalogo_software_checklist_padrao
    ADD CONSTRAINT fk_checklist_padrao_software_software FOREIGN KEY (codigo_catalogo_software) REFERENCES public.tb_cad_catalogo_software(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_catalogo_software_checklist_padrao
    ADD CONSTRAINT fk_checklist_padrao_software_tarefa FOREIGN KEY (codigo_catalogo_tarefa) REFERENCES public.tb_cad_catalogo_tarefa(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_contrato
    ADD CONSTRAINT fk_contrato_cliente FOREIGN KEY (codigo_cliente) REFERENCES public.tb_cad_cliente(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_catalogo_maquina_ferramenta_modelo
    ADD CONSTRAINT fk_ferramenta_modelo_maquina_catalogo_ativo FOREIGN KEY (codigo_catalogo_ativo) REFERENCES public.tb_cad_catalogo_ativo(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_catalogo_maquina_ferramenta_modelo
    ADD CONSTRAINT fk_ferramenta_modelo_maquina_catalogo_maquina FOREIGN KEY (codigo_catalogo_maquina) REFERENCES public.tb_cad_catalogo_maquina(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_funcionario_habilidade
    ADD CONSTRAINT fk_funcionario_habilidade_funcionario FOREIGN KEY (codigo_funcionario) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_funcionario_habilidade
    ADD CONSTRAINT fk_funcionario_habilidade_habilidade FOREIGN KEY (codigo_habilidade) REFERENCES public.tb_cad_habilidade(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_funcionario_manutencao
    ADD CONSTRAINT fk_funcionario_manutencao_funcionario FOREIGN KEY (codigo_funcionario) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_funcionario_manutencao
    ADD CONSTRAINT fk_funcionario_manutencao_historico FOREIGN KEY (codigo_historico_manutencao) REFERENCES public.tb_srv_maquina_historico_manutencao(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_funcionario
    ADD CONSTRAINT fk_funcionario_usuario FOREIGN KEY (codigo_usuario) REFERENCES public.tb_cad_usuario(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_historico_manutencao
    ADD CONSTRAINT fk_historico_manutencao_maquina FOREIGN KEY (codigo_maquina_contrato) REFERENCES public.tb_srv_maquina_contrato(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_historico_manutencao
    ADD CONSTRAINT fk_historico_manutencao_software FOREIGN KEY (codigo_software_instalado) REFERENCES public.tb_srv_maquina_software_instalado(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_historico_manutencao
    ADD CONSTRAINT fk_historico_manutencao_tipo FOREIGN KEY (codigo_tipo_manutencao) REFERENCES public.tb_cad_tipo_manutencao(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_contrato
    ADD CONSTRAINT fk_maquina_contrato_catalogo FOREIGN KEY (codigo_catalogo_maquina) REFERENCES public.tb_cad_catalogo_maquina(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_contrato
    ADD CONSTRAINT fk_maquina_contrato_contrato FOREIGN KEY (codigo_contrato) REFERENCES public.tb_cad_contrato(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_observacao_manutencao
    ADD CONSTRAINT fk_observacao_manutencao_funcionario FOREIGN KEY (codigo_funcionario) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_observacao_manutencao
    ADD CONSTRAINT fk_observacao_manutencao_historico FOREIGN KEY (codigo_historico_manutencao) REFERENCES public.tb_srv_maquina_historico_manutencao(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_software_instalado
    ADD CONSTRAINT fk_software_instalado_maquina FOREIGN KEY (codigo_maquina_contrato) REFERENCES public.tb_srv_maquina_contrato(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_software_instalado
    ADD CONSTRAINT fk_software_instalado_software FOREIGN KEY (codigo_software) REFERENCES public.tb_cad_catalogo_software(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_tipo_manutencao_habilidade
    ADD CONSTRAINT fk_tipo_manutencao_habilidade_habilidade FOREIGN KEY (codigo_habilidade) REFERENCES public.tb_cad_habilidade(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_tipo_manutencao_habilidade
    ADD CONSTRAINT fk_tipo_manutencao_habilidade_tipo FOREIGN KEY (codigo_tipo_manutencao) REFERENCES public.tb_cad_tipo_manutencao(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_troca_imprevista_manutencao
    ADD CONSTRAINT fk_troca_imprevista_ativo FOREIGN KEY (codigo_ativo_utilizado) REFERENCES public.tb_cad_ativo(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_troca_imprevista_manutencao
    ADD CONSTRAINT fk_troca_imprevista_funcionario FOREIGN KEY (codigo_funcionario) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_srv_maquina_troca_imprevista_manutencao
    ADD CONSTRAINT fk_troca_imprevista_historico FOREIGN KEY (codigo_historico_manutencao) REFERENCES public.tb_srv_maquina_historico_manutencao(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_cliente_contato
    ADD CONSTRAINT fk_cliente_contato_cliente FOREIGN KEY (codigo_cliente) REFERENCES public.tb_cad_cliente(codigo) ON DELETE CASCADE;

ALTER TABLE ONLY public.tb_cad_cliente
    ADD CONSTRAINT fkcgevicxv5tes8676h6w4ro136 FOREIGN KEY (codigo_usuario) REFERENCES public.tb_cad_usuario(codigo);

ALTER TABLE ONLY public.tb_cad_perfil_permissao
    ADD CONSTRAINT fken9vcojqp5uogm8c1f1faocni FOREIGN KEY (codigo_permissao) REFERENCES public.tb_cad_permissao(codigo);

ALTER TABLE ONLY public.tb_cad_usuario_perfil
    ADD CONSTRAINT fkeu9lkcrkouqusc056rn3scmh7 FOREIGN KEY (codigo_perfil) REFERENCES public.tb_cad_perfil(codigo);