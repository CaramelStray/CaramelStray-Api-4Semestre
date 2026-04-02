--
-- PostgreSQL database dump
--

-- Dumped from database version 15.4 (Debian 15.4-1.pgdg110+1)
-- Dumped by pg_dump version 15.4 (Debian 15.4-1.pgdg110+1)

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

--
-- Name: tiger; Type: SCHEMA; Schema: -; Owner: user_dev
--

CREATE SCHEMA tiger;


ALTER SCHEMA tiger OWNER TO user_dev;

--
-- Name: tiger_data; Type: SCHEMA; Schema: -; Owner: user_dev
--

CREATE SCHEMA tiger_data;


ALTER SCHEMA tiger_data OWNER TO user_dev;

--
-- Name: topology; Type: SCHEMA; Schema: -; Owner: user_dev
--

CREATE SCHEMA topology;


ALTER SCHEMA topology OWNER TO user_dev;

--
-- Name: SCHEMA topology; Type: COMMENT; Schema: -; Owner: user_dev
--

COMMENT ON SCHEMA topology IS 'PostGIS Topology schema';


--
-- Name: fuzzystrmatch; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS fuzzystrmatch WITH SCHEMA public;


--
-- Name: EXTENSION fuzzystrmatch; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION fuzzystrmatch IS 'determine similarities and distance between strings';


--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry and geography spatial types and functions';


--
-- Name: postgis_tiger_geocoder; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis_tiger_geocoder WITH SCHEMA tiger;


--
-- Name: EXTENSION postgis_tiger_geocoder; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis_tiger_geocoder IS 'PostGIS tiger geocoder and reverse geocoder';


--
-- Name: postgis_topology; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis_topology WITH SCHEMA topology;


--
-- Name: EXTENSION postgis_topology; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis_topology IS 'PostGIS topology spatial types and functions';


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

--
-- Name: tb_cad_ativo_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

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

--
-- Name: tb_cad_catalogo_ativo_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

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

--
-- Name: tb_cad_catalogo_maquina_checklist_padrao; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_catalogo_maquina_checklist_padrao (
    codigo_catalogo_maquina integer NOT NULL,
    codigo_tarefa integer NOT NULL
);


ALTER TABLE public.tb_cad_catalogo_maquina_checklist_padrao OWNER TO user_dev;

--
-- Name: tb_cad_catalogo_maquina_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_cad_catalogo_maquina ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_catalogo_maquina_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_catalogo_maquina_ferramenta_modelo; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_catalogo_maquina_ferramenta_modelo (
    codigo_catalogo_maquina integer NOT NULL,
    codigo_catalogo_ativo integer NOT NULL,
    quantidade_necessaria integer DEFAULT 1 NOT NULL
);


ALTER TABLE public.tb_cad_catalogo_maquina_ferramenta_modelo OWNER TO user_dev;

--
-- Name: tb_cad_catalogo_software; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_catalogo_software (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.tb_cad_catalogo_software OWNER TO user_dev;

--
-- Name: tb_cad_catalogo_software_checklist_padrao; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_catalogo_software_checklist_padrao (
    codigo_catalogo_software integer NOT NULL,
    codigo_catalogo_tarefa integer NOT NULL,
    obrigatorio boolean NOT NULL
);


ALTER TABLE public.tb_cad_catalogo_software_checklist_padrao OWNER TO user_dev;

--
-- Name: tb_cad_catalogo_software_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_cad_catalogo_software ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_catalogo_software_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_catalogo_tarefa; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_catalogo_tarefa (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL,
    categoria character varying(100)
);


ALTER TABLE public.tb_cad_catalogo_tarefa OWNER TO user_dev;

--
-- Name: tb_cad_catalogo_tarefa_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_cad_catalogo_tarefa ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_catalogo_tarefa_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_cliente; Type: TABLE; Schema: public; Owner: user_dev
--

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
    codigo_usuario integer
);


ALTER TABLE public.tb_cad_cliente OWNER TO user_dev;

--
-- Name: tb_cad_cliente_id_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_cad_cliente ALTER COLUMN codigo ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tb_cad_cliente_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_contrato; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_contrato (
    codigo integer NOT NULL,
    codigo_cliente integer NOT NULL,
    data_inicio date,
    data_fim date,
    status character varying(100)
);


ALTER TABLE public.tb_cad_contrato OWNER TO user_dev;

--
-- Name: tb_cad_contrato_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_cad_contrato ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_contrato_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_funcionario; Type: TABLE; Schema: public; Owner: user_dev
--

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

--
-- Name: tb_cad_funcionario_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_cad_funcionario ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_funcionario_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_funcionario_habilidade; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_funcionario_habilidade (
    codigo_funcionario integer NOT NULL,
    codigo_habilidade integer NOT NULL,
    nivel integer,
    data_validade date
);


ALTER TABLE public.tb_cad_funcionario_habilidade OWNER TO user_dev;

