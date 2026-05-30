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
    codigo_maquina_contrato integer,
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
    tipo character varying(100) NOT NULL,
    CONSTRAINT ck_catalogo_ativo_tipo CHECK (((tipo)::text = ANY ((ARRAY['FERRAMENTA'::character varying, 'EQUIPAMENTO'::character varying, 'COMPONENTE'::character varying, 'PERIFERICO'::character varying, 'EPI'::character varying, 'CONSUMIVEL'::character varying])::text[])))
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
    descricao character varying(255) NOT NULL,
    versao character varying(100),
    tipo character varying(100),
    descricao_tecnica character varying(255),
    desenvolvedor_fornecedor character varying(255),
    descricao_fornecedor character varying(255),
    url_documentacao character varying(500),
    especificacao text,
    ativo boolean DEFAULT true NOT NULL,
    data_cadastro timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
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
    observacao text,
    rua character varying(255),
    numero character varying(50),
    internacional boolean DEFAULT false
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
-- Name: tb_cad_cliente_contato; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_cliente_contato (
    codigo integer NOT NULL,
    codigo_cliente integer NOT NULL,
    nome character varying(255) NOT NULL,
    email character varying(255),
    telefone character varying(30)
);

ALTER TABLE public.tb_cad_cliente_contato OWNER TO user_dev;

