BEGIN;

ALTER TABLE public.tb_srv_ordem_servico
    ADD COLUMN IF NOT EXISTS tipo_ordem character varying(50),
    ADD COLUMN IF NOT EXISTS data_inicio_prevista timestamp without time zone,
    ADD COLUMN IF NOT EXISTS data_fim_prevista timestamp without time zone;

ALTER TABLE public.tb_srv_ordem_servico_checklist_ativo
    ADD COLUMN IF NOT EXISTS status_intervencao character varying(50),
    ADD COLUMN IF NOT EXISTS data_intervencao timestamp without time zone,
    ADD COLUMN IF NOT EXISTS observacao_intervencao text;

ALTER TABLE public.tb_srv_maquina_historico_manutencao
    ADD COLUMN IF NOT EXISTS codigo_ordem_servico integer;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_historico_manutencao_ordem_servico'
    ) THEN
        ALTER TABLE ONLY public.tb_srv_maquina_historico_manutencao
            ADD CONSTRAINT fk_historico_manutencao_ordem_servico
            FOREIGN KEY (codigo_ordem_servico)
            REFERENCES public.tb_srv_ordem_servico(codigo)
            ON DELETE CASCADE;
    END IF;
END $$;

ALTER TABLE public.tb_cad_usuario
    ADD COLUMN IF NOT EXISTS imagem_perfil character varying(500);

ALTER TABLE public.tb_cad_contrato
    ADD COLUMN IF NOT EXISTS certificacao_requerida character varying(255);

ALTER TABLE public.tb_srv_maquina_contrato
    ADD COLUMN IF NOT EXISTS certificacao_requerida character varying(255);

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
