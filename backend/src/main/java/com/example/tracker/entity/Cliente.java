package com.example.tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_cad_cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer id;

    @Column(name = "descricao_empresa", nullable = false)
    private String nomeEmpresa;

    @Column(name = "documento", length = 30)
    private String documento;

    @Column(name = "email_contato")
    private String emailContato;

    @Column(name = "telefone_contato", length = 30)
    private String telefoneContato;
    
    @Column(name = "descricao_responsavel")
    private String nomeResponsavel;

    @Column(name = "pais", length = 100)
    private String pais;

    @Column(name = "estado_regiao", length = 100)
    private String estadoRegiao;

    @Column(name = "cidade", length = 100)
    private String cidade;

    @Column(name = "classificacao_distancia", length = 100)
    private String classificacaoDistancia;

    @Column(name = "fuso_horario", length = 100)
    private String fusoHorario;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "rua", length = 255)
    private String rua;

    @Column(name = "numero", length = 50)
    private String numero;

    @Column(name = "internacional")
    private Boolean internacional;

    @Column(name = "data_cadastro", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataCadastro;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmailContato() {
        return emailContato;
    }

    public void setEmailContato(String emailContato) {
        this.emailContato = emailContato;
    }

    public String getTelefoneContato() {
        return telefoneContato;
    }

    public void setTelefoneContato(String telefoneContato) {
        this.telefoneContato = telefoneContato;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstadoRegiao() {
        return estadoRegiao;
    }

    public void setEstadoRegiao(String estadoRegiao) {
        this.estadoRegiao = estadoRegiao;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getClassificacaoDistancia() {
        return classificacaoDistancia;
    }

    public void setClassificacaoDistancia(String classificacaoDistancia) {
        this.classificacaoDistancia = classificacaoDistancia;
    }

    public String getFusoHorario() {
        return fusoHorario;
    }

    public void setFusoHorario(String fusoHorario) {
        this.fusoHorario = fusoHorario;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Boolean getInternacional() {
        return internacional;
    }

    public void setInternacional(Boolean internacional) {
        this.internacional = internacional;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