--
-- Name: tb_cad_cliente_contato_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
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
-- Name: tb_cad_contrato; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_contrato (
    codigo integer NOT NULL,
    codigo_cliente integer NOT NULL,
    data_inicio date,
    data_fim date,
    descricao character varying(50),
    status character varying(100),
    periodo_manutencao_preventiva integer,
    conexao_internet boolean DEFAULT false,
    vencimento_manutencao_preventiva date
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
    certificacao character varying(255),
    estado character varying(100),
    disponibilidade character varying(100),
    latitude numeric(10,6),
    longitude numeric(10,6),
    ultima_atualizacao_localizacao timestamp without time zone
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
-- Name: tb_srv_tecnico_ferramenta_ativo; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_srv_tecnico_ferramenta_ativo (
    codigo integer NOT NULL,
    codigo_historico_manutencao integer NOT NULL,
    codigo_funcionario integer NOT NULL,
    codigo_ativo integer NOT NULL,
    descricao_ativo character varying(255),
    levado boolean DEFAULT false NOT NULL,
    devolvido boolean DEFAULT false NOT NULL,
    observacao text
);


ALTER TABLE public.tb_srv_tecnico_ferramenta_ativo OWNER TO user_dev;

--
-- Name: tb_srv_tecnico_ferramenta_ativo_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_tecnico_ferramenta_ativo ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_tecnico_ferramenta_ativo_codigo_seq
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
    codigo_tipo_manutencao integer,
    codigo_ordem_servico integer,
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


CREATE TABLE public.tb_srv_ordem_servico (
    codigo integer NOT NULL,
    codigo_cliente integer NOT NULL,
    codigo_funcionario integer,
    codigo_software_instalado integer,
    codigo_contrato integer NOT NULL,
    codigo_maquina_contrato integer NOT NULL,
    status character varying(100),
    criticidade character varying(50),
    tipo_ordem character varying(50),
    data_abertura timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_agendamento timestamp without time zone,
    data_inicio_execucao timestamp without time zone,
    data_fim_execucao timestamp without time zone,
    observacao_geral text
);


ALTER TABLE public.tb_srv_ordem_servico OWNER TO user_dev;

--
-- Name: tb_srv_ordem_servico_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_ordem_servico ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_ordem_servico_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_srv_ordem_servico_checklist_ativo; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_srv_ordem_servico_checklist_ativo (
    codigo integer NOT NULL,
    codigo_ordem_servico integer NOT NULL,
    codigo_funcionario integer,
    codigo_ativo integer NOT NULL,
    descricao_ativo character varying(255),
    levado boolean DEFAULT false NOT NULL,
    devolvido boolean DEFAULT false NOT NULL,
    observacao text,
    status_intervencao character varying(50),
    data_intervencao timestamp without time zone,
    observacao_intervencao text
);


ALTER TABLE public.tb_srv_ordem_servico_checklist_ativo OWNER TO user_dev;

--
-- Name: tb_srv_ordem_servico_checklist_ativo_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_ordem_servico_checklist_ativo ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_ordem_servico_checklist_ativo_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_srv_ciclo_embarcacao; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_srv_ciclo_embarcacao (
    codigo integer NOT NULL,
    embarcacao character varying(255) NOT NULL,
    data_chegada timestamp without time zone NOT NULL,
    data_saida timestamp without time zone,
    local character varying(255) NOT NULL
);


ALTER TABLE public.tb_srv_ciclo_embarcacao OWNER TO user_dev;

--
-- Name: tb_srv_ciclo_embarcacao_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_ciclo_embarcacao ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_ciclo_embarcacao_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_cad_tipo_viagem; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_cad_tipo_viagem (
    codigo integer NOT NULL,
    descricao character varying(100) NOT NULL,
    observacao text,
    ativo boolean DEFAULT true NOT NULL,
    data_cadastro timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.tb_cad_tipo_viagem OWNER TO user_dev;

--
-- Name: tb_cad_tipo_viagem_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_cad_tipo_viagem ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_cad_tipo_viagem_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_srv_viagem; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_srv_viagem (
    codigo integer NOT NULL,
    codigo_tipo_viagem integer NOT NULL,
    codigo_cliente integer NOT NULL,
    codigo_funcionario_responsavel integer,
    codigo_ordem_servico integer,
    status character varying(50) DEFAULT 'ABERTA'::character varying NOT NULL,
    data_cadastro timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_saida_prevista timestamp(6) without time zone,
    data_saida_real timestamp(6) without time zone,
    data_retorno_previsto timestamp(6) without time zone,
    data_retorno_real timestamp(6) without time zone,
    origem character varying(255),
    destino character varying(255),
    km_previsto numeric(10,2),
    km_real numeric(10,2),
    observacao text
);


ALTER TABLE public.tb_srv_viagem OWNER TO user_dev;

--
-- Name: tb_srv_viagem_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_viagem ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_viagem_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_srv_viagem_parada; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_srv_viagem_parada (
    codigo integer NOT NULL,
    codigo_viagem integer NOT NULL,
    ordem integer NOT NULL,
    descricao_local character varying(255) NOT NULL,
    endereco character varying(255),
    cidade character varying(100),
    estado_regiao character varying(100),
    latitude numeric(10,6),
    longitude numeric(10,6),
    data_chegada_prevista timestamp(6) without time zone,
    data_chegada_real timestamp(6) without time zone,
    data_saida_prevista timestamp(6) without time zone,
    data_saida_real timestamp(6) without time zone,
    observacao text
);


ALTER TABLE public.tb_srv_viagem_parada OWNER TO user_dev;

--
-- Name: tb_srv_viagem_parada_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_viagem_parada ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_viagem_parada_codigo_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tb_srv_viagem_checklist_dinamico; Type: TABLE; Schema: public; Owner: user_dev
--

CREATE TABLE public.tb_srv_viagem_checklist_dinamico (
    codigo integer NOT NULL,
    codigo_viagem integer NOT NULL,
    descricao_item character varying(255) NOT NULL,
    conferido boolean DEFAULT false NOT NULL,
    data_conferencia timestamp(6) without time zone DEFAULT CURRENT_TIMESTAMP,
    observacao text
);


ALTER TABLE public.tb_srv_viagem_checklist_dinamico OWNER TO user_dev;

--
-- Name: tb_srv_viagem_checklist_dinamico_codigo_seq; Type: SEQUENCE; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_viagem_checklist_dinamico ALTER COLUMN codigo ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tb_srv_viagem_checklist_dinamico_codigo_seq
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
1	1	MT-DIG-0001	L2026-04	Multimetro da bancada tecnica 01	\N	DISPONIVEL
2	1	MT-DIG-0002	L2026-04	Multimetro da bancada tecnica 02	\N	DISPONIVEL
3	2	AL-AMP-0001	L2026-04	Alicate amperimetro da equipe tecnica	\N	DISPONIVEL
4	3	CH-ISO-0001	L2026-04	Kit chave isolada - maleta 01	\N	DISPONIVEL
5	4	ALN-SET-0001	L2026-04	Kit allen metrico - maleta 01	\N	DISPONIVEL
6	5	NT-TEC-0001	L2026-04	Notebook tecnico para diagnosticos em campo	\N	DISPONIVEL
7	5	NT-TEC-0002	L2026-04	Notebook tecnico reserva	\N	EM_MANUTENCAO
\.


--
-- Data for Name: tb_cad_ativo_estoque; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_ativo_estoque (codigo_catalogo_ativo, quantidade) FROM stdin;
1	2
2	1
3	6
4	3
5	2
6	8
7	10
8	4
9	12
10	12
\.
COPY public.tb_srv_ordem_servico (codigo, codigo_cliente, codigo_funcionario, codigo_software_instalado, codigo_contrato, codigo_maquina_contrato, status, criticidade, data_abertura, data_agendamento, data_inicio_execucao, data_fim_execucao, observacao_geral) FROM stdin;
\.

--
-- Data for Name: tb_srv_ordem_servico_checklist_ativo; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_ordem_servico_checklist_ativo (codigo, codigo_ordem_servico, codigo_funcionario, codigo_ativo, descricao_ativo, levado, devolvido, observacao) FROM stdin;
\.

--
-- Data for Name: tb_cad_catalogo_ativo; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_ativo (codigo, descricao_produto, modelo, marca, descricao, especificacao, tipo) FROM stdin;
1	Multimetro Digital	DT-9205A	Minipa	Equipamento para medicao eletrica	Tensao, corrente, resistencia e continuidade	FERRAMENTA
2	Alicate Amperimetro	ET-3200	Minipa	Alicate para medicao de corrente	Corrente AC/DC e tensao	FERRAMENTA
3	Chave de Fenda Isolada	SL-6	Tramontina	Ferramenta manual isolada para manutencao eletrica	Kit de chaves isoladas	FERRAMENTA
4	Kit Chave Allen	ALLEN-MM	Gedore	Kit de chaves allen metricas	Jogo de 1,5 mm a 10 mm	FERRAMENTA
5	Notebook Tecnico	Latitude 5420	Dell	Notebook para uso em manutencoes e diagnosticos	Intel i5, 16GB RAM, SSD 512GB	EQUIPAMENTO
6	SSD 512GB SATA	SA400S37/480G	Kingston	Unidade para substituicao em manutencao	Armazenamento SSD SATA	COMPONENTE
7	Memoria RAM DDR4 8GB	KVR26N19S8/8	Kingston	Modulo de memoria para reposicao	DDR4 8GB 2666MHz	COMPONENTE
8	Fonte ATX 500W	VX-500	Vinik	Fonte de alimentacao para reposicao	500W, bivolt	COMPONENTE
9	Mouse USB	M90	Logitech	Periferico para reposicao	Mouse optico USB	PERIFERICO
10	Teclado USB	K120	Logitech	Periferico para reposicao	Teclado ABNT2 USB	PERIFERICO
\.


--
-- Data for Name: tb_cad_catalogo_maquina; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_maquina (codigo, descricao, especificacao, limite_manutencao) FROM stdin;
1	Router CNC X200	Router CNC para corte e usinagem leve	180 dias
2	Injetora PL-450	Maquina injetora de medio porte	120 dias
3	Compressor Industrial 80L	Compressor de ar industrial	90 dias
\.


--
-- Data for Name: tb_cad_catalogo_maquina_checklist_padrao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_maquina_checklist_padrao (codigo_catalogo_maquina, codigo_tarefa) FROM stdin;
1	1
1	2
1	4
1	5
1	10
2	1
2	2
2	3
2	4
2	10
3	2
3	3
3	5
3	10
\.


--
-- Data for Name: tb_cad_catalogo_maquina_ferramenta_modelo; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_maquina_ferramenta_modelo (codigo_catalogo_maquina, codigo_catalogo_ativo, quantidade_necessaria) FROM stdin;
1	1	1
1	3	1
1	4	1
1	5	1
2	1	1
2	2	1
2	3	1
2	5	1
3	1	1
3	2	1
\.


--
-- Data for Name: tb_cad_catalogo_software; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_software (codigo, descricao) FROM stdin;
1	Windows 11 Pro
2	TeamViewer Host
3	ERP Tracker Agent
4	PostgreSQL
5	Antivirus Endpoint
\.


--
-- Data for Name: tb_cad_catalogo_software_checklist_padrao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_software_checklist_padrao (codigo_catalogo_software, codigo_catalogo_tarefa, obrigatorio) FROM stdin;
1	6	t
1	7	t
1	8	t
1	9	t
1	10	t
2	7	t
2	9	t
2	10	t
3	6	t
3	9	t
3	10	t
4	6	t
4	9	t
4	10	t
5	7	t
5	8	t
5	10	t
\.


--
-- Data for Name: tb_cad_catalogo_tarefa; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_catalogo_tarefa (codigo, descricao, categoria) FROM stdin;
1	Inspecionar cabos e conectores	MAQUINA
2	Limpar filtros e ventilacao	MAQUINA
3	Verificar lubrificacao	MAQUINA
4	Testar sensores e botoes de emergencia	MAQUINA
5	Conferir aterramento eletrico	MAQUINA
6	Validar backup da configuracao	SOFTWARE
7	Conferir licenciamento	SOFTWARE
8	Atualizar antivirus e assinaturas	SOFTWARE
9	Testar comunicacao em rede	SOFTWARE
10	Registrar evidencias da manutencao	GERAL
\.


--
-- Data for Name: tb_cad_cliente; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_cliente (codigo, ativo, cidade, classificacao_distancia, data_cadastro, documento, email_contato, estado_regiao, fuso_horario, descricao_empresa, descricao_responsavel, pais, telefone_contato) FROM stdin;
1	t	Campinas	Regional	2026-03-27 21:20:28.403828	11.111.111/0001-11	contato@alpha.com	Sao Paulo	America/Sao_Paulo	Alpha Industrial	Ana Souza	Brasil	11999990001
2	t	Niteroi	Nacional	2026-03-27 21:20:28.64737	22.222.222/0001-22	contato@beta.com	Rio de Janeiro	America/Sao_Paulo	Beta Logistics	Bruno Lima	Brasil	21999990002
3	f	Belo Horizonte	Internacional	2026-03-27 21:20:28.648821	33.333.333/0001-33	contato@gamma.com	Minas Gerais	America/Sao_Paulo	Gamma Services	Carla Mendes	Brasil	31999990003
4	t	Sao Paulo	Nacional	2026-03-27 21:29:26.053489	44.444.444/0001-44	contato@deltatecnologia.com	Sao Paulo	America/Sao_Paulo	Delta Tecnologia	Diego Martins	Brasil	11988887777
\.


--
-- Data for Name: tb_cad_cliente_contato; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_cliente_contato (codigo, codigo_cliente, nome, email, telefone) FROM stdin;
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
2	ROLE_TECNICO
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
-- Data for Name: tb_cad_tipo_viagem; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_cad_tipo_viagem (codigo, descricao, observacao, ativo, data_cadastro) FROM stdin;
1	ATENDIMENTO_TECNICO	Viagem para atendimento técnico em cliente.	t	2026-04-25 00:00:00
2	INSTALACAO	Viagem para instalação de máquina, software ou equipamento.	t	2026-04-25 00:00:00
3	MANUTENCAO	Viagem para manutenção preventiva ou corretiva.	t	2026-04-25 00:00:00
4	ENTREGA	Viagem para entrega de equipamento, peça ou material.	t	2026-04-25 00:00:00
5	RETIRADA	Viagem para retirada de equipamento, peça ou material.	t	2026-04-25 00:00:00
6	VISITA_COMERCIAL	Viagem para visita comercial ou relacionamento com cliente.	t	2026-04-25 00:00:00
\.


--
-- Data for Name: tb_srv_viagem; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_viagem (codigo, codigo_tipo_viagem, codigo_cliente, codigo_funcionario_responsavel, codigo_ordem_servico, status, data_cadastro, data_saida_prevista, data_saida_real, data_retorno_previsto, data_retorno_real, origem, destino, km_previsto, km_real, observacao) FROM stdin;
\.


--
-- Data for Name: tb_srv_viagem_parada; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_viagem_parada (codigo, codigo_viagem, ordem, descricao_local, endereco, cidade, estado_regiao, latitude, longitude, data_chegada_prevista, data_chegada_real, data_saida_prevista, data_saida_real, observacao) FROM stdin;
\.


--
-- Data for Name: tb_srv_viagem_checklist_dinamico; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_viagem_checklist_dinamico (codigo, codigo_viagem, descricao_item, conferido, data_conferencia, observacao) FROM stdin;
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
-- Data for Name: tb_srv_tecnico_ferramenta_ativo; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_tecnico_ferramenta_ativo (codigo, codigo_historico_manutencao, codigo_funcionario, codigo_ativo, descricao_ativo, levado, devolvido, observacao) FROM stdin;
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
-- Data for Name: tb_srv_ciclo_embarcacao; Type: TABLE DATA; Schema: public; Owner: user_dev
--

COPY public.tb_srv_ciclo_embarcacao (codigo, embarcacao, data_chegada, data_saida, local) FROM stdin;
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

SELECT pg_catalog.setval('public.tb_cad_ativo_codigo_seq', 7, true);


-- Name: tb_srv_ordem_servico_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_ordem_servico_codigo_seq', 1, false);


--
-- Name: tb_srv_ordem_servico_checklist_ativo_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_ordem_servico_checklist_ativo_codigo_seq', 1, false);


--
-- Name: tb_srv_ciclo_embarcacao_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_ciclo_embarcacao_codigo_seq', 1, false);


--
-- Name: tb_cad_catalogo_ativo_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_catalogo_ativo_codigo_seq', 10, true);


--
-- Name: tb_cad_catalogo_maquina_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_catalogo_maquina_codigo_seq', 3, true);


--
-- Name: tb_cad_catalogo_software_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_catalogo_software_codigo_seq', 5, true);


--
-- Name: tb_cad_catalogo_tarefa_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_catalogo_tarefa_codigo_seq', 10, true);


--
-- Name: tb_cad_cliente_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_cliente_id_seq', 4, true);


--
-- Name: tb_cad_cliente_contato_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_cliente_contato_codigo_seq', 1, false);


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

SELECT pg_catalog.setval('public.tb_cad_perfil_id_seq', 2, true);


--
-- Name: tb_cad_permissao_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_permissao_id_seq', 1, false);



--
-- Name: tb_cad_tipo_viagem_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_tipo_viagem_codigo_seq', 6, true);


--
-- Name: tb_srv_viagem_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_viagem_codigo_seq', 1, false);


--
-- Name: tb_srv_viagem_parada_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_viagem_parada_codigo_seq', 1, false);


--
-- Name: tb_srv_viagem_checklist_dinamico_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_viagem_checklist_dinamico_codigo_seq', 1, false);


--
-- Name: tb_cad_tipo_manutencao_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_tipo_manutencao_codigo_seq', 1, false);


--
-- Name: tb_cad_usuario_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_cad_usuario_id_seq', 1, true);


--
-- Name: tb_srv_tecnico_ferramenta_ativo_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: user_dev
--

SELECT pg_catalog.setval('public.tb_srv_tecnico_ferramenta_ativo_codigo_seq', 1, false);


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


-- Name: tb_srv_ordem_servico tb_srv_ordem_servico_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_ordem_servico
    ADD CONSTRAINT tb_srv_ordem_servico_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_ordem_servico_checklist_ativo tb_srv_ordem_servico_checklist_ativo_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_ordem_servico_checklist_ativo
    ADD CONSTRAINT tb_srv_ordem_servico_checklist_ativo_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_ordem_servico_checklist_ativo uk_ordem_servico_checklist_ativo; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_ordem_servico_checklist_ativo
    ADD CONSTRAINT uk_ordem_servico_checklist_ativo UNIQUE (codigo_ordem_servico, codigo_ativo);


--
-- Name: tb_srv_ciclo_embarcacao tb_srv_ciclo_embarcacao_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_ciclo_embarcacao
    ADD CONSTRAINT tb_srv_ciclo_embarcacao_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_ciclo_embarcacao tb_srv_ciclo_embarcacao_periodo_check; Type: CHECK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_ciclo_embarcacao
    ADD CONSTRAINT tb_srv_ciclo_embarcacao_periodo_check CHECK (((data_saida IS NULL) OR (data_saida >= data_chegada)));


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
-- Name: tb_cad_cliente_contato tb_cad_cliente_contato_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_cliente_contato
    ADD CONSTRAINT tb_cad_cliente_contato_pkey PRIMARY KEY (codigo);


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
-- Name: tb_cad_tipo_viagem tb_cad_tipo_viagem_descricao_key; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_tipo_viagem
    ADD CONSTRAINT tb_cad_tipo_viagem_descricao_key UNIQUE (descricao);


--
-- Name: tb_cad_tipo_viagem tb_cad_tipo_viagem_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_tipo_viagem
    ADD CONSTRAINT tb_cad_tipo_viagem_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_viagem tb_srv_viagem_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_viagem
    ADD CONSTRAINT tb_srv_viagem_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_viagem tb_srv_viagem_status_check; Type: CHECK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_viagem
    ADD CONSTRAINT tb_srv_viagem_status_check CHECK (((status)::text = ANY ((ARRAY['ABERTA'::character varying, 'EM_ANDAMENTO'::character varying, 'FINALIZADA'::character varying, 'CANCELADA'::character varying])::text[])));


--
-- Name: tb_srv_viagem tb_srv_viagem_km_previsto_check; Type: CHECK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_viagem
    ADD CONSTRAINT tb_srv_viagem_km_previsto_check CHECK (((km_previsto IS NULL) OR (km_previsto >= (0)::numeric)));


--
-- Name: tb_srv_viagem tb_srv_viagem_km_real_check; Type: CHECK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_viagem
    ADD CONSTRAINT tb_srv_viagem_km_real_check CHECK (((km_real IS NULL) OR (km_real >= (0)::numeric)));


--
-- Name: tb_srv_viagem_parada tb_srv_viagem_parada_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_viagem_parada
    ADD CONSTRAINT tb_srv_viagem_parada_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_viagem_parada tb_srv_viagem_parada_ordem_key; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_viagem_parada
    ADD CONSTRAINT tb_srv_viagem_parada_ordem_key UNIQUE (codigo_viagem, ordem);


--
-- Name: tb_srv_viagem_parada tb_srv_viagem_parada_ordem_check; Type: CHECK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE public.tb_srv_viagem_parada
    ADD CONSTRAINT tb_srv_viagem_parada_ordem_check CHECK ((ordem > 0));


--
-- Name: tb_srv_viagem_checklist_dinamico tb_srv_viagem_checklist_dinamico_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_viagem_checklist_dinamico
    ADD CONSTRAINT tb_srv_viagem_checklist_dinamico_pkey PRIMARY KEY (codigo);


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
-- Name: tb_srv_tecnico_ferramenta_ativo tb_srv_tecnico_ferramenta_ativo_pkey; Type: CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_tecnico_ferramenta_ativo
    ADD CONSTRAINT tb_srv_tecnico_ferramenta_ativo_pkey PRIMARY KEY (codigo);


--
-- Name: tb_srv_ordem_servico fk_ordem_servico_cliente; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_ordem_servico
    ADD CONSTRAINT fk_ordem_servico_cliente FOREIGN KEY (codigo_cliente) REFERENCES public.tb_cad_cliente(codigo) ON DELETE CASCADE;

--
-- Name: tb_srv_ordem_servico fk_ordem_servico_funcionario; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_ordem_servico
    ADD CONSTRAINT fk_ordem_servico_funcionario FOREIGN KEY (codigo_funcionario) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;

--
-- Name: tb_srv_ordem_servico fk_ordem_servico_software_instalado; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_ordem_servico
    ADD CONSTRAINT fk_ordem_servico_software_instalado FOREIGN KEY (codigo_software_instalado) REFERENCES public.tb_srv_maquina_software_instalado(codigo) ON DELETE CASCADE;

--
-- Name: tb_srv_ordem_servico fk_ordem_servico_contrato; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_ordem_servico
    ADD CONSTRAINT fk_ordem_servico_contrato FOREIGN KEY (codigo_contrato) REFERENCES public.tb_cad_contrato(codigo) ON DELETE CASCADE;

--
-- Name: tb_srv_ordem_servico fk_ordem_servico_maquina_contrato; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_ordem_servico
    ADD CONSTRAINT fk_ordem_servico_maquina_contrato FOREIGN KEY (codigo_maquina_contrato) REFERENCES public.tb_srv_maquina_contrato(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_ordem_servico_checklist_ativo fk_os_checklist_ativo_ordem; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_ordem_servico_checklist_ativo
    ADD CONSTRAINT fk_os_checklist_ativo_ordem FOREIGN KEY (codigo_ordem_servico) REFERENCES public.tb_srv_ordem_servico(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_ordem_servico_checklist_ativo fk_os_checklist_ativo_funcionario; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_ordem_servico_checklist_ativo
    ADD CONSTRAINT fk_os_checklist_ativo_funcionario FOREIGN KEY (codigo_funcionario) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_ordem_servico_checklist_ativo fk_os_checklist_ativo_ativo; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_ordem_servico_checklist_ativo
    ADD CONSTRAINT fk_os_checklist_ativo_ativo FOREIGN KEY (codigo_ativo) REFERENCES public.tb_cad_ativo(codigo) ON DELETE CASCADE;


--
-- Name: tb_srv_viagem fk_viagem_tipo_viagem; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_viagem
    ADD CONSTRAINT fk_viagem_tipo_viagem FOREIGN KEY (codigo_tipo_viagem) REFERENCES public.tb_cad_tipo_viagem(codigo);


--
-- Name: tb_srv_viagem fk_viagem_cliente; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_viagem
    ADD CONSTRAINT fk_viagem_cliente FOREIGN KEY (codigo_cliente) REFERENCES public.tb_cad_cliente(codigo);


--
-- Name: tb_srv_viagem fk_viagem_funcionario_responsavel; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_viagem
    ADD CONSTRAINT fk_viagem_funcionario_responsavel FOREIGN KEY (codigo_funcionario_responsavel) REFERENCES public.tb_cad_funcionario(codigo);


--
-- Name: tb_srv_viagem fk_viagem_ordem_servico; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_viagem
    ADD CONSTRAINT fk_viagem_ordem_servico FOREIGN KEY (codigo_ordem_servico) REFERENCES public.tb_srv_ordem_servico(codigo);


--
-- Name: tb_srv_viagem_parada fk_viagem_parada_viagem; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_viagem_parada
    ADD CONSTRAINT fk_viagem_parada_viagem FOREIGN KEY (codigo_viagem) REFERENCES public.tb_srv_viagem(codigo) ON DELETE CASCADE;



--
-- Name: tb_srv_viagem_checklist_dinamico fk_checklist_viagem; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_viagem_checklist_dinamico
    ADD CONSTRAINT fk_checklist_viagem FOREIGN KEY (codigo_viagem) REFERENCES public.tb_srv_viagem(codigo) ON DELETE CASCADE;


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
-- Name: tb_cad_ativo fk_ativo_maquina_contrato; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_ativo
    ADD CONSTRAINT fk_ativo_maquina_contrato FOREIGN KEY (codigo_maquina_contrato) REFERENCES public.tb_srv_maquina_contrato(codigo) ON DELETE SET NULL;








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
-- Name: tb_srv_maquina_historico_manutencao fk_historico_manutencao_ordem_servico; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_maquina_historico_manutencao
    ADD CONSTRAINT fk_historico_manutencao_ordem_servico FOREIGN KEY (codigo_ordem_servico) REFERENCES public.tb_srv_ordem_servico(codigo) ON DELETE CASCADE;


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
-- Name: tb_cad_cliente_contato fk_cliente_contato_cliente; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_cad_cliente_contato
    ADD CONSTRAINT fk_cliente_contato_cliente FOREIGN KEY (codigo_cliente) REFERENCES public.tb_cad_cliente(codigo) ON DELETE CASCADE;




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
-- Name: tb_srv_tecnico_ferramenta_ativo fk_tecnico_ferramenta_ativo_historico; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_tecnico_ferramenta_ativo
    ADD CONSTRAINT fk_tecnico_ferramenta_ativo_historico FOREIGN KEY (codigo_historico_manutencao) REFERENCES public.tb_srv_maquina_historico_manutencao(codigo) ON DELETE CASCADE;

--
-- Name: tb_srv_tecnico_ferramenta_ativo fk_tecnico_ferramenta_ativo_funcionario; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_tecnico_ferramenta_ativo
    ADD CONSTRAINT fk_tecnico_ferramenta_ativo_funcionario FOREIGN KEY (codigo_funcionario) REFERENCES public.tb_cad_funcionario(codigo) ON DELETE CASCADE;

--
-- Name: tb_srv_tecnico_ferramenta_ativo fk_tecnico_ferramenta_ativo_ativo; Type: FK CONSTRAINT; Schema: public; Owner: user_dev
--

ALTER TABLE ONLY public.tb_srv_tecnico_ferramenta_ativo
    ADD CONSTRAINT fk_tecnico_ferramenta_ativo_ativo FOREIGN KEY (codigo_ativo) REFERENCES public.tb_cad_ativo(codigo) ON DELETE CASCADE;

--
-- PostgreSQL database dump complete
--


-- SEED ADICIONADO SOBRE O ARQUIVO ORIGINAL ENVIADO PELO USUARIO
-- Nada acima deste ponto foi removido ou alterado.

-- CARGA COMPLEMENTAR - POPULAR TODAS AS TABELAS DO SISTEMA
-- Observacao: nao remove nem altera os dados ja existentes.
-- Execute depois do dump original ou mantenha anexado ao final dele.

BEGIN;

-- 1) Permissoes do sistema
INSERT INTO public.tb_cad_permissao (codigo, descricao) OVERRIDING SYSTEM VALUE VALUES
    (1, 'CLIENTE_VISUALIZAR'),
    (2, 'CLIENTE_CADASTRAR'),
    (3, 'CONTRATO_VISUALIZAR'),
    (4, 'CONTRATO_CADASTRAR'),
    (5, 'MAQUINA_VISUALIZAR'),
    (6, 'MAQUINA_MANUTENCAO'),
    (7, 'ORDEM_SERVICO_VISUALIZAR'),
    (8, 'ORDEM_SERVICO_EXECUTAR'),
    (9, 'VIAGEM_VISUALIZAR'),
    (10, 'VIAGEM_CADASTRAR'),
    (11, 'ATIVO_VISUALIZAR'),
    (12, 'ATIVO_MOVIMENTAR'),
    (13, 'USUARIO_GERENCIAR')
ON CONFLICT (codigo) DO NOTHING;

-- 2) Permissoes vinculadas ao perfil administrador ja existente
INSERT INTO public.tb_cad_perfil_permissao (codigo_perfil, codigo_permissao) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
    (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
    (1, 11), (1, 12), (1, 13)
ON CONFLICT (codigo_perfil, codigo_permissao) DO NOTHING;

-- 3) Usuarios tecnicos
INSERT INTO public.tb_cad_usuario (codigo, ativo, data_criacao, email, senha) OVERRIDING SYSTEM VALUE VALUES
    (2, true, '2026-04-25 08:00:00', 'tecnico01@tracker.com', '$2y$10$seed.seed.seed.seed.seed.seed.seed.seed.seed.seedse'),
    (3, true, '2026-04-25 08:05:00', 'tecnico02@tracker.com', '$2y$10$seed.seed.seed.seed.seed.seed.seed.seed.seed.seedse'),
    (4, true, '2026-04-25 08:10:00', 'gestor@tracker.com',    '$2y$10$seed.seed.seed.seed.seed.seed.seed.seed.seed.seedse')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO public.tb_cad_usuario_perfil (codigo_usuario, codigo_perfil) VALUES
    (2, 2),
    (3, 2),
    (4, 1)
ON CONFLICT (codigo_usuario, codigo_perfil) DO NOTHING;

-- 4) Habilidades tecnicas
INSERT INTO public.tb_cad_habilidade (codigo, descricao, observacao) OVERRIDING SYSTEM VALUE VALUES
    (1, 'Eletrica Industrial', 'Manutencao e diagnostico eletrico em maquinas industriais.'),
    (2, 'Mecanica Industrial', 'Ajustes mecanicos, lubrificacao e troca de componentes.'),
    (3, 'Redes e Conectividade', 'Configuracao de rede, acesso remoto e comunicacao com equipamentos.'),
    (4, 'Software e Sistemas', 'Instalacao, atualizacao e verificacao de softwares.'),
    (5, 'Seguranca do Trabalho', 'Procedimentos de seguranca, bloqueio e trabalho em altura.'),
    (6, 'Diagnostico de Hardware', 'Analise de falhas em computadores, fontes e perifericos.')
ON CONFLICT (codigo) DO NOTHING;

-- 5) Funcionarios
INSERT INTO public.tb_cad_funcionario (codigo, codigo_usuario, nome, cpf, cargo, telefone, certificacao, estado, disponibilidade, latitude, longitude) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 'Administrador Tracker', '000.000.000-00', 'Administrador', '11900000000', 'Gestao de Sistema', 'Sao Paulo', 'DISPONIVEL', -23.550520, -46.633308),
    (2, 2, 'Marcos Pereira', '111.111.111-11', 'Tecnico de Campo', '11911111111', 'NR10 / NR35', 'Sao Paulo', 'DISPONIVEL', -23.561414, -46.655881),
    (3, 3, 'Juliana Alves', '222.222.222-22', 'Tecnica de Software', '21922222222', 'Redes / Sistemas', 'Rio de Janeiro', 'EM_ROTA', -22.883200, -43.103400),
    (4, 4, 'Ricardo Nunes', '333.333.333-33', 'Supervisor Tecnico', '31933333333', 'Gestao de Manutencao', 'Minas Gerais', 'DISPONIVEL', -19.916681, -43.934493)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO public.tb_cad_funcionario_habilidade (codigo_funcionario, codigo_habilidade, nivel, data_validade) VALUES
    (1, 4, 5, '2027-12-31'),
    (2, 1, 5, '2027-12-31'),
    (2, 2, 4, '2027-12-31'),
    (2, 5, 5, '2027-12-31'),
    (3, 3, 5, '2027-12-31'),
    (3, 4, 5, '2027-12-31'),
    (3, 6, 4, '2027-12-31'),
    (4, 1, 4, '2027-12-31'),
    (4, 2, 4, '2027-12-31'),
    (4, 5, 5, '2027-12-31')
ON CONFLICT (codigo_funcionario, codigo_habilidade) DO NOTHING;

-- 6) Ativos fisicos adicionais para movimentacao e manutencao
INSERT INTO public.tb_cad_ativo (codigo, codigo_catalogo_ativo, numero_serie, lote, descricao, codigo_funcionario_responsavel, status) OVERRIDING SYSTEM VALUE VALUES
    (8, 6, 'SSD-512-0001', 'L2026-04', 'SSD 512GB para reposicao em chamado tecnico', 2, 'DISPONIVEL'),
    (9, 7, 'RAM-DDR4-0001', 'L2026-04', 'Memoria DDR4 8GB para reposicao em manutencao', 2, 'DISPONIVEL'),
    (10, 8, 'FNT-500W-0001', 'L2026-04', 'Fonte ATX 500W para troca corretiva', 4, 'DISPONIVEL'),
    (11, 9, 'MOU-USB-0001', 'L2026-04', 'Mouse USB reserva para atendimento externo', 3, 'DISPONIVEL'),
    (12, 10, 'TEC-USB-0001', 'L2026-04', 'Teclado USB reserva para atendimento externo', 3, 'DISPONIVEL')
ON CONFLICT (codigo) DO NOTHING;

-- 7) Contatos dos clientes
INSERT INTO public.tb_cad_cliente_contato (codigo, codigo_cliente, nome, email, telefone) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 'Ana Souza', 'ana.souza@alpha.com', '11999990001'),
    (2, 1, 'Paulo Henrique', 'paulo.henrique@alpha.com', '11999990011'),
    (3, 2, 'Bruno Lima', 'bruno.lima@beta.com', '21999990002'),
    (4, 3, 'Carla Mendes', 'carla.mendes@gamma.com', '31999990003'),
    (5, 4, 'Diego Martins', 'diego.martins@deltatecnologia.com', '11988887777')
