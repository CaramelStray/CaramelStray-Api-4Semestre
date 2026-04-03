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

ALTER TABLE IF EXISTS ONLY public.tb_srv_contrato_ativo DROP CONSTRAINT IF EXISTS tb_srv_contrato_ativo_pkey;
DROP TABLE IF EXISTS public.tb_srv_maquina_historico_manutencao;
DROP TABLE IF EXISTS public.tb_srv_maquina_funcionario_manutencao;
DROP TABLE IF EXISTS public.tb_srv_maquina_contrato;
DROP TABLE IF EXISTS public.tb_srv_maquina_checklist_manutencao;
DROP TABLE IF EXISTS public.tb_srv_maquina_checklist_ferramenta_manutencao;
DROP TABLE IF EXISTS public.tb_srv_contrato_ativo;
DROP TABLE IF EXISTS public.tb_cad_usuario_perfil;
DROP TABLE IF EXISTS public.tb_cad_usuario;
DROP TABLE IF EXISTS public.tb_cad_tipo_manutencao_habilidade;
DROP TABLE IF EXISTS public.tb_cad_tipo_manutencao;
DROP TABLE IF EXISTS public.tb_cad_permissao;
DROP TABLE IF EXISTS public.tb_cad_perfil_permissao;
DROP TABLE IF EXISTS public.tb_cad_perfil;
DROP TABLE IF EXISTS public.tb_cad_habilidade;
DROP TABLE IF EXISTS public.tb_cad_funcionario_habilidade;
DROP TABLE IF EXISTS public.tb_cad_funcionario;
DROP TABLE IF EXISTS public.tb_cad_contrato;
DROP TABLE IF EXISTS public.tb_cad_cliente_contato;
DROP TABLE IF EXISTS public.tb_cad_cliente;
DROP TABLE IF EXISTS public.tb_cad_catalogo_tarefa;
DROP TABLE IF EXISTS public.tb_cad_catalogo_software_checklist_padrao;
DROP TABLE IF EXISTS public.tb_cad_catalogo_software;
DROP TABLE IF EXISTS public.tb_cad_catalogo_maquina_ferramenta_modelo;
DROP TABLE IF EXISTS public.tb_cad_catalogo_maquina_checklist_padrao;
DROP TABLE IF EXISTS public.tb_cad_catalogo_maquina;
DROP TABLE IF EXISTS public.tb_cad_catalogo_ativo;
DROP TABLE IF EXISTS public.tb_cad_ativo_estoque;
DROP TABLE IF EXISTS public.tb_cad_ativo;
DROP EXTENSION IF EXISTS postgis_topology;
DROP EXTENSION IF EXISTS postgis_tiger_geocoder;
DROP EXTENSION IF EXISTS postgis;
DROP EXTENSION IF EXISTS pgcrypto;
DROP EXTENSION IF EXISTS fuzzystrmatch;
DROP SCHEMA IF EXISTS topology;
DROP SCHEMA IF EXISTS tiger_data;
DROP SCHEMA IF EXISTS tiger;
--
-- Name: tiger; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA tiger;


--
-- Name: tiger_data; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA tiger_data;


--
-- Name: topology; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA topology;


--
-- Name: SCHEMA topology; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA topology IS 'PostGIS Topology schema';


--
-- Name: fuzzystrmatch; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS fuzzystrmatch WITH SCHEMA public;


--
-- Name: EXTENSION fuzzystrmatch; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION fuzzystrmatch IS 'determine similarities and distance between strings';


--
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry and geography spatial types and functions';


--
-- Name: postgis_tiger_geocoder; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis_tiger_geocoder WITH SCHEMA tiger;


--
-- Name: EXTENSION postgis_tiger_geocoder; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION postgis_tiger_geocoder IS 'PostGIS tiger geocoder and reverse geocoder';


--
-- Name: postgis_topology; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis_topology WITH SCHEMA topology;


--
-- Name: EXTENSION postgis_topology; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION postgis_topology IS 'PostGIS topology spatial types and functions';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: tb_cad_ativo; Type: TABLE; Schema: public; Owner: -
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


--
-- Name: tb_cad_ativo_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_ativo_estoque; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_ativo_estoque (
    codigo_catalogo_ativo integer NOT NULL,
    quantidade integer DEFAULT 0 NOT NULL
);


--
-- Name: tb_cad_catalogo_ativo; Type: TABLE; Schema: public; Owner: -
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


