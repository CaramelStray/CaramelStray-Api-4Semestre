ALTER TABLE IF EXISTS tb_cad_usuario RENAME COLUMN id TO codigo;
ALTER TABLE IF EXISTS tb_cad_perfil RENAME COLUMN id TO codigo;
ALTER TABLE IF EXISTS tb_cad_permissao RENAME COLUMN id TO codigo;
ALTER TABLE IF EXISTS tb_cad_cliente RENAME COLUMN id TO codigo;

ALTER TABLE IF EXISTS tb_cad_cliente RENAME COLUMN usuario_id TO codigo_usuario;
ALTER TABLE IF EXISTS tb_cad_cliente RENAME COLUMN nome_empresa TO descricao_empresa;
ALTER TABLE IF EXISTS tb_cad_cliente RENAME COLUMN nome_responsavel TO descricao_responsavel;

ALTER TABLE IF EXISTS tb_cad_cliente DROP COLUMN IF EXISTS criticidade;

ALTER TABLE IF EXISTS tb_cad_usuario_perfil RENAME COLUMN usuario_id TO codigo_usuario;
ALTER TABLE IF EXISTS tb_cad_usuario_perfil RENAME COLUMN perfil_id TO codigo_perfil;

ALTER TABLE IF EXISTS tb_cad_perfil_permissao RENAME COLUMN perfil_id TO codigo_perfil;
ALTER TABLE IF EXISTS tb_cad_perfil_permissao RENAME COLUMN permissao_id TO codigo_permissao;

ALTER TABLE IF EXISTS tb_cad_perfil RENAME COLUMN nome TO descricao;
ALTER TABLE IF EXISTS tb_cad_permissao RENAME COLUMN nome TO descricao;

ALTER TABLE IF EXISTS tb_cad_usuario
    ALTER COLUMN data_criacao SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE IF EXISTS tb_cad_cliente
    ALTER COLUMN data_cadastro SET DEFAULT CURRENT_TIMESTAMP;