ON CONFLICT (codigo) DO NOTHING;

-- 8) Contratos dos clientes
INSERT INTO public.tb_cad_contrato (codigo, codigo_cliente, data_inicio, data_fim, descricao, status, periodo_manutencao_preventiva, conexao_internet, vencimento_manutencao_preventiva) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, '2026-01-01', '2026-12-31', 'Contrato Alpha 2026', 'ATIVO', 180, true,  '2026-07-01'),
    (2, 2, '2026-02-01', '2027-01-31', 'Contrato Beta 2026',  'ATIVO', 120, true,  '2026-06-01'),
    (3, 3, '2026-03-01', '2026-09-30', 'Contrato Gamma 2026', 'SUSPENSO', 90, false, '2026-06-01'),
    (4, 4, '2026-04-01', '2027-03-31', 'Contrato Delta 2026', 'ATIVO', 90, true,  '2026-07-01')
ON CONFLICT (codigo) DO NOTHING;

-- 9) Tipos de manutencao
INSERT INTO public.tb_cad_tipo_manutencao (codigo, descricao, observacao) OVERRIDING SYSTEM VALUE VALUES
    (1, 'PREVENTIVA', 'Manutencao planejada conforme periodo do contrato.'),
    (2, 'CORRETIVA', 'Manutencao para correcao de falha identificada.'),
    (3, 'INSTALACAO', 'Instalacao inicial de maquina, software ou componente.'),
    (4, 'ATUALIZACAO_SOFTWARE', 'Atualizacao, licenciamento ou reinstalacao de software.'),
    (5, 'INSPECAO_TECNICA', 'Visita tecnica para avaliacao e diagnostico.')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO public.tb_cad_tipo_manutencao_habilidade (codigo_tipo_manutencao, codigo_habilidade, obrigatoria) VALUES
    (1, 1, true), (1, 2, true), (1, 5, true),
    (2, 1, true), (2, 2, true), (2, 6, false),
    (3, 1, true), (3, 3, true), (3, 5, true),
    (4, 3, true), (4, 4, true),
    (5, 1, false), (5, 2, false), (5, 5, true)