--
-- Name: tb_cad_catalogo_ativo_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_catalogo_maquina; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_catalogo_maquina (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL,
    especificacao text,
    limite_manutencao character varying(100)
);


--
-- Name: tb_cad_catalogo_maquina_checklist_padrao; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_catalogo_maquina_checklist_padrao (
    codigo_catalogo_maquina integer NOT NULL,
    codigo_tarefa integer NOT NULL
);


--
-- Name: tb_cad_catalogo_maquina_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_catalogo_maquina_ferramenta_modelo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_catalogo_maquina_ferramenta_modelo (
    codigo_catalogo_maquina integer NOT NULL,
    codigo_catalogo_ativo integer NOT NULL,
    quantidade_necessaria integer DEFAULT 1 NOT NULL
);


--
-- Name: tb_cad_catalogo_software; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_catalogo_software (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL
);


--
-- Name: tb_cad_catalogo_software_checklist_padrao; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_catalogo_software_checklist_padrao (
    codigo_catalogo_software integer NOT NULL,
    codigo_catalogo_tarefa integer NOT NULL,
    obrigatorio boolean NOT NULL
);


--
-- Name: tb_cad_catalogo_software_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_catalogo_tarefa; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_catalogo_tarefa (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL,
    categoria character varying(100)
);


--
-- Name: tb_cad_catalogo_tarefa_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_cliente; Type: TABLE; Schema: public; Owner: -
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
    codigo_usuario integer,
    observacao text,
    rua character varying(255),
    numero character varying(50),
    internacional boolean DEFAULT false
);


--
-- Name: tb_cad_cliente_contato; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_cliente_contato (
    codigo integer NOT NULL,
    codigo_cliente integer NOT NULL,
    nome character varying(255) NOT NULL,
    email character varying(255),
    telefone character varying(30)
);


--
-- Name: tb_cad_cliente_contato_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.tb_cad_cliente_contato ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_cliente_contato_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_cliente_id_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_contrato; Type: TABLE; Schema: public; Owner: -
--

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


--
-- Name: tb_cad_contrato_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_funcionario; Type: TABLE; Schema: public; Owner: -
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


--
-- Name: tb_cad_funcionario_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_funcionario_habilidade; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_funcionario_habilidade (
    codigo_funcionario integer NOT NULL,
    codigo_habilidade integer NOT NULL,
    nivel integer,
    data_validade date
);


--
-- Name: tb_cad_habilidade; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_habilidade (
    codigo integer NOT NULL,
    descricao character varying(100) NOT NULL,
    observacao character varying(255)
);


--
-- Name: tb_cad_habilidade_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_perfil; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_perfil (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL
);


--
-- Name: tb_cad_perfil_id_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_perfil_permissao; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_perfil_permissao (
    codigo_perfil integer NOT NULL,
    codigo_permissao integer NOT NULL
);


--
-- Name: tb_cad_permissao; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_permissao (
    codigo integer NOT NULL,
    descricao character varying(255) NOT NULL
);


--
-- Name: tb_cad_permissao_id_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_tipo_manutencao; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_tipo_manutencao (
    codigo integer NOT NULL,
    descricao character varying(100) NOT NULL,
    observacao character varying(255)
);


--
-- Name: tb_cad_tipo_manutencao_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_tipo_manutencao_habilidade; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_tipo_manutencao_habilidade (
    codigo_tipo_manutencao integer NOT NULL,
    codigo_habilidade integer NOT NULL,
    obrigatoria boolean DEFAULT true NOT NULL
);


--
-- Name: tb_cad_usuario; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_usuario (
    codigo integer NOT NULL,
    ativo boolean NOT NULL,
    data_criacao timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP,
    email character varying(255) NOT NULL,
    senha character varying(255) NOT NULL
);


--
-- Name: tb_cad_usuario_id_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_cad_usuario_perfil; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_cad_usuario_perfil (
    codigo_usuario integer NOT NULL,
    codigo_perfil integer NOT NULL
);


--
-- Name: tb_srv_contrato_ativo; Type: TABLE; Schema: public; Owner: -
--

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


--
-- Name: tb_srv_contrato_ativo_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.tb_srv_contrato_ativo ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_contrato_ativo_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_srv_maquina_checklist_ferramenta_manutencao; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_srv_maquina_checklist_ferramenta_manutencao (
    codigo integer NOT NULL,
    codigo_historico_manutencao integer NOT NULL,
    codigo_catalogo_ativo integer NOT NULL,
    codigo_ativo integer,
    levou boolean DEFAULT false NOT NULL
);


