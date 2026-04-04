package com.example.tracker.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(
            mappedBy = "catalogoMaquina",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<CatalogoMaquinaChecklistPadrao> checklistPadrao = new ArrayList<>();

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

    public List<CatalogoMaquinaChecklistPadrao> getChecklistPadrao() {
        return checklistPadrao;
    }

    public void setChecklistPadrao(List<CatalogoMaquinaChecklistPadrao> checklistPadrao) {
        limparChecklistPadrao();
        if (checklistPadrao == null) {
            return;
        }
        checklistPadrao.forEach(this::adicionarChecklistPadraoItem);
    }

    public void adicionarChecklistPadraoItem(CatalogoMaquinaChecklistPadrao item) {
        if (item == null) {
            return;
        }
        item.setCatalogoMaquina(this);
        this.checklistPadrao.add(item);
    }

    public void limparChecklistPadrao() {
        this.checklistPadrao.forEach(item -> item.setCatalogoMaquina(null));
        this.checklistPadrao.clear();
    }
}