ON CONFLICT (codigo_tipo_manutencao, codigo_habilidade) DO NOTHING;

-- 10) Maquinas vinculadas aos contratos
INSERT INTO public.tb_srv_maquina_contrato (codigo, codigo_contrato, codigo_catalogo_maquina, numero_serie, data_instalacao, manutencao_feita, trabalho_em_altura, latitude, longitude) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 1, 'CNC-X200-ALP-001', '2026-01-15', 'Preventiva inicial realizada', false, -22.905560, -47.060830),
    (2, 2, 2, 'INJ-PL450-BET-001', '2026-02-10', 'Instalacao validada', true, -22.883200, -43.103400),
    (3, 4, 3, 'CMP-80L-DEL-001', '2026-04-05', 'Comissionamento realizado', false, -23.550520, -46.633308),
    (4, 1, 3, 'CMP-80L-ALP-002', '2026-03-20', 'Preventiva pendente', false, -22.905560, -47.060830)
ON CONFLICT (codigo) DO NOTHING;

-- 11) Softwares instalados nas maquinas
INSERT INTO public.tb_srv_maquina_software_instalado (codigo, codigo_maquina_contrato, codigo_software, chave_licenca, versao_instalada) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 1, 'WIN11-ALPHA-CNC-001', '23H2'),
    (2, 1, 2, 'TV-ALPHA-CNC-001', '15.54'),
    (3, 2, 3, 'ERP-BETA-INJ-001', '2.8.1'),
    (4, 3, 5, 'AV-DELTA-CMP-001', '2026.04'),
    (5, 4, 2, 'TV-ALPHA-CMP-002', '15.54')
ON CONFLICT (codigo) DO NOTHING;