--
-- Name: tb_srv_maquina_checklist_ferramenta_manutencao_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_srv_maquina_checklist_manutencao; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_srv_maquina_checklist_manutencao (
    codigo integer NOT NULL,
    codigo_historico_manutencao integer NOT NULL,
    codigo_tarefa integer NOT NULL,
    realizado boolean,
    observacao_tarefa text
);


--
-- Name: tb_srv_maquina_checklist_manutencao_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_srv_maquina_contrato; Type: TABLE; Schema: public; Owner: -
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


--
-- Name: tb_srv_maquina_contrato_codigo_seq; Type: SEQUENCE; Schema: public; Owner: -
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
-- Name: tb_srv_maquina_funcionario_manutencao; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tb_srv_maquina_funcionario_manutencao (
    codigo_historico_manutencao integer NOT NULL,
    codigo_funcionario integer NOT NULL
);


--
-- Name: tb_srv_maquina_historico_manutencao; Type: TABLE; Schema: public; Owner: -
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


--
-- Data for Name: spatial_ref_sys; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_ativo; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_ativo_estoque; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_catalogo_ativo; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_catalogo_maquina; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_catalogo_maquina_checklist_padrao; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_catalogo_maquina_ferramenta_modelo; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_catalogo_software; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_catalogo_software_checklist_padrao; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_catalogo_tarefa; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_cliente; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tb_cad_cliente VALUES (1, true, 'Sao Paulo', 'Nacional', '2026-03-30 17:41:53.983291', '11.111.111/0001-11', 'contato@acme.com', 'SP', 'utc-3', 'Acme Mineracao', 'Carlos Silva', 'Brasil', '(11) 90000-0001', 1, NULL, NULL, NULL, false);
INSERT INTO public.tb_cad_cliente VALUES (2, true, 'Rio de Janeiro', 'Regional', '2026-03-30 17:41:53.983291', '22.222.222/0001-22', 'suporte@norteenergia.com', 'RJ', 'utc-3', 'Norte Energia', 'Ana Costa', 'Brasil', '(21) 90000-0002', 1, NULL, NULL, NULL, false);
INSERT INTO public.tb_cad_cliente VALUES (3, true, 'Belo Horizonte', 'Internacional', '2026-03-30 17:41:53.983291', '33.333.333/0001-33', 'ops@globalpumps.com', 'MG', 'utc-3', 'Global Pumps', 'Marina Souza', 'Brasil', '(31) 90000-0003', 1, NULL, NULL, NULL, false);
INSERT INTO public.tb_cad_cliente VALUES (4, true, 'Sao Paulo', 'Nacional', '2026-03-30 18:04:43.028257', '44.444.444/0001-44', 'compat@teste.com', 'SP', 'utc-3', 'Teste Compat API', 'QA Backend', 'Brasil', '(11)90000-0099', 1, NULL, NULL, NULL, false);
INSERT INTO public.tb_cad_cliente VALUES (5, true, 'Campinas', 'Regional', '2026-03-30 18:27:04.72122', '55.555.555/0001-55', 'novascolunas@teste.com', 'SP', 'utc-3', 'Cliente Colunas Novas', 'Integracao Backend', 'Brasil', '(11)98888-0005', 1, 'Cliente criado para validar novas colunas', 'Rua das Acacias', '1200', false);
INSERT INTO public.tb_cad_cliente VALUES (6, true, 'Santos', 'Regional', '2026-03-30 19:27:39.944747', '66.666.666/0001-66', 'cliente.final@tracker.com', 'SP', 'utc-3', 'Cliente Teste Final', 'Teste Sistema', 'Brasil', '(11)97777-0066', 1, 'Criado no teste de login+cadastro', 'Av. do Porto', '660', false);
INSERT INTO public.tb_cad_cliente VALUES (7, true, 'Santiago', 'Internacional', '2026-03-30 20:33:35.196472', '88.888.888/0001-88', 'andes@clientes.com', 'Santiago', 'utc-4', 'Andes Tech', 'Laura Andes', 'Chile', '(11)95555-8888', 1, 'Cliente de teste 2', 'Av. Central', '88', true);
INSERT INTO public.tb_cad_cliente VALUES (8, true, 'Ouro Preto', 'Regional', '2026-03-30 20:33:35.196442', '77.777.777/0001-77', 'atlas@clientes.com', 'MG', 'utc-3', 'Atlas Mining', 'Bruno Atlas', 'Brasil', '(11)95555-7777', 1, 'Cliente de teste 1', 'Rua do Ouro', '77', false);
INSERT INTO public.tb_cad_cliente VALUES (9, true, 'Macae', 'Nacional', '2026-03-30 20:33:35.476206', '99.999.999/0001-99', 'norte@clientes.com', 'RJ', 'utc-3', 'Norte Offshore', 'Rafael Norte', 'Brasil', '(11)95555-9999', 1, 'Cliente de teste 3', 'Rua Maritima', '99', false);