CREATE TABLE IF NOT EXISTS tb_cad_funcionario (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_usuario INTEGER UNIQUE,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(20),
    cargo VARCHAR(100),
    telefone VARCHAR(30),
    latitude NUMERIC(10,6),
    longitude NUMERIC(10,6),
    CONSTRAINT fk_funcionario_usuario
        FOREIGN KEY (codigo_usuario) REFERENCES tb_cad_usuario(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_cad_contrato (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_cliente INTEGER NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    status VARCHAR(100),
    CONSTRAINT fk_contrato_cliente
        FOREIGN KEY (codigo_cliente) REFERENCES tb_cad_cliente(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_cad_catalogo_maquina (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    especificacao TEXT,
    limite_manutencao VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS tb_cad_catalogo_software (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_cad_catalogo_tarefa (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    categoria VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS tb_cad_catalogo_ativo (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    descricao_produto VARCHAR(255) NOT NULL,
    modelo VARCHAR(255),
    marca VARCHAR(255),
    descricao TEXT,
    especificacao TEXT,
    tipo VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS tb_cad_tipo_manutencao (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    descricao VARCHAR(100) NOT NULL UNIQUE,
    observacao VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tb_cad_habilidade (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    descricao VARCHAR(100) NOT NULL UNIQUE,
    observacao VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tb_cad_ativo_estoque (
    codigo_catalogo_ativo INTEGER PRIMARY KEY,
    quantidade INTEGER DEFAULT 0 NOT NULL,
    CONSTRAINT fk_ativo_estoque_catalogo
        FOREIGN KEY (codigo_catalogo_ativo) REFERENCES tb_cad_catalogo_ativo(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_cad_ativo (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_catalogo_ativo INTEGER NOT NULL,
    numero_serie VARCHAR(255),
    lote VARCHAR(255),
    descricao TEXT,
    codigo_funcionario_responsavel INTEGER,
    status VARCHAR(100),
    CONSTRAINT fk_ativo_catalogo
        FOREIGN KEY (codigo_catalogo_ativo) REFERENCES tb_cad_catalogo_ativo(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_ativo_funcionario
        FOREIGN KEY (codigo_funcionario_responsavel) REFERENCES tb_cad_funcionario(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_srv_maquina_contrato (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_contrato INTEGER NOT NULL,
    codigo_catalogo_maquina INTEGER NOT NULL,
    numero_serie VARCHAR(255),
    data_instalacao DATE,
    manutencao_feita VARCHAR(255),
    trabalho_em_altura BOOLEAN DEFAULT FALSE NOT NULL,
    latitude NUMERIC(10,6),
    longitude NUMERIC(10,6),
    CONSTRAINT fk_maquina_contrato_contrato
        FOREIGN KEY (codigo_contrato) REFERENCES tb_cad_contrato(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_maquina_contrato_catalogo
        FOREIGN KEY (codigo_catalogo_maquina) REFERENCES tb_cad_catalogo_maquina(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_srv_maquina_software_instalado (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_maquina_contrato INTEGER NOT NULL,
    codigo_software INTEGER NOT NULL,
    chave_licenca VARCHAR(255),
    versao_instalada VARCHAR(100),
    CONSTRAINT fk_software_instalado_maquina
        FOREIGN KEY (codigo_maquina_contrato) REFERENCES tb_srv_maquina_contrato(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_software_instalado_software
        FOREIGN KEY (codigo_software) REFERENCES tb_cad_catalogo_software(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_cad_catalogo_maquina_checklist_padrao (
    codigo_catalogo_maquina INTEGER NOT NULL,
    codigo_tarefa INTEGER NOT NULL,
    CONSTRAINT pk_checklist_padrao_maquina PRIMARY KEY (codigo_catalogo_maquina, codigo_tarefa),
    CONSTRAINT fk_checklist_padrao_maquina_catalogo
        FOREIGN KEY (codigo_catalogo_maquina) REFERENCES tb_cad_catalogo_maquina(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_checklist_padrao_maquina_tarefa
        FOREIGN KEY (codigo_tarefa) REFERENCES tb_cad_catalogo_tarefa(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_cad_catalogo_software_checklist_padrao (
    codigo_catalogo_software INTEGER NOT NULL,
    codigo_catalogo_tarefa INTEGER NOT NULL,
    obrigatorio BOOLEAN NOT NULL,
    CONSTRAINT pk_checklist_padrao_software PRIMARY KEY (codigo_catalogo_software, codigo_catalogo_tarefa),
    CONSTRAINT fk_checklist_padrao_software_software
        FOREIGN KEY (codigo_catalogo_software) REFERENCES tb_cad_catalogo_software(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_checklist_padrao_software_tarefa
        FOREIGN KEY (codigo_catalogo_tarefa) REFERENCES tb_cad_catalogo_tarefa(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_cad_catalogo_maquina_ferramenta_modelo (
    codigo_catalogo_maquina INTEGER NOT NULL,
    codigo_catalogo_ativo INTEGER NOT NULL,
    quantidade_necessaria INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT pk_ferramenta_modelo_maquina PRIMARY KEY (codigo_catalogo_maquina, codigo_catalogo_ativo),
    CONSTRAINT fk_ferramenta_modelo_maquina_catalogo_maquina
        FOREIGN KEY (codigo_catalogo_maquina) REFERENCES tb_cad_catalogo_maquina(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_ferramenta_modelo_maquina_catalogo_ativo
        FOREIGN KEY (codigo_catalogo_ativo) REFERENCES tb_cad_catalogo_ativo(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_cad_funcionario_habilidade (
    codigo_funcionario INTEGER NOT NULL,
    codigo_habilidade INTEGER NOT NULL,
    nivel INTEGER,
    data_validade DATE,
    CONSTRAINT pk_funcionario_habilidade PRIMARY KEY (codigo_funcionario, codigo_habilidade),
    CONSTRAINT fk_funcionario_habilidade_funcionario
        FOREIGN KEY (codigo_funcionario) REFERENCES tb_cad_funcionario(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_funcionario_habilidade_habilidade
        FOREIGN KEY (codigo_habilidade) REFERENCES tb_cad_habilidade(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_cad_tipo_manutencao_habilidade (
    codigo_tipo_manutencao INTEGER NOT NULL,
    codigo_habilidade INTEGER NOT NULL,
    obrigatoria BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT pk_tipo_manutencao_habilidade PRIMARY KEY (codigo_tipo_manutencao, codigo_habilidade),
    CONSTRAINT fk_tipo_manutencao_habilidade_tipo
        FOREIGN KEY (codigo_tipo_manutencao) REFERENCES tb_cad_tipo_manutencao(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_tipo_manutencao_habilidade_habilidade
        FOREIGN KEY (codigo_habilidade) REFERENCES tb_cad_habilidade(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_srv_maquina_historico_manutencao (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_maquina_contrato INTEGER NOT NULL,
    codigo_software_instalado INTEGER,
    codigo_tipo_manutencao INTEGER NOT NULL,
    status VARCHAR(100),
    criticidade VARCHAR(50),
    vencimento TIMESTAMP,
    data_agendamento TIMESTAMP,
    data_inicio_execucao TIMESTAMP,
    data_fim_execucao TIMESTAMP,
    observacao_geral TEXT,
    CONSTRAINT fk_historico_manutencao_maquina
        FOREIGN KEY (codigo_maquina_contrato) REFERENCES tb_srv_maquina_contrato(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_historico_manutencao_software
        FOREIGN KEY (codigo_software_instalado) REFERENCES tb_srv_maquina_software_instalado(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_historico_manutencao_tipo
        FOREIGN KEY (codigo_tipo_manutencao) REFERENCES tb_cad_tipo_manutencao(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_srv_maquina_funcionario_manutencao (
    codigo_historico_manutencao INTEGER NOT NULL,
    codigo_funcionario INTEGER NOT NULL,
    CONSTRAINT pk_funcionario_manutencao PRIMARY KEY (codigo_historico_manutencao, codigo_funcionario),
    CONSTRAINT fk_funcionario_manutencao_historico
        FOREIGN KEY (codigo_historico_manutencao) REFERENCES tb_srv_maquina_historico_manutencao(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_funcionario_manutencao_funcionario
        FOREIGN KEY (codigo_funcionario) REFERENCES tb_cad_funcionario(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_srv_maquina_checklist_manutencao (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_historico_manutencao INTEGER NOT NULL,
    codigo_tarefa INTEGER NOT NULL,
    realizado BOOLEAN,
    observacao_tarefa TEXT,
    CONSTRAINT fk_checklist_manutencao_historico
        FOREIGN KEY (codigo_historico_manutencao) REFERENCES tb_srv_maquina_historico_manutencao(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_checklist_manutencao_tarefa
        FOREIGN KEY (codigo_tarefa) REFERENCES tb_cad_catalogo_tarefa(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_srv_maquina_checklist_ferramenta_manutencao (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_historico_manutencao INTEGER NOT NULL,
    codigo_catalogo_ativo INTEGER NOT NULL,
    codigo_ativo INTEGER,
    levou BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT fk_checklist_ferramenta_historico
        FOREIGN KEY (codigo_historico_manutencao) REFERENCES tb_srv_maquina_historico_manutencao(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_checklist_ferramenta_catalogo_ativo
        FOREIGN KEY (codigo_catalogo_ativo) REFERENCES tb_cad_catalogo_ativo(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_checklist_ferramenta_ativo
        FOREIGN KEY (codigo_ativo) REFERENCES tb_cad_ativo(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_srv_maquina_observacao_manutencao (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_historico_manutencao INTEGER NOT NULL,
    codigo_funcionario INTEGER NOT NULL,
    observacao TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_observacao_manutencao_historico
        FOREIGN KEY (codigo_historico_manutencao) REFERENCES tb_srv_maquina_historico_manutencao(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_observacao_manutencao_funcionario
        FOREIGN KEY (codigo_funcionario) REFERENCES tb_cad_funcionario(codigo)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_srv_maquina_troca_imprevista_manutencao (
    codigo INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_historico_manutencao INTEGER NOT NULL,
    codigo_funcionario INTEGER NOT NULL,
    codigo_ativo_utilizado INTEGER NOT NULL,
    observacao TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_troca_imprevista_historico
        FOREIGN KEY (codigo_historico_manutencao) REFERENCES tb_srv_maquina_historico_manutencao(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_troca_imprevista_funcionario
        FOREIGN KEY (codigo_funcionario) REFERENCES tb_cad_funcionario(codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_troca_imprevista_ativo
        FOREIGN KEY (codigo_ativo_utilizado) REFERENCES tb_cad_ativo(codigo)
        ON DELETE CASCADE
);
