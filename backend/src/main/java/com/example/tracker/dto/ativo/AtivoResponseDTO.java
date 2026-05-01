package com.example.tracker.dto.ativo;

import com.example.tracker.entity.Ativo;

public class AtivoResponseDTO {

    private Integer codigo;
    private Integer codigoCatalogoAtivo;
    private String descricaoProduto;
    private String modelo;
    private String marca;
    private String tipo;
    private String numeroSerie;
    private String lote;
    private String descricao;
    private Integer codigoFuncionarioResponsavel;
    private String nomeFuncionarioResponsavel;
    private String status;

    public static AtivoResponseDTO fromEntity(Ativo ativo) {
        AtivoResponseDTO dto = new AtivoResponseDTO();
        dto.setCodigo(ativo.getCodigo());
        dto.setNumeroSerie(ativo.getNumeroSerie());
        dto.setLote(ativo.getLote());
        dto.setDescricao(ativo.getDescricao());
        dto.setStatus(ativo.getStatus());

        if (ativo.getCatalogoAtivo() != null) {
            dto.setCodigoCatalogoAtivo(ativo.getCatalogoAtivo().getCodigo());
            dto.setDescricaoProduto(ativo.getCatalogoAtivo().getDescricaoProduto());
            dto.setModelo(ativo.getCatalogoAtivo().getModelo());
            dto.setMarca(ativo.getCatalogoAtivo().getMarca());
            dto.setTipo(ativo.getCatalogoAtivo().getTipo());
        }

        if (ativo.getFuncionarioResponsavel() != null) {
            dto.setCodigoFuncionarioResponsavel(ativo.getFuncionarioResponsavel().getId());
            dto.setNomeFuncionarioResponsavel(ativo.getFuncionarioResponsavel().getNome());
        }

        return dto;
    }

    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public Integer getCodigoCatalogoAtivo() { return codigoCatalogoAtivo; }
    public void setCodigoCatalogoAtivo(Integer codigoCatalogoAtivo) { this.codigoCatalogoAtivo = codigoCatalogoAtivo; }

    public String getDescricaoProduto() { return descricaoProduto; }
    public void setDescricaoProduto(String descricaoProduto) { this.descricaoProduto = descricaoProduto; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getCodigoFuncionarioResponsavel() { return codigoFuncionarioResponsavel; }
    public void setCodigoFuncionarioResponsavel(Integer codigoFuncionarioResponsavel) { this.codigoFuncionarioResponsavel = codigoFuncionarioResponsavel; }

    public String getNomeFuncionarioResponsavel() { return nomeFuncionarioResponsavel; }
    public void setNomeFuncionarioResponsavel(String nomeFuncionarioResponsavel) { this.nomeFuncionarioResponsavel = nomeFuncionarioResponsavel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
