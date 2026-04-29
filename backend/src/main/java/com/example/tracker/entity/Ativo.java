package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_cad_ativo")
public class Ativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_catalogo_ativo", nullable = false)
    private CatalogoAtivo catalogoAtivo;

    @Column(name = "numero_serie")
    private String numeroSerie;

    @Column(name = "lote")
    private String lote;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "codigo_funcionario_responsavel")
    private Tecnico funcionarioResponsavel;

    @Column(name = "status", length = 100)
    private String status;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public CatalogoAtivo getCatalogoAtivo() {
        return catalogoAtivo;
    }

    public void setCatalogoAtivo(CatalogoAtivo catalogoAtivo) {
        this.catalogoAtivo = catalogoAtivo;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Tecnico getFuncionarioResponsavel() {
        return funcionarioResponsavel;
    }

    public void setFuncionarioResponsavel(Tecnico funcionarioResponsavel) {
        this.funcionarioResponsavel = funcionarioResponsavel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