--
-- Name: tb_cad_habilidade; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_habilidade (
    codigo integer NOT NULL,
    descricao character varying(100) NOT NULL,
    observacao character varying(255)
);


ALTER TABLE public.tb_cad_habilidade OWNER TO user_dev;

--
-- Name: tb_cad_habilidade_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_cad_habilidade ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_habilidade_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_perfil; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_perfil (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.tb_cad_perfil OWNER TO user_dev;

--
-- Name: tb_cad_perfil_id_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_cad_perfil ALTER COLUMN codigo ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tb_cad_perfil_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_perfil_permissao; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_perfil_permissao (
    codigo_perfil integer NOT NULL,
    codigo_permissao integer NOT NULL
);


ALTER TABLE public.tb_cad_perfil_permissao OWNER TO user_dev;

--
-- Name: tb_cad_permissao; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_permissao (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.tb_cad_permissao OWNER TO user_dev;

--
-- Name: tb_cad_permissao_id_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_cad_permissao ALTER COLUMN codigo ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tb_cad_permissao_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_tipo_manutencao; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_tipo_manutencao (
    codigo integer NOT NULL,
    descricao character varying(100) NOT NULL,
    observacao character varying(255)
);


ALTER TABLE public.tb_cad_tipo_manutencao OWNER TO user_dev;

--
-- Name: tb_cad_tipo_manutencao_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_cad_tipo_manutencao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_tipo_manutencao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_tipo_manutencao_habilidade; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_tipo_manutencao_habilidade (
    codigo_tipo_manutencao integer NOT NULL,
    codigo_habilidade integer NOT NULL,
    obrigatoria boolean DEFAULT true NOT NULL
);


ALTER TABLE public.tb_cad_tipo_manutencao_habilidade OWNER TO user_dev;

--
-- Name: tb_cad_usuario; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_usuario (
    codigo integer NOT NULL,
    ativo boolean NOT NULL,
    data_criacao timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP,
    email character varying(255) NOT NULL,
    senha character varying(255) NOT NULL
);


ALTER TABLE public.tb_cad_usuario OWNER TO user_dev;

--
-- Name: tb_cad_usuario_id_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_cad_usuario ALTER COLUMN codigo ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tb_cad_usuario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_usuario_perfil; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_usuario_perfil (
    codigo_usuario integer NOT NULL,
    codigo_perfil integer NOT NULL
);


ALTER TABLE public.tb_cad_usuario_perfil OWNER TO user_dev;

--
-- Name: tb_srv_maquina_checklist_ferramenta_manutencao; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_srv_maquina_checklist_ferramenta_manutencao (
    codigo integer NOT NULL,
    codigo_historico_manutencao integer NOT NULL,
    codigo_catalogo_ativo integer NOT NULL,
    codigo_ativo integer,
    levou boolean DEFAULT false NOT NULL
);


ALTER TABLE public.tb_srv_maquina_checklist_ferramenta_manutencao OWNER TO user_dev;

--
-- Name: tb_srv_maquina_checklist_ferramenta_manutencao_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_maquina_checklist_ferramenta_manutencao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_checklist_ferramenta_manutencao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_srv_maquina_checklist_manutencao; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_srv_maquina_checklist_manutencao (
    codigo integer NOT NULL,
    codigo_historico_manutencao integer NOT NULL,
    codigo_tarefa integer NOT NULL,
    realizado boolean,
    observacao_tarefa text
);


ALTER TABLE public.tb_srv_maquina_checklist_manutencao OWNER TO user_dev;

--
-- Name: tb_srv_maquina_checklist_manutencao_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_maquina_checklist_manutencao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_checklist_manutencao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_srv_maquina_contrato; Type: TABLE; Schema: public; Owner: user_dev
--

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

--
-- Name: tb_srv_maquina_contrato_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_maquina_contrato ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_contrato_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_srv_maquina_funcionario_manutencao; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_srv_maquina_funcionario_manutencao (
    codigo_historico_manutencao integer NOT NULL,
    codigo_funcionario integer NOT NULL
);


ALTER TABLE public.tb_srv_maquina_funcionario_manutencao OWNER TO user_dev;

--
-- Name: tb_srv_maquina_historico_manutencao; Type: TABLE; Schema: public; Owner: user_dev
--

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
    observacao_geral text
);


ALTER TABLE public.tb_srv_maquina_historico_manutencao OWNER TO user_dev;

--
-- Name: tb_srv_maquina_historico_manutencao_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_maquina_historico_manutencao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_historico_manutencao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_srv_maquina_observacao_manutencao; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_srv_maquina_observacao_manutencao (
    codigo integer NOT NULL,
    codigo_historico_manutencao integer NOT NULL,
    codigo_funcionario integer NOT NULL,
    observacao text,
    data_criacao timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.tb_srv_maquina_observacao_manutencao OWNER TO user_dev;

--
-- Name: tb_srv_maquina_observacao_manutencao_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_maquina_observacao_manutencao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_observacao_manutencao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_srv_maquina_software_instalado; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_srv_maquina_software_instalado (
    codigo integer NOT NULL,
    codigo_maquina_contrato integer NOT NULL,
    codigo_software integer NOT NULL,
    chave_licenca character varying(255),
    versao_instalada character varying(100)
);


ALTER TABLE public.tb_srv_maquina_software_instalado OWNER TO user_dev;

--
-- Name: tb_srv_maquina_software_instalado_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_maquina_software_instalado ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_software_instalado_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_srv_maquina_troca_imprevista_manutencao; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_srv_maquina_troca_imprevista_manutencao (
    codigo integer NOT NULL,
    codigo_historico_manutencao integer NOT NULL,
    codigo_funcionario integer NOT NULL,
    codigo_ativo_utilizado integer NOT NULL,
    observacao text,
    data_criacao timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.tb_srv_maquina_troca_imprevista_manutencao OWNER TO user_dev;

--
-- Name: tb_srv_maquina_troca_imprevista_manutencao_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_maquina_troca_imprevista_manutencao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_maquina_troca_imprevista_manutencao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Data for Name: spatial_ref_sys; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.spatial_ref_sys (srid, auth_name, auth_srid, srtext, proj4text) FROM stdin;
\.


--
-- Data for Name: tb_cad_ativo; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_ativo (codigo, codigo_catalogo_ativo, numero_serie, lote, descricao, codigo_funcionario_responsavel, status) FROM stdin;
\.


--
-- Data for Name: tb_cad_ativo_estoque; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_ativo_estoque (codigo_catalogo_ativo, quantidade) FROM stdin;
\.


--
-- Data for Name: tb_cad_catalogo_ativo; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_ativo (codigo, descricao_produto, modelo, marca, descricao, especificacao, tipo) FROM stdin;
\.


--
-- Data for Name: tb_cad_catalogo_maquina; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_maquina (codigo, descricao, especificacao, limite_manutencao) FROM stdin;
\.


--
-- Data for Name: tb_cad_catalogo_maquina_checklist_padrao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_maquina_checklist_padrao (codigo_catalogo_maquina, codigo_tarefa) FROM stdin;
\.


--
-- Data for Name: tb_cad_catalogo_maquina_ferramenta_modelo; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_maquina_ferramenta_modelo (codigo_catalogo_maquina, codigo_catalogo_ativo, quantidade_necessaria) FROM stdin;
\.


--
-- Data for Name: tb_cad_catalogo_software; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_software (codigo, descricao) FROM stdin;
\.


--
-- Data for Name: tb_cad_catalogo_software_checklist_padrao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_software_checklist_padrao (codigo_catalogo_software, codigo_catalogo_tarefa, obrigatorio) FROM stdin;
\.


--
-- Data for Name: tb_cad_catalogo_tarefa; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_tarefa (codigo, descricao, categoria) FROM stdin;
\.


--
-- Data for Name: tb_cad_cliente; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_cliente (codigo, ativo, cidade, classificacao_distancia, data_cadastro, documento, email_contato, estado_regiao, fuso_horario, descricao_empresa, descricao_responsavel, pais, telefone_contato, codigo_usuario) FROM stdin;
1	t	Campinas	Regional	2026-03-27 21:20:28.403828	11.111.111/0001-11	contato@alpha.com	Sao Paulo	America/Sao_Paulo	Alpha Industrial	Ana Souza	Brasil	11999990001	1
2	t	Niteroi	Nacional	2026-03-27 21:20:28.64737	22.222.222/0001-22	contato@beta.com	Rio de Janeiro	America/Sao_Paulo	Beta Logistics	Bruno Lima	Brasil	21999990002	1
3	f	Belo Horizonte	Internacional	2026-03-27 21:20:28.648821	33.333.333/0001-33	contato@gamma.com	Minas Gerais	America/Sao_Paulo	Gamma Services	Carla Mendes	Brasil	31999990003	1
4	t	Sao Paulo	Nacional	2026-03-27 21:29:26.053489	44.444.444/0001-44	contato@deltatecnologia.com	Sao Paulo	America/Sao_Paulo	Delta Tecnologia	Diego Martins	Brasil	11988887777	1
\.


--
-- Data for Name: tb_cad_contrato; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_contrato (codigo, codigo_cliente, data_inicio, data_fim, status) FROM stdin;
\.


--
-- Data for Name: tb_cad_funcionario; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_funcionario (codigo, codigo_usuario, nome, cpf, cargo, telefone, latitude, longitude) FROM stdin;
\.


--
-- Data for Name: tb_cad_funcionario_habilidade; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_funcionario_habilidade (codigo_funcionario, codigo_habilidade, nivel, data_validade) FROM stdin;
\.


--
-- Data for Name: tb_cad_habilidade; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_habilidade (codigo, descricao, observacao) FROM stdin;
\.


--
-- Data for Name: tb_cad_perfil; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_perfil (codigo, descricao) FROM stdin;
1	ROLE_ADMIN
\.


--
-- Data for Name: tb_cad_perfil_permissao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_perfil_permissao (codigo_perfil, codigo_permissao) FROM stdin;
\.


--
-- Data for Name: tb_cad_permissao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_permissao (codigo, descricao) FROM stdin;
\.


--
-- Data for Name: tb_cad_tipo_manutencao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_tipo_manutencao (codigo, descricao, observacao) FROM stdin;
\.


--
-- Data for Name: tb_cad_tipo_manutencao_habilidade; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_tipo_manutencao_habilidade (codigo_tipo_manutencao, codigo_habilidade, obrigatoria) FROM stdin;
\.


--
-- Data for Name: tb_cad_usuario; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_usuario (codigo, ativo, data_criacao, email, senha) FROM stdin;
1	t	2026-03-27 21:18:55.151169	admin@tracker.com	$2y$10$ejmYLdvzjgmq3z/dJecyl.fiaducLy0xWavsFzTt5/THg1iZmpWRK
\.


--
-- Data for Name: tb_cad_usuario_perfil; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_usuario_perfil (codigo_usuario, codigo_perfil) FROM stdin;
1	1
\.


--
-- Data for Name: tb_srv_maquina_checklist_ferramenta_manutencao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_maquina_checklist_ferramenta_manutencao (codigo, codigo_historico_manutencao, codigo_catalogo_ativo, codigo_ativo, levou) FROM stdin;
\.


--
-- Data for Name: tb_srv_maquina_checklist_manutencao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_maquina_checklist_manutencao (codigo, codigo_historico_manutencao, codigo_tarefa, realizado, observacao_tarefa) FROM stdin;
\.


--
-- Data for Name: tb_srv_maquina_contrato; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_maquina_contrato (codigo, codigo_contrato, codigo_catalogo_maquina, numero_serie, data_instalacao, manutencao_feita, trabalho_em_altura, latitude, longitude) FROM stdin;
\.


--
-- Data for Name: tb_srv_maquina_funcionario_manutencao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_maquina_funcionario_manutencao (codigo_historico_manutencao, codigo_funcionario) FROM stdin;
\.


--
-- Data for Name: tb_srv_maquina_historico_manutencao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_maquina_historico_manutencao (codigo, codigo_maquina_contrato, codigo_software_instalado, codigo_tipo_manutencao, status, criticidade, vencimento, data_agendamento, data_inicio_execucao, data_fim_execucao, observacao_geral) FROM stdin;
\.


--
-- Data for Name: tb_srv_maquina_observacao_manutencao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_maquina_observacao_manutencao (codigo, codigo_historico_manutencao, codigo_funcionario, observacao, data_criacao) FROM stdin;
\.


--
-- Data for Name: tb_srv_maquina_software_instalado; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_maquina_software_instalado (codigo, codigo_maquina_contrato, codigo_software, chave_licenca, versao_instalada) FROM stdin;
\.


--
-- Data for Name: tb_srv_maquina_troca_imprevista_manutencao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_maquina_troca_imprevista_manutencao (codigo, codigo_historico_manutencao, codigo_funcionario, codigo_ativo_utilizado, observacao, data_criacao) FROM stdin;
\.


--
-- Data for Name: geocode_settings; Type: TABLE DATA; Schema: tiger; Owner: user_dev
--

COPY tiger.geocode_settings (name, setting, unit, category, short_desc) FROM stdin;
\.


--
-- Data for Name: pagc_gaz; Type: TABLE DATA; Schema: tiger; Owner: user_dev
--

COPY tiger.pagc_gaz (id, seq, word, stdword, token, is_custom) FROM stdin;
\.


--
-- Data for Name: pagc_lex; Type: TABLE DATA; Schema: tiger; Owner: user_dev
--

COPY tiger.pagc_lex (id, seq, word, stdword, token, is_custom) FROM stdin;
\.


--
-- Data for Name: pagc_rules; Type: TABLE DATA; Schema: tiger; Owner: user_dev
--

COPY tiger.pagc_rules (id, rule, is_custom) FROM stdin;
\.


--
-- Data for Name: topology; Type: TABLE DATA; Schema: topology; Owner: user_dev
--

COPY topology.topology (id, name, srid, "precision", hasz) FROM stdin;
\.


--
-- Data for Name: layer; Type: TABLE DATA; Schema: topology; Owner: user_dev
--

COPY topology.layer (topology_id, layer_id, schema_name, table_name, feature_column, feature_type, level, child_id) FROM stdin;
\.


--
-- Name: tb_cad_ativo_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_ativo_codigo_seq', 1, false);


--
-- Name: tb_cad_catalogo_ativo_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_catalogo_ativo_codigo_seq', 1, false);


--
-- Name: tb_cad_catalogo_maquina_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_catalogo_maquina_codigo_seq', 1, false);


--
-- Name: tb_cad_catalogo_software_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_catalogo_software_codigo_seq', 1, false);


--
-- Name: tb_cad_catalogo_tarefa_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_catalogo_tarefa_codigo_seq', 1, false);


--
-- Name: tb_cad_cliente_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_cliente_id_seq', 4, true);


--
-- Name: tb_cad_contrato_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_contrato_codigo_seq', 1, false);


--
-- Name: tb_cad_funcionario_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_funcionario_codigo_seq', 1, false);


--
-- Name: tb_cad_habilidade_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_habilidade_codigo_seq', 1, false);


--
-- Name: tb_cad_perfil_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_perfil_id_seq', 1, true);


--
-- Name: tb_cad_permissao_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_permissao_id_seq', 1, false);


--
-- Name: tb_cad_tipo_manutencao_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_tipo_manutencao_codigo_seq', 1, false);


--
-- Name: tb_cad_usuario_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_usuario_id_seq', 1, true);


--
-- Name: tb_srv_maquina_checklist_ferramenta_manutencao_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_maquina_checklist_ferramenta_manutencao_codigo_seq', 1, false);


--
-- Name: tb_srv_maquina_checklist_manutencao_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_maquina_checklist_manutencao_codigo_seq', 1, false);


--
-- Name: tb_srv_maquina_contrato_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_maquina_contrato_codigo_seq', 1, false);


--
-- Name: tb_srv_maquina_historico_manutencao_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_maquina_historico_manutencao_codigo_seq', 1, false);


--
-- Name: tb_srv_maquina_observacao_manutencao_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_maquina_observacao_manutencao_codigo_seq', 1, false);


--
-- Name: tb_srv_maquina_software_instalado_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_maquina_software_instalado_codigo_seq', 1, false);


--
-- Name: tb_srv_maquina_troca_imprevista_manutencao_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_maquina_troca_imprevista_manutencao_codigo_seq', 1, false);


--
-- Name: topology_id_seq; Type: SEQUENCE SET; Schema: topology; Owner: user_dev
--

SELECT pg_catalog.setval('topology.topology_id_seq', 1, false);


--
-- Name: tb_cad_catalogo_maquina_checklist_padrao pk_checklist_padrao_maquina; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_maquina_checklist_padrao
    ADD CONSTRAINT pk_checklist_padrao_maquina PRIMARY KEY (codigo_catalogo_maquina, codigo_tarefa);


--
-- Name: tb_cad_catalogo_software_checklist_padrao pk_checklist_padrao_software; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_software_checklist_padrao
    ADD CONSTRAINT pk_checklist_padrao_software PRIMARY KEY (codigo_catalogo_software, codigo_catalogo_tarefa);


--
-- Name: tb_cad_catalogo_maquina_ferramenta_modelo pk_ferramenta_modelo_maquina; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_maquina_ferramenta_modelo
    ADD CONSTRAINT pk_ferramenta_modelo_maquina PRIMARY KEY (codigo_catalogo_maquina, codigo_catalogo_ativo);


--
-- Name: tb_cad_funcionario_habilidade pk_funcionario_habilidade; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_funcionario_habilidade
    ADD CONSTRAINT pk_funcionario_habilidade PRIMARY KEY (codigo_funcionario, codigo_habilidade);


--
-- Name: tb_srv_maquina_funcionario_manutencao pk_funcionario_manutencao; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_funcionario_manutencao
    ADD CONSTRAINT pk_funcionario_manutencao PRIMARY KEY (codigo_historico_manutencao, codigo_funcionario);


--
-- Name: tb_cad_tipo_manutencao_habilidade pk_tipo_manutencao_habilidade; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_tipo_manutencao_habilidade
    ADD CONSTRAINT pk_tipo_manutencao_habilidade PRIMARY KEY (codigo_tipo_manutencao, codigo_habilidade);


--
-- Name: tb_cad_ativo_estoque tb_cad_ativo_estoque_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_ativo_estoque
    ADD CONSTRAINT tb_cad_ativo_estoque_pkey PRIMARY KEY (codigo_catalogo_ativo);


--
-- Name: tb_cad_ativo tb_cad_ativo_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_ativo
    ADD CONSTRAINT tb_cad_ativo_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_catalogo_ativo tb_cad_catalogo_ativo_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_ativo
    ADD CONSTRAINT tb_cad_catalogo_ativo_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_catalogo_maquina tb_cad_catalogo_maquina_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_maquina
    ADD CONSTRAINT tb_cad_catalogo_maquina_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_catalogo_software tb_cad_catalogo_software_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_software
    ADD CONSTRAINT tb_cad_catalogo_software_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_catalogo_tarefa tb_cad_catalogo_tarefa_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_tarefa
    ADD CONSTRAINT tb_cad_catalogo_tarefa_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_cliente tb_cad_cliente_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_cliente
    ADD CONSTRAINT tb_cad_cliente_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_contrato tb_cad_contrato_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_contrato
    ADD CONSTRAINT tb_cad_contrato_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_funcionario tb_cad_funcionario_codigo_usuario_key; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_funcionario
    ADD CONSTRAINT tb_cad_funcionario_codigo_usuario_key UNIQUE (codigo_usuario);


--
-- Name: tb_cad_funcionario tb_cad_funcionario_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_funcionario
    ADD CONSTRAINT tb_cad_funcionario_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_habilidade tb_cad_habilidade_descricao_key; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_habilidade
    ADD CONSTRAINT tb_cad_habilidade_descricao_key UNIQUE (descricao);


--
-- Name: tb_cad_habilidade tb_cad_habilidade_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_habilidade
    ADD CONSTRAINT tb_cad_habilidade_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_perfil_permissao tb_cad_perfil_permissao_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_perfil_permissao
    ADD CONSTRAINT tb_cad_perfil_permissao_pkey PRIMARY KEY (codigo_perfil, codigo_permissao);


--
-- Name: tb_cad_perfil tb_cad_perfil_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_perfil
    ADD CONSTRAINT tb_cad_perfil_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_permissao tb_cad_permissao_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_permissao
    ADD CONSTRAINT tb_cad_permissao_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_tipo_manutencao tb_cad_tipo_manutencao_descricao_key; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_tipo_manutencao
    ADD CONSTRAINT tb_cad_tipo_manutencao_descricao_key UNIQUE (descricao);


--
-- Name: tb_cad_tipo_manutencao tb_cad_tipo_manutencao_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_tipo_manutencao
    ADD CONSTRAINT tb_cad_tipo_manutencao_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_usuario_perfil tb_cad_usuario_perfil_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_usuario_perfil
    ADD CONSTRAINT tb_cad_usuario_perfil_pkey PRIMARY KEY (codigo_usuario, codigo_perfil);


--
-- Name: tb_cad_usuario tb_cad_usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_usuario
    ADD CONSTRAINT tb_cad_usuario_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_maquina_checklist_ferramenta_manutencao tb_srv_maquina_checklist_ferramenta_manutencao_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_checklist_ferramenta_manutencao
    ADD CONSTRAINT tb_srv_maquina_checklist_ferramenta_manutencao_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_maquina_checklist_manutencao tb_srv_maquina_checklist_manutencao_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_checklist_manutencao
    ADD CONSTRAINT tb_srv_maquina_checklist_manutencao_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_maquina_contrato tb_srv_maquina_contrato_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_contrato
    ADD CONSTRAINT tb_srv_maquina_contrato_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_maquina_historico_manutencao tb_srv_maquina_historico_manutencao_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_historico_manutencao
    ADD CONSTRAINT tb_srv_maquina_historico_manutencao_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_maquina_observacao_manutencao tb_srv_maquina_observacao_manutencao_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_observacao_manutencao
    ADD CONSTRAINT tb_srv_maquina_observacao_manutencao_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_maquina_software_instalado tb_srv_maquina_software_instalado_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_software_instalado
    ADD CONSTRAINT tb_srv_maquina_software_instalado_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_maquina_troca_imprevista_manutencao tb_srv_maquina_troca_imprevista_manutencao_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_troca_imprevista_manutencao
    ADD CONSTRAINT tb_srv_maquina_troca_imprevista_manutencao_pkey PRIMARY KEY (codigo);


--
-- Name: tb_cad_usuario uk632kb8n9udn5mba0exa9qs501; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_usuario
    ADD CONSTRAINT uk632kb8n9udn5mba0exa9qs501 UNIQUE (email);


--
-- Name: tb_cad_usuario_perfil fk6f1v3k7v61mt1n6u6lpt2x0e3; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_usuario_perfil
    ADD CONSTRAINT fk6f1v3k7v61mt1n6u6lpt2x0e3 FOREIGN KEY (codigo_usuario) REFERENCES public.tb_cad_usuario(codigo);


--
-- Name: tb_cad_perfil_permissao fk9xue5r7rqj3ujnmeb7gvwnhnu; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_perfil_permissao
    ADD CONSTRAINT fk9xue5r7rqj3ujnmeb7gvwnhnu FOREIGN KEY (codigo_perfil) REFERENCES public.tb_cad_perfil(codigo);


--
-- Name: tb_cad_ativo fk_ativo_catalogo; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_ativo
    ADD CONSTRAINT fk_ativo_catalogo FOREIGN KEY (codigo_catalogo_ativo) REFERENCES public.tb_cad_catalogo_ativo(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_ativo_estoque fk_ativo_estoque_catalogo; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_ativo_estoque
    ADD CONSTRAINT fk_ativo_estoque_catalogo FOREIGN KEY (codigo_catalogo_ativo) REFERENCES public.tb_cad_catalogo_ativo(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_ativo fk_ativo_funcionario; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_ativo
    ADD CONSTRAINT fk_ativo_funcionario FOREIGN KEY (codigo_funcionario_responsavel) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_checklist_ferramenta_manutencao fk_checklist_ferramenta_ativo; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_checklist_ferramenta_manutencao
    ADD CONSTRAINT fk_checklist_ferramenta_ativo FOREIGN KEY (codigo_ativo) REFERENCES public.tb_cad_ativo(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_checklist_ferramenta_manutencao fk_checklist_ferramenta_catalogo_ativo; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_checklist_ferramenta_manutencao
    ADD CONSTRAINT fk_checklist_ferramenta_catalogo_ativo FOREIGN KEY (codigo_catalogo_ativo) REFERENCES public.tb_cad_catalogo_ativo(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_checklist_ferramenta_manutencao fk_checklist_ferramenta_historico; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_checklist_ferramenta_manutencao
    ADD CONSTRAINT fk_checklist_ferramenta_historico FOREIGN KEY (codigo_historico_manutencao) REFERENCES public.tb_srv_maquina_historico_manutencao(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_checklist_manutencao fk_checklist_manutencao_historico; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_checklist_manutencao
    ADD CONSTRAINT fk_checklist_manutencao_historico FOREIGN KEY (codigo_historico_manutencao) REFERENCES public.tb_srv_maquina_historico_manutencao(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_checklist_manutencao fk_checklist_manutencao_tarefa; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_checklist_manutencao
    ADD CONSTRAINT fk_checklist_manutencao_tarefa FOREIGN KEY (codigo_tarefa) REFERENCES public.tb_cad_catalogo_tarefa(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_catalogo_maquina_checklist_padrao fk_checklist_padrao_maquina_catalogo; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_maquina_checklist_padrao
    ADD CONSTRAINT fk_checklist_padrao_maquina_catalogo FOREIGN KEY (codigo_catalogo_maquina) REFERENCES public.tb_cad_catalogo_maquina(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_catalogo_maquina_checklist_padrao fk_checklist_padrao_maquina_tarefa; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_maquina_checklist_padrao
    ADD CONSTRAINT fk_checklist_padrao_maquina_tarefa FOREIGN KEY (codigo_tarefa) REFERENCES public.tb_cad_catalogo_tarefa(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_catalogo_software_checklist_padrao fk_checklist_padrao_software_software; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_software_checklist_padrao
    ADD CONSTRAINT fk_checklist_padrao_software_software FOREIGN KEY (codigo_catalogo_software) REFERENCES public.tb_cad_catalogo_software(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_catalogo_software_checklist_padrao fk_checklist_padrao_software_tarefa; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_software_checklist_padrao
    ADD CONSTRAINT fk_checklist_padrao_software_tarefa FOREIGN KEY (codigo_catalogo_tarefa) REFERENCES public.tb_cad_catalogo_tarefa(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_contrato fk_contrato_cliente; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_contrato
    ADD CONSTRAINT fk_contrato_cliente FOREIGN KEY (codigo_cliente) REFERENCES public.tb_cad_cliente(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_catalogo_maquina_ferramenta_modelo fk_ferramenta_modelo_maquina_catalogo_ativo; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_maquina_ferramenta_modelo
    ADD CONSTRAINT fk_ferramenta_modelo_maquina_catalogo_ativo FOREIGN KEY (codigo_catalogo_ativo) REFERENCES public.tb_cad_catalogo_ativo(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_catalogo_maquina_ferramenta_modelo fk_ferramenta_modelo_maquina_catalogo_maquina; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_catalogo_maquina_ferramenta_modelo
    ADD CONSTRAINT fk_ferramenta_modelo_maquina_catalogo_maquina FOREIGN KEY (codigo_catalogo_maquina) REFERENCES public.tb_cad_catalogo_maquina(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_funcionario_habilidade fk_funcionario_habilidade_funcionario; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_funcionario_habilidade
    ADD CONSTRAINT fk_funcionario_habilidade_funcionario FOREIGN KEY (codigo_funcionario) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_funcionario_habilidade fk_funcionario_habilidade_habilidade; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_funcionario_habilidade
    ADD CONSTRAINT fk_funcionario_habilidade_habilidade FOREIGN KEY (codigo_habilidade) REFERENCES public.tb_cad_habilidade(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_funcionario_manutencao fk_funcionario_manutencao_funcionario; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_funcionario_manutencao
    ADD CONSTRAINT fk_funcionario_manutencao_funcionario FOREIGN KEY (codigo_funcionario) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_funcionario_manutencao fk_funcionario_manutencao_historico; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_funcionario_manutencao
    ADD CONSTRAINT fk_funcionario_manutencao_historico FOREIGN KEY (codigo_historico_manutencao) REFERENCES public.tb_srv_maquina_historico_manutencao(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_funcionario fk_funcionario_usuario; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_funcionario
    ADD CONSTRAINT fk_funcionario_usuario FOREIGN KEY (codigo_usuario) REFERENCES public.tb_cad_usuario(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_historico_manutencao fk_historico_manutencao_maquina; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_historico_manutencao
    ADD CONSTRAINT fk_historico_manutencao_maquina FOREIGN KEY (codigo_maquina_contrato) REFERENCES public.tb_srv_maquina_contrato(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_historico_manutencao fk_historico_manutencao_software; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_historico_manutencao
    ADD CONSTRAINT fk_historico_manutencao_software FOREIGN KEY (codigo_software_instalado) REFERENCES public.tb_srv_maquina_software_instalado(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_historico_manutencao fk_historico_manutencao_tipo; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_historico_manutencao
    ADD CONSTRAINT fk_historico_manutencao_tipo FOREIGN KEY (codigo_tipo_manutencao) REFERENCES public.tb_cad_tipo_manutencao(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_contrato fk_maquina_contrato_catalogo; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_contrato
    ADD CONSTRAINT fk_maquina_contrato_catalogo FOREIGN KEY (codigo_catalogo_maquina) REFERENCES public.tb_cad_catalogo_maquina(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_contrato fk_maquina_contrato_contrato; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_contrato
    ADD CONSTRAINT fk_maquina_contrato_contrato FOREIGN KEY (codigo_contrato) REFERENCES public.tb_cad_contrato(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_observacao_manutencao fk_observacao_manutencao_funcionario; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_observacao_manutencao
    ADD CONSTRAINT fk_observacao_manutencao_funcionario FOREIGN KEY (codigo_funcionario) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_observacao_manutencao fk_observacao_manutencao_historico; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_observacao_manutencao
    ADD CONSTRAINT fk_observacao_manutencao_historico FOREIGN KEY (codigo_historico_manutencao) REFERENCES public.tb_srv_maquina_historico_manutencao(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_software_instalado fk_software_instalado_maquina; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_software_instalado
    ADD CONSTRAINT fk_software_instalado_maquina FOREIGN KEY (codigo_maquina_contrato) REFERENCES public.tb_srv_maquina_contrato(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_software_instalado fk_software_instalado_software; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_software_instalado
    ADD CONSTRAINT fk_software_instalado_software FOREIGN KEY (codigo_software) REFERENCES public.tb_cad_catalogo_software(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_tipo_manutencao_habilidade fk_tipo_manutencao_habilidade_habilidade; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_tipo_manutencao_habilidade
    ADD CONSTRAINT fk_tipo_manutencao_habilidade_habilidade FOREIGN KEY (codigo_habilidade) REFERENCES public.tb_cad_habilidade(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_tipo_manutencao_habilidade fk_tipo_manutencao_habilidade_tipo; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_tipo_manutencao_habilidade
    ADD CONSTRAINT fk_tipo_manutencao_habilidade_tipo FOREIGN KEY (codigo_tipo_manutencao) REFERENCES public.tb_cad_tipo_manutencao(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_troca_imprevista_manutencao fk_troca_imprevista_ativo; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_troca_imprevista_manutencao
    ADD CONSTRAINT fk_troca_imprevista_ativo FOREIGN KEY (codigo_ativo_utilizado) REFERENCES public.tb_cad_ativo(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_troca_imprevista_manutencao fk_troca_imprevista_funcionario; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_troca_imprevista_manutencao
    ADD CONSTRAINT fk_troca_imprevista_funcionario FOREIGN KEY (codigo_funcionario) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_maquina_troca_imprevista_manutencao fk_troca_imprevista_historico; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_troca_imprevista_manutencao
    ADD CONSTRAINT fk_troca_imprevista_historico FOREIGN KEY (codigo_historico_manutencao) REFERENCES public.tb_srv_maquina_historico_manutencao(codigo) ON DELETE CASCADE;


--
-- Name: tb_cad_cliente fkcgevicxv5tes8676h6w4ro136; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_cliente
    ADD CONSTRAINT fkcgevicxv5tes8676h6w4ro136 FOREIGN KEY (codigo_usuario) REFERENCES public.tb_cad_usuario(codigo);


--
-- Name: tb_cad_perfil_permissao fken9vcojqp5uogm8c1f1faocni; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_perfil_permissao
    ADD CONSTRAINT fken9vcojqp5uogm8c1f1faocni FOREIGN KEY (codigo_permissao) REFERENCES public.tb_cad_permissao(codigo);


--
-- Name: tb_cad_usuario_perfil fkeu9lkcrkouqusc056rn3scmh7; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_usuario_perfil
    ADD CONSTRAINT fkeu9lkcrkouqusc056rn3scmh7 FOREIGN KEY (codigo_perfil) REFERENCES public.tb_cad_perfil(codigo);


--
-- PostgreSQL database dump complete
--