--
-- Data for Name: tb_cad_cliente_contato; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_contrato; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_funcionario; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_funcionario_habilidade; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_habilidade; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_perfil; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tb_cad_perfil VALUES (1, 'ROLE_ADMIN');


--
-- Data for Name: tb_cad_perfil_permissao; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_permissao; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_tipo_manutencao; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_tipo_manutencao_habilidade; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_cad_usuario; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tb_cad_usuario VALUES (1, true, '2026-03-30 17:38:35.974495', 'admin@tracker.com', '$2a$06$2Bkl/YGcdnY5iG8SFxt2sere91RlnH4QQkDR/X7qzqUs.aN/53OrS');


--
-- Data for Name: tb_cad_usuario_perfil; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tb_cad_usuario_perfil VALUES (1, 1);


--
-- Data for Name: tb_srv_contrato_ativo; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_srv_maquina_checklist_ferramenta_manutencao; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_srv_maquina_checklist_manutencao; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_srv_maquina_contrato; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_srv_maquina_funcionario_manutencao; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: tb_srv_maquina_historico_manutencao; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: geocode_settings; Type: TABLE DATA; Schema: tiger; Owner: -
--



--
-- Data for Name: pagc_gaz; Type: TABLE DATA; Schema: tiger; Owner: -
--



--
-- Data for Name: pagc_lex; Type: TABLE DATA; Schema: tiger; Owner: -
--



--
-- Data for Name: pagc_rules; Type: TABLE DATA; Schema: tiger; Owner: -
--



--
-- Data for Name: topology; Type: TABLE DATA; Schema: topology; Owner: -
--



--
-- Data for Name: layer; Type: TABLE DATA; Schema: topology; Owner: -
--



--
-- Name: tb_cad_ativo_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_ativo_codigo_seq', 1, false);


--
-- Name: tb_cad_catalogo_ativo_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_catalogo_ativo_codigo_seq', 1, false);


--
-- Name: tb_cad_catalogo_maquina_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_catalogo_maquina_codigo_seq', 1, false);


--
-- Name: tb_cad_catalogo_software_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_catalogo_software_codigo_seq', 1, false);


--
-- Name: tb_cad_catalogo_tarefa_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_catalogo_tarefa_codigo_seq', 1, false);


--
-- Name: tb_cad_cliente_contato_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_cliente_contato_codigo_seq', 1, false);


--
-- Name: tb_cad_cliente_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_cliente_id_seq', 11, true);


--
-- Name: tb_cad_contrato_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_contrato_codigo_seq', 1, false);


--
-- Name: tb_cad_funcionario_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_funcionario_codigo_seq', 1, false);


--
-- Name: tb_cad_habilidade_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_habilidade_codigo_seq', 1, false);


--
-- Name: tb_cad_perfil_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_perfil_id_seq', 1, true);


--
-- Name: tb_cad_permissao_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_permissao_id_seq', 1, false);


--
-- Name: tb_cad_tipo_manutencao_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_tipo_manutencao_codigo_seq', 1, false);


--
-- Name: tb_cad_usuario_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_cad_usuario_id_seq', 1, true);


--
-- Name: tb_srv_contrato_ativo_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_srv_contrato_ativo_codigo_seq', 1, false);


--
-- Name: tb_srv_maquina_checklist_ferramenta_manutencao_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_srv_maquina_checklist_ferramenta_manutencao_codigo_seq', 1, false);


--
-- Name: tb_srv_maquina_checklist_manutencao_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_srv_maquina_checklist_manutencao_codigo_seq', 1, false);


--
-- Name: tb_srv_maquina_contrato_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tb_srv_maquina_contrato_codigo_seq', 1, false);


--
-- Name: topology_id_seq; Type: SEQUENCE SET; Schema: topology; Owner: -
--

SELECT pg_catalog.setval('topology.topology_id_seq', 1, false);


--
-- Name: tb_srv_contrato_ativo tb_srv_contrato_ativo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tb_srv_contrato_ativo
    ADD CONSTRAINT tb_srv_contrato_ativo_pkey PRIMARY KEY (codigo);


--
-- PostgreSQL database dump complete
--

