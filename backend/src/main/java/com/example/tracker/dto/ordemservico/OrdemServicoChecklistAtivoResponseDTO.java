package com.example.tracker.dto.ordemservico;

import com.example.tracker.entity.Ativo;
import com.example.tracker.entity.CatalogoAtivo;
import com.example.tracker.entity.OrdemServicoChecklistAtivo;

public class OrdemServicoChecklistAtivoResponseDTO {

    private Integer codigo;
    private Integer codigoOrdemServico;
    private Integer codigoFuncionario;
    private Integer codigoAtivo;
    private Integer codigoCatalogoAtivo;
    private String descricaoAtivo;
    private String descricaoProduto;
    private String modelo;
    private String marca;
    private String numeroSerie;
    private String lote;
    private String statusAtivo;
    private Boolean levado;
    private Boolean devolvido;
    private String observacao;

    public static OrdemServicoChecklistAtivoResponseDTO fromEntity(OrdemServicoChecklistAtivo item) {
        OrdemServicoChecklistAtivoResponseDTO dto = new OrdemServicoChecklistAtivoResponseDTO();
        dto.setCodigo(item.getCodigo());

        if (item.getOrdemServico() != null) {
            dto.setCodigoOrdemServico(item.getOrdemServico().getCodigo());
        }

        if (item.getFuncionario() != null) {
            dto.setCodigoFuncionario(item.getFuncionario().getId());
        }

        Ativo ativo = item.getAtivo();
        if (ativo != null) {
            dto.setCodigoAtivo(ativo.getCodigo());
            dto.setNumeroSerie(ativo.getNumeroSerie());
            dto.setLote(ativo.getLote());
            dto.setStatusAtivo(ativo.getStatus());

            CatalogoAtivo catalogo = ativo.getCatalogoAtivo();
            if (catalogo != null) {
                dto.setCodigoCatalogoAtivo(catalogo.getCodigo());
                dto.setDescricaoProduto(catalogo.getDescricaoProduto());
                dto.setModelo(catalogo.getModelo());
                dto.setMarca(catalogo.getMarca());
            }
        }

        dto.setDescricaoAtivo(item.getDescricaoAtivo());
        dto.setLevado(Boolean.TRUE.equals(item.getLevado()));
        dto.setDevolvido(Boolean.TRUE.equals(item.getDevolvido()));
        dto.setObservacao(item.getObservacao());
        return dto;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigoOrdemServico() {
        return codigoOrdemServico;
    }

    public void setCodigoOrdemServico(Integer codigoOrdemServico) {
        this.codigoOrdemServico = codigoOrdemServico;
    }

    public Integer getCodigoFuncionario() {
        return codigoFuncionario;
    }

    public void setCodigoFuncionario(Integer codigoFuncionario) {
        this.codigoFuncionario = codigoFuncionario;
    }

    public Integer getCodigoAtivo() {
        return codigoAtivo;
    }

    public void setCodigoAtivo(Integer codigoAtivo) {
        this.codigoAtivo = codigoAtivo;
    }

    public Integer getCodigoCatalogoAtivo() {
        return codigoCatalogoAtivo;
    }

    public void setCodigoCatalogoAtivo(Integer codigoCatalogoAtivo) {
        this.codigoCatalogoAtivo = codigoCatalogoAtivo;
    }

    public String getDescricaoAtivo() {
        return descricaoAtivo;
    }

    public void setDescricaoAtivo(String descricaoAtivo) {
        this.descricaoAtivo = descricaoAtivo;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getStatusAtivo() {
        return statusAtivo;
    }

    public void setStatusAtivo(String statusAtivo) {
        this.statusAtivo = statusAtivo;
    }

    public Boolean getLevado() {
        return levado;
    }

    public void setLevado(Boolean levado) {
        this.levado = levado;
    }

    public Boolean getDevolvido() {
        return devolvido;
    }

    public void setDevolvido(Boolean devolvido) {
        this.devolvido = devolvido;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