-- 12) Historicos de manutencao
INSERT INTO public.tb_srv_maquina_historico_manutencao (codigo, codigo_maquina_contrato, codigo_software_instalado, codigo_tipo_manutencao, status, criticidade, vencimento, data_agendamento, data_inicio_execucao, data_fim_execucao, observacao_geral) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 1, 1, 'FINALIZADA', 'MEDIA', '2026-07-01 09:00:00', '2026-04-20 09:00:00', '2026-04-20 09:15:00', '2026-04-20 11:30:00', 'Preventiva realizada no router CNC.'),
    (2, 2, 3, 2, 'EM_EXECUCAO', 'ALTA', '2026-06-01 09:00:00', '2026-04-26 13:00:00', '2026-04-26 13:20:00', NULL, 'Falha intermitente na comunicacao com o ERP Tracker Agent.'),
    (3, 3, 4, 4, 'AGENDADA', 'BAIXA', '2026-07-01 09:00:00', '2026-04-29 10:00:00', NULL, NULL, 'Atualizacao de antivirus endpoint.'),
    (4, 4, 5, 5, 'ABERTA', 'MEDIA', '2026-05-15 09:00:00', '2026-04-30 08:30:00', NULL, NULL, 'Inspecao tecnica do compressor reserva.'),
    (5, 1, 2, 3, 'FINALIZADA', 'MEDIA', '2026-05-05 09:00:00', '2026-04-10 08:00:00', '2026-04-10 08:30:00', '2026-04-10 10:00:00', 'Instalacao e validacao do acesso remoto.')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO public.tb_srv_maquina_funcionario_manutencao (codigo_historico_manutencao, codigo_funcionario) VALUES
    (1, 2),
    (2, 3),
    (2, 4),
    (3, 3),
    (4, 2),
    (5, 3)
ON CONFLICT (codigo_historico_manutencao, codigo_funcionario) DO NOTHING;

-- 13) Checklist executado nas manutencoes
INSERT INTO public.tb_srv_maquina_checklist_manutencao (codigo, codigo_historico_manutencao, codigo_tarefa, realizado, observacao_tarefa) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 1, true, 'Cabos conferidos sem avarias.'),
    (2, 1, 2, true, 'Limpeza realizada nos filtros.'),
    (3, 1, 4, true, 'Sensores e emergencia testados.'),
    (4, 1, 10, true, 'Evidencias registradas.'),
    (5, 2, 6, false, 'Backup pendente por falha de comunicacao.'),
    (6, 2, 9, false, 'Rede oscilando durante os testes.'),
    (7, 3, 7, true, 'Licenciamento conferido.'),
    (8, 3, 8, false, 'Atualizacao agendada.'),
    (9, 4, 2, false, 'Limpeza sera feita na visita.'),
    (10, 5, 9, true, 'Acesso remoto validado.')
ON CONFLICT (codigo) DO NOTHING;

-- 14) Observacoes das manutencoes
INSERT INTO public.tb_srv_maquina_observacao_manutencao (codigo, codigo_historico_manutencao, codigo_funcionario, observacao, data_criacao) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 2, 'Cliente acompanhou a preventiva e aprovou os testes finais.', '2026-04-20 11:35:00'),
    (2, 2, 3, 'Identificada perda de comunicacao com o agente do ERP.', '2026-04-26 14:10:00'),
    (3, 2, 4, 'Supervisor solicitou retorno com cabo de rede reserva.', '2026-04-26 14:30:00'),
    (4, 3, 3, 'Atualizacao sera executada fora do horario de producao.', '2026-04-26 15:00:00'),
    (5, 4, 2, 'Aguardando confirmacao de parada da linha pelo cliente.', '2026-04-26 16:00:00')
ON CONFLICT (codigo) DO NOTHING;

-- 15) Trocas imprevistas em manutencao
INSERT INTO public.tb_srv_maquina_troca_imprevista_manutencao (codigo, codigo_historico_manutencao, codigo_funcionario, codigo_ativo_utilizado, observacao, data_criacao) OVERRIDING SYSTEM VALUE VALUES
    (1, 2, 3, 8, 'SSD separado para teste caso a falha seja no armazenamento.', '2026-04-26 14:45:00'),
    (2, 2, 4, 9, 'Memoria reserva separada para diagnostico.', '2026-04-26 14:50:00'),
    (3, 4, 2, 10, 'Fonte reserva prevista para inspecao do compressor.', '2026-04-26 16:05:00')
ON CONFLICT (codigo) DO NOTHING;

-- 16) Ferramentas levadas pelos tecnicos
INSERT INTO public.tb_srv_tecnico_ferramenta_ativo (codigo, codigo_historico_manutencao, codigo_funcionario, codigo_ativo, descricao_ativo, levado, devolvido, observacao) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 2, 1, 'Multimetro Digital', true, true, 'Utilizado na preventiva e devolvido.'),
    (2, 1, 2, 4, 'Kit chave isolada', true, true, 'Sem avarias no retorno.'),
    (3, 2, 3, 6, 'Notebook tecnico', true, false, 'Em uso no atendimento atual.'),
    (4, 2, 4, 3, 'Alicate amperimetro', true, false, 'Separado para diagnostico eletrico.'),
    (5, 4, 2, 2, 'Alicate amperimetro', false, false, 'Reservado para proxima visita.')
ON CONFLICT (codigo) DO NOTHING;

-- 17) Ordens de servico
INSERT INTO public.tb_srv_ordem_servico (codigo, codigo_cliente, codigo_funcionario, codigo_software_instalado, codigo_contrato, codigo_maquina_contrato, status, criticidade, data_abertura, data_agendamento, data_inicio_execucao, data_fim_execucao, observacao_geral) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 2, 1, 1, 1, 'FINALIZADA', 'MEDIA', '2026-04-19 08:10:00', '2026-04-20 09:00:00', '2026-04-20 09:15:00', '2026-04-20 11:30:00', 'OS preventiva do Router CNC X200.'),
    (2, 2, 3, 3, 2, 2, 'EM_EXECUCAO', 'ALTA', '2026-04-26 09:20:00', '2026-04-26 13:00:00', '2026-04-26 13:20:00', NULL, 'OS corretiva por falha de comunicacao.'),
    (3, 4, 3, 4, 4, 3, 'AGENDADA', 'BAIXA', '2026-04-25 10:00:00', '2026-04-29 10:00:00', NULL, NULL, 'OS para atualizacao de antivirus endpoint.'),
    (4, 1, 2, 5, 1, 4, 'ABERTA', 'MEDIA', '2026-04-26 15:00:00', '2026-04-30 08:30:00', NULL, NULL, 'OS de inspecao tecnica do compressor reserva.')
ON CONFLICT (codigo) DO NOTHING;

-- 18) Checklist de ativos das ordens de servico
INSERT INTO public.tb_srv_ordem_servico_checklist_ativo (codigo, codigo_ordem_servico, codigo_funcionario, codigo_ativo, descricao_ativo, levado, devolvido, observacao) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 2, 1, 'Multimetro Digital', true, true, 'Levado e devolvido no mesmo dia.'),
    (2, 1, 2, 4, 'Kit chave isolada', true, true, 'Usado na preventiva.'),
    (3, 2, 3, 6, 'Notebook tecnico', true, false, 'Em uso na OS em execucao.'),
    (4, 2, 4, 8, 'SSD 512GB SATA', true, false, 'Separado para troca, se necessario.'),
    (5, 3, 3, 11, 'Mouse USB', false, false, 'Reserva para atendimento.'),
    (6, 4, 2, 2, 'Alicate amperimetro', false, false, 'Planejado para a visita.')
ON CONFLICT (codigo) DO NOTHING;

-- 19) Viagens
INSERT INTO public.tb_srv_viagem (codigo, codigo_tipo_viagem, codigo_cliente, codigo_funcionario_responsavel, codigo_ordem_servico, status, data_cadastro, data_saida_prevista, data_saida_real, data_retorno_previsto, data_retorno_real, origem, destino, km_previsto, km_real, observacao) OVERRIDING SYSTEM VALUE VALUES
    (1, 3, 1, 2, 1, 'FINALIZADA', '2026-04-19 08:20:00', '2026-04-20 07:30:00', '2026-04-20 07:35:00', '2026-04-20 12:30:00', '2026-04-20 12:20:00', 'Sao Paulo - SP', 'Campinas - SP', 95.00, 92.50, 'Viagem para manutencao preventiva.'),
    (2, 1, 2, 3, 2, 'EM_ANDAMENTO', '2026-04-26 09:30:00', '2026-04-26 11:00:00', '2026-04-26 11:10:00', '2026-04-26 18:00:00', NULL, 'Rio de Janeiro - RJ', 'Niteroi - RJ', 25.00, NULL, 'Atendimento tecnico emergencial.'),
    (3, 2, 4, 3, 3, 'ABERTA', '2026-04-25 10:10:00', '2026-04-29 08:00:00', NULL, '2026-04-29 14:00:00', NULL, 'Sao Paulo - SP', 'Sao Paulo - SP', 18.00, NULL, 'Visita para atualizacao de software.'),
    (4, 3, 1, 2, 4, 'ABERTA', '2026-04-26 15:10:00', '2026-04-30 06:30:00', NULL, '2026-04-30 13:00:00', NULL, 'Sao Paulo - SP', 'Campinas - SP', 95.00, NULL, 'Inspecao do compressor reserva.')
