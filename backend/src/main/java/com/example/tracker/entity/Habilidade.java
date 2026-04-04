package com.example.tracker.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_cad_habilidade")
public class Habilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @Column(nullable = false, unique = true, length = 100)
    private String descricao;

    @Column(length = 255)
    private String observacao;

    @OneToMany(mappedBy = "habilidade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TecnicoHabilidade> tecnicos;

    public Habilidade() {
    }

    public Habilidade(String descricao, String observacao) {
        this.descricao = descricao;
        this.observacao = observacao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<TecnicoHabilidade> getTecnicos() {
        return tecnicos;
    }

    public void setTecnicos(List<TecnicoHabilidade> tecnicos) {
        this.tecnicos = tecnicos;
    }
}