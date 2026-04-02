ALTER TABLE public.tb_cad_catalogo_software
    ADD COLUMN IF NOT EXISTS versao character varying(100);

ALTER TABLE public.tb_cad_catalogo_software
    ADD COLUMN IF NOT EXISTS tipo character varying(100);

ALTER TABLE public.tb_cad_catalogo_software
    ADD COLUMN IF NOT EXISTS desenvolvedor_fornecedor character varying(255);

ALTER TABLE public.tb_cad_catalogo_software
    ADD COLUMN IF NOT EXISTS url_documentacao character varying(500);

ALTER TABLE public.tb_cad_catalogo_software
    ADD COLUMN IF NOT EXISTS descricao_tecnica text;

ALTER TABLE public.tb_cad_catalogo_software
    ADD COLUMN IF NOT EXISTS ativo boolean DEFAULT true;

ALTER TABLE public.tb_cad_catalogo_software
    ADD COLUMN IF NOT EXISTS data_cadastro timestamp without time zone DEFAULT CURRENT_TIMESTAMP;

UPDATE public.tb_cad_catalogo_software
SET versao = COALESCE(NULLIF(TRIM(versao), ''), 'v1.0.0'),
    tipo = COALESCE(NULLIF(TRIM(tipo), ''), 'SOFTWARE'),
    ativo = COALESCE(ativo, true),
    data_cadastro = COALESCE(data_cadastro, CURRENT_TIMESTAMP)
WHERE versao IS NULL
   OR TRIM(versao) = ''
   OR tipo IS NULL
   OR TRIM(tipo) = ''
   OR ativo IS NULL
   OR data_cadastro IS NULL;

ALTER TABLE public.tb_cad_catalogo_software
    ALTER COLUMN versao SET NOT NULL;

ALTER TABLE public.tb_cad_catalogo_software
    ALTER COLUMN tipo SET NOT NULL;

ALTER TABLE public.tb_cad_catalogo_software
    ALTER COLUMN ativo SET NOT NULL;

ALTER TABLE public.tb_cad_catalogo_software
    ALTER COLUMN data_cadastro SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_cad_catalogo_software_descricao_versao'
    ) THEN
        ALTER TABLE public.tb_cad_catalogo_software
            ADD CONSTRAINT uq_cad_catalogo_software_descricao_versao UNIQUE (descricao, versao);
    END IF;
END $$;
