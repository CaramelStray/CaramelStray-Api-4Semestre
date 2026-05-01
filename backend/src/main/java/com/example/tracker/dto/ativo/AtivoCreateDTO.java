package com.example.tracker.dto.ativo;

import jakarta.validation.constraints.NotNull;

public class AtivoCreateDTO {

    @NotNull(message = "Código do catálogo de ativo é obrigatório")
    private Integer codigoCatalogoAtivo;

    private String numeroSerie;
    private String lote;
    private String descricao;
    private Integer codigoFuncionarioResponsavel;
    private String status;

    public Integer getCodigoCatalogoAtivo() { return codigoCatalogoAtivo; }
    public void setCodigoCatalogoAtivo(Integer codigoCatalogoAtivo) { this.codigoCatalogoAtivo = codigoCatalogoAtivo; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getCodigoFuncionarioResponsavel() { return codigoFuncionarioResponsavel; }
    public void setCodigoFuncionarioResponsavel(Integer codigoFuncionarioResponsavel) { this.codigoFuncionarioResponsavel = codigoFuncionarioResponsavel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
