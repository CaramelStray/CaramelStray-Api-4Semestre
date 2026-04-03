package com.example.tracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_cad_catalogo_maquina")
public class CatalogoMaquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "especificacao", columnDefinition = "TEXT")
    private String especificacao;

    @Column(name = "limite_manutencao")
    private String limiteManutencao;

    public CatalogoMaquina() {
    }

    public CatalogoMaquina(String descricao, String especificacao, String limiteManutencao) {
        this.descricao = descricao;
        this.especificacao = especificacao;
        this.limiteManutencao = limiteManutencao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEspecificacao() {
        return especificacao;
    }

    public void setEspecificacao(String especificacao) {
        this.especificacao = especificacao;
    }

    public String getLimiteManutencao() {
        return limiteManutencao;
    }

    public void setLimiteManutencao(String limiteManutencao) {
        this.limiteManutencao = limiteManutencao;
    }
}