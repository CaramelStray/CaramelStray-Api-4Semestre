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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "tb_cad_catalogo_software",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_cad_catalogo_software_descricao_versao",
                        columnNames = {"descricao", "versao"})
        })
public class CatalogoSoftware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer id;

    @Column(name = "descricao", nullable = false, length = 255)
    private String nome;

    @Column(name = "versao", nullable = false, length = 100)
    private String versao;

    @Column(name = "tipo", nullable = false, length = 100)
    private String tipo;

    @Column(name = "desenvolvedor_fornecedor", length = 255)
    private String desenvolvedorFornecedor;

    @Column(name = "url_documentacao", length = 500)
    private String urlDocumentacao;

    @Column(name = "descricao_tecnica")
    private String descricaoTecnica;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_cadastro", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @OneToMany(
            mappedBy = "software",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private List<CatalogoSoftwareChecklistPadrao> checklistPadrao = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDesenvolvedorFornecedor() {
        return desenvolvedorFornecedor;
    }

    public void setDesenvolvedorFornecedor(String desenvolvedorFornecedor) {
        this.desenvolvedorFornecedor = desenvolvedorFornecedor;
    }

    public String getUrlDocumentacao() {
        return urlDocumentacao;
    }

    public void setUrlDocumentacao(String urlDocumentacao) {
        this.urlDocumentacao = urlDocumentacao;
    }

    public String getDescricaoTecnica() {
        return descricaoTecnica;
    }

    public void setDescricaoTecnica(String descricaoTecnica) {
        this.descricaoTecnica = descricaoTecnica;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public List<CatalogoSoftwareChecklistPadrao> getChecklistPadrao() {
        return checklistPadrao;
    }

    public void setChecklistPadrao(List<CatalogoSoftwareChecklistPadrao> checklistPadrao) {
        limparChecklistPadrao();
        if (checklistPadrao == null) {
            return;
        }
        checklistPadrao.forEach(this::adicionarChecklistPadraoItem);
    }

    public void adicionarChecklistPadraoItem(CatalogoSoftwareChecklistPadrao item) {
        if (item == null) {
            return;
        }
        item.setSoftware(this);
        this.checklistPadrao.add(item);
    }

    public void limparChecklistPadrao() {
        this.checklistPadrao.forEach(item -> item.setSoftware(null));
        this.checklistPadrao.clear();
    }
}