ON CONFLICT (codigo) DO NOTHING;

-- 20) Paradas das viagens
INSERT INTO public.tb_srv_viagem_parada (codigo, codigo_viagem, ordem, descricao_local, endereco, cidade, estado_regiao, latitude, longitude, data_chegada_prevista, data_chegada_real, data_saida_prevista, data_saida_real, observacao) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 1, 'Sede Alpha Industrial', 'Rua Industrial, 100', 'Campinas', 'Sao Paulo', -22.905560, -47.060830, '2026-04-20 09:00:00', '2026-04-20 08:55:00', '2026-04-20 11:45:00', '2026-04-20 11:40:00', 'Atendimento concluido.'),
    (2, 2, 1, 'Unidade Beta Logistics', 'Av. Roberto Silveira, 250', 'Niteroi', 'Rio de Janeiro', -22.883200, -43.103400, '2026-04-26 13:00:00', '2026-04-26 12:55:00', '2026-04-26 17:00:00', NULL, 'Atendimento em andamento.'),
    (3, 3, 1, 'Delta Tecnologia', 'Av. Paulista, 1000', 'Sao Paulo', 'Sao Paulo', -23.565000, -46.652000, '2026-04-29 10:00:00', NULL, '2026-04-29 12:00:00', NULL, 'Atualizacao agendada.'),
    (4, 4, 1, 'Sede Alpha Industrial', 'Rua Industrial, 100', 'Campinas', 'Sao Paulo', -22.905560, -47.060830, '2026-04-30 08:30:00', NULL, '2026-04-30 11:30:00', NULL, 'Inspecao pendente.'),
    (5, 4, 2, 'Posto de Apoio Tecnico', 'Rodovia SP-330, km 70', 'Louveira', 'Sao Paulo', -23.086500, -46.950500, '2026-04-30 12:00:00', NULL, '2026-04-30 12:15:00', NULL, 'Parada prevista para abastecimento e conferencia de materiais.')
ON CONFLICT (codigo) DO NOTHING;

-- 21) Checklist dinamico das viagens
INSERT INTO public.tb_srv_viagem_checklist_dinamico (codigo, codigo_viagem, descricao_item, conferido, data_conferencia, observacao) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 'Separar ordem de servico impressa', true, '2026-04-20 07:00:00', 'Documento levado pelo tecnico.'),
    (2, 1, 'Conferir ferramentas obrigatorias', true, '2026-04-20 07:05:00', 'Ferramentas conferidas.'),
    (3, 1, 'Registrar KM de saida', true, '2026-04-20 07:35:00', 'KM registrado no inicio da viagem.'),
    (4, 2, 'Confirmar contato do cliente', true, '2026-04-26 10:00:00', 'Contato confirmado com Bruno Lima.'),
    (5, 2, 'Separar notebook tecnico', true, '2026-04-26 10:20:00', 'Notebook tecnico em posse da equipe.'),
    (6, 2, 'Registrar KM de retorno', false, NULL, 'Pendente ate finalizar a viagem.'),
    (7, 3, 'Confirmar janela de atualizacao', false, NULL, 'Aguardando confirmacao do cliente.'),
    (8, 3, 'Validar acesso remoto antes da saida', false, NULL, 'Sera validado no dia da visita.'),
    (9, 4, 'Separar alicate amperimetro', false, NULL, 'Planejado para a visita de inspecao.'),
    (10, 4, 'Conferir endereco e rota', false, NULL, 'Pendente.')
ON CONFLICT (codigo) DO NOTHING;

-- 22) Ajuste das sequencias para evitar colisao nos proximos inserts
SELECT pg_catalog.setval('public.tb_cad_permissao_id_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_cad_permissao), 1), true);
SELECT pg_catalog.setval('public.tb_cad_usuario_id_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_cad_usuario), 1), true);
SELECT pg_catalog.setval('public.tb_cad_habilidade_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_cad_habilidade), 1), true);
SELECT pg_catalog.setval('public.tb_cad_funcionario_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_cad_funcionario), 1), true);
SELECT pg_catalog.setval('public.tb_cad_ativo_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_cad_ativo), 1), true);
SELECT pg_catalog.setval('public.tb_cad_cliente_contato_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_cad_cliente_contato), 1), true);
SELECT pg_catalog.setval('public.tb_cad_contrato_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_cad_contrato), 1), true);
SELECT pg_catalog.setval('public.tb_cad_tipo_manutencao_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_cad_tipo_manutencao), 1), true);
SELECT pg_catalog.setval('public.tb_srv_maquina_contrato_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_maquina_contrato), 1), true);
SELECT pg_catalog.setval('public.tb_srv_maquina_software_instalado_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_maquina_software_instalado), 1), true);
SELECT pg_catalog.setval('public.tb_srv_maquina_historico_manutencao_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_maquina_historico_manutencao), 1), true);
SELECT pg_catalog.setval('public.tb_srv_maquina_checklist_manutencao_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_maquina_checklist_manutencao), 1), true);
SELECT pg_catalog.setval('public.tb_srv_maquina_observacao_manutencao_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_maquina_observacao_manutencao), 1), true);
SELECT pg_catalog.setval('public.tb_srv_maquina_troca_imprevista_manutencao_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_maquina_troca_imprevista_manutencao), 1), true);
SELECT pg_catalog.setval('public.tb_srv_tecnico_ferramenta_ativo_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_tecnico_ferramenta_ativo), 1), true);
SELECT pg_catalog.setval('public.tb_srv_ordem_servico_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_ordem_servico), 1), true);
SELECT pg_catalog.setval('public.tb_srv_ordem_servico_checklist_ativo_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_ordem_servico_checklist_ativo), 1), true);
SELECT pg_catalog.setval('public.tb_srv_ciclo_embarcacao_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_ciclo_embarcacao), 1), true);
SELECT pg_catalog.setval('public.tb_srv_viagem_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_viagem), 1), true);
SELECT pg_catalog.setval('public.tb_srv_viagem_parada_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_viagem_parada), 1), true);
SELECT pg_catalog.setval('public.tb_srv_viagem_checklist_dinamico_codigo_seq', COALESCE((SELECT MAX(codigo) FROM public.tb_srv_viagem_checklist_dinamico), 1), true);

COMMIT;


BEGIN;


ALTER TABLE public.tb_srv_ordem_servico
    ADD COLUMN IF NOT EXISTS data_inicio_prevista timestamp without time zone,
    ADD COLUMN IF NOT EXISTS data_fim_prevista timestamp without time zone;


ALTER TABLE public.tb_cad_usuario
    ADD COLUMN IF NOT EXISTS imagem_perfil character varying(500);


ALTER TABLE public.tb_cad_contrato
    ADD COLUMN IF NOT EXISTS certificacao_requerida character varying(255);

ALTER TABLE public.tb_srv_maquina_contrato
    ADD COLUMN IF NOT EXISTS certificacao_requerida character varying(255);

ALTER TABLE public.tb_cad_ativo
    ADD COLUMN IF NOT EXISTS codigo_maquina_contrato integer;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_ativo_maquina_contrato'
    ) THEN
        ALTER TABLE ONLY public.tb_cad_ativo
            ADD CONSTRAINT fk_ativo_maquina_contrato
            FOREIGN KEY (codigo_maquina_contrato)
            REFERENCES public.tb_srv_maquina_contrato(codigo)
            ON DELETE SET NULL;
    END IF;
END $$;


CREATE TABLE IF NOT EXISTS public.tb_cad_status_tecnico (
    codigo character varying(50) NOT NULL PRIMARY KEY,
    descricao character varying(100) NOT NULL,
    ativo boolean DEFAULT true NOT NULL
);

ALTER TABLE public.tb_cad_status_tecnico OWNER TO user_dev;

INSERT INTO public.tb_cad_status_tecnico (codigo, descricao, ativo) VALUES
    ('DISPONIVEL', 'Disponivel', true),
    ('EM_ATENDIMENTO', 'Em atendimento', true),
    ('EM_ROTA', 'Em rota', true),
    ('EM_VIAGEM', 'Em viagem', true),
    ('INDISPONIVEL', 'Indisponivel', true)
ON CONFLICT (codigo) DO NOTHING;


CREATE TABLE IF NOT EXISTS public.tb_srv_ordem_servico_funcionario (
    codigo_ordem_servico integer NOT NULL,
    codigo_funcionario integer NOT NULL
);

ALTER TABLE public.tb_srv_ordem_servico_funcionario OWNER TO user_dev;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'pk_ordem_servico_funcionario'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_ordem_servico_funcionario
            ADD CONSTRAINT pk_ordem_servico_funcionario
            PRIMARY KEY (codigo_ordem_servico, codigo_funcionario);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_ordem_servico_funcionario_ordem'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_ordem_servico_funcionario
            ADD CONSTRAINT fk_ordem_servico_funcionario_ordem
            FOREIGN KEY (codigo_ordem_servico)
            REFERENCES public.tb_srv_ordem_servico(codigo)
            ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_ordem_servico_funcionario_funcionario'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_ordem_servico_funcionario
            ADD CONSTRAINT fk_ordem_servico_funcionario_funcionario
            FOREIGN KEY (codigo_funcionario)
            REFERENCES public.tb_cad_funcionario(codigo)
            ON DELETE CASCADE;
    END IF;
END $$;

INSERT INTO public.tb_srv_ordem_servico_funcionario (codigo_ordem_servico, codigo_funcionario)
SELECT codigo, codigo_funcionario
FROM public.tb_srv_ordem_servico
WHERE codigo_funcionario IS NOT NULL
ON CONFLICT (codigo_ordem_servico, codigo_funcionario) DO NOTHING;

-- 6) Habilidades agregadas ao contrato.
CREATE TABLE IF NOT EXISTS public.tb_cad_contrato_habilidade (
    codigo_contrato integer NOT NULL,
    codigo_habilidade integer NOT NULL,
    nivel_minimo integer,
    obrigatoria boolean DEFAULT true NOT NULL,
    observacao text
);

ALTER TABLE public.tb_cad_contrato_habilidade OWNER TO user_dev;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'pk_contrato_habilidade'
    ) THEN
        ALTER TABLE ONLY public.tb_cad_contrato_habilidade
            ADD CONSTRAINT pk_contrato_habilidade
            PRIMARY KEY (codigo_contrato, codigo_habilidade);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_contrato_habilidade_contrato'
    ) THEN
        ALTER TABLE ONLY public.tb_cad_contrato_habilidade
            ADD CONSTRAINT fk_contrato_habilidade_contrato
            FOREIGN KEY (codigo_contrato)
            REFERENCES public.tb_cad_contrato(codigo)
            ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_contrato_habilidade_habilidade'
    ) THEN
        ALTER TABLE ONLY public.tb_cad_contrato_habilidade
            ADD CONSTRAINT fk_contrato_habilidade_habilidade
            FOREIGN KEY (codigo_habilidade)
            REFERENCES public.tb_cad_habilidade(codigo)
            ON DELETE CASCADE;
    END IF;
END $$;


CREATE TABLE IF NOT EXISTS public.tb_srv_maquina_contrato_habilidade (
    codigo_maquina_contrato integer NOT NULL,
    codigo_habilidade integer NOT NULL,
    nivel_minimo integer,
    obrigatoria boolean DEFAULT true NOT NULL,
    observacao text
);

ALTER TABLE public.tb_srv_maquina_contrato_habilidade OWNER TO user_dev;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'pk_maquina_contrato_habilidade'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_maquina_contrato_habilidade
            ADD CONSTRAINT pk_maquina_contrato_habilidade
            PRIMARY KEY (codigo_maquina_contrato, codigo_habilidade);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_maquina_contrato_habilidade_maquina'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_maquina_contrato_habilidade
            ADD CONSTRAINT fk_maquina_contrato_habilidade_maquina
            FOREIGN KEY (codigo_maquina_contrato)
            REFERENCES public.tb_srv_maquina_contrato(codigo)
            ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_maquina_contrato_habilidade_habilidade'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_maquina_contrato_habilidade
            ADD CONSTRAINT fk_maquina_contrato_habilidade_habilidade
            FOREIGN KEY (codigo_habilidade)
            REFERENCES public.tb_cad_habilidade(codigo)
            ON DELETE CASCADE;
    END IF;
END $$;


CREATE TABLE IF NOT EXISTS public.tb_srv_maquina_ativo_historico (
    codigo integer GENERATED ALWAYS AS IDENTITY,
    codigo_maquina_contrato integer NOT NULL,
    codigo_ativo integer NOT NULL,
    codigo_ordem_servico integer,
    codigo_historico_manutencao integer,
    codigo_tipo_manutencao integer,
    codigo_funcionario integer,
    descricao_ativo character varying(255),
    levado boolean DEFAULT false NOT NULL,
    utilizado boolean DEFAULT false NOT NULL,
    devolvido boolean DEFAULT false NOT NULL,
    status_intervencao character varying(50),
    data_registro timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_utilizacao timestamp without time zone,
    observacao text
);

ALTER TABLE public.tb_srv_maquina_ativo_historico OWNER TO user_dev;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'tb_srv_maquina_ativo_historico_pkey'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_maquina_ativo_historico
            ADD CONSTRAINT tb_srv_maquina_ativo_historico_pkey
            PRIMARY KEY (codigo);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ck_maquina_ativo_historico_status'
    ) THEN
        ALTER TABLE public.tb_srv_maquina_ativo_historico
            ADD CONSTRAINT ck_maquina_ativo_historico_status
            CHECK (
                status_intervencao IS NULL OR
                status_intervencao IN ('PLANEJADO', 'LEVADO', 'UTILIZADO', 'NAO_UTILIZADO', 'DEVOLVIDO', 'CANCELADO')
            );
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_maquina_ativo_historico_maquina'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_maquina_ativo_historico
            ADD CONSTRAINT fk_maquina_ativo_historico_maquina
            FOREIGN KEY (codigo_maquina_contrato)
            REFERENCES public.tb_srv_maquina_contrato(codigo)
            ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_maquina_ativo_historico_ativo'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_maquina_ativo_historico
            ADD CONSTRAINT fk_maquina_ativo_historico_ativo
            FOREIGN KEY (codigo_ativo)
            REFERENCES public.tb_cad_ativo(codigo)
            ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_maquina_ativo_historico_ordem'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_maquina_ativo_historico
            ADD CONSTRAINT fk_maquina_ativo_historico_ordem
            FOREIGN KEY (codigo_ordem_servico)
            REFERENCES public.tb_srv_ordem_servico(codigo)
            ON DELETE SET NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_maquina_ativo_historico_historico'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_maquina_ativo_historico
            ADD CONSTRAINT fk_maquina_ativo_historico_historico
            FOREIGN KEY (codigo_historico_manutencao)
            REFERENCES public.tb_srv_maquina_historico_manutencao(codigo)
            ON DELETE SET NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_maquina_ativo_historico_tipo'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_maquina_ativo_historico
            ADD CONSTRAINT fk_maquina_ativo_historico_tipo
            FOREIGN KEY (codigo_tipo_manutencao)
            REFERENCES public.tb_cad_tipo_manutencao(codigo)
            ON DELETE SET NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_maquina_ativo_historico_funcionario'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_maquina_ativo_historico
            ADD CONSTRAINT fk_maquina_ativo_historico_funcionario
            FOREIGN KEY (codigo_funcionario)
            REFERENCES public.tb_cad_funcionario(codigo)
            ON DELETE SET NULL;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_ordem_servico_funcionario_funcionario
    ON public.tb_srv_ordem_servico_funcionario (codigo_funcionario);

CREATE INDEX IF NOT EXISTS idx_maquina_ativo_historico_maquina
    ON public.tb_srv_maquina_ativo_historico (codigo_maquina_contrato);

CREATE INDEX IF NOT EXISTS idx_maquina_ativo_historico_ativo
    ON public.tb_srv_maquina_ativo_historico (codigo_ativo);

CREATE INDEX IF NOT EXISTS idx_maquina_ativo_historico_ordem
    ON public.tb_srv_maquina_ativo_historico (codigo_ordem_servico);

COMMIT;
