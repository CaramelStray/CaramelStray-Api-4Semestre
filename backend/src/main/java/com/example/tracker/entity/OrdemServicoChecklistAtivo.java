package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tb_srv_ordem_servico_checklist_ativo",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ordem_servico_checklist_ativo",
                columnNames = {"codigo_ordem_servico", "codigo_ativo"}))
public class OrdemServicoChecklistAtivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_ordem_servico", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne
    @JoinColumn(name = "codigo_funcionario")
    private Tecnico funcionario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_ativo", nullable = false)
    private Ativo ativo;

    @Column(name = "descricao_ativo")
    private String descricaoAtivo;

    @Column(name = "levado", nullable = false)
    private Boolean levado = false;

    @Column(name = "devolvido", nullable = false)
    private Boolean devolvido = false;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "status_intervencao", length = 50)
    private String statusIntervencao;

    @Column(name = "data_intervencao")
    private LocalDateTime dataIntervencao;

    @Column(name = "observacao_intervencao")
    private String observacaoIntervencao;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }

    public Tecnico getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Tecnico funcionario) {
        this.funcionario = funcionario;
    }

    public Ativo getAtivo() {
        return ativo;
    }

    public void setAtivo(Ativo ativo) {
        this.ativo = ativo;
    }

    public String getDescricaoAtivo() {
        return descricaoAtivo;
    }

    public void setDescricaoAtivo(String descricaoAtivo) {
        this.descricaoAtivo = descricaoAtivo;
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

    public String getStatusIntervencao() {
        return statusIntervencao;
    }

    public void setStatusIntervencao(String statusIntervencao) {
        this.statusIntervencao = statusIntervencao;
    }

    public LocalDateTime getDataIntervencao() {
        return dataIntervencao;
    }

    public void setDataIntervencao(LocalDateTime dataIntervencao) {
        this.dataIntervencao = dataIntervencao;
    }

    public String getObservacaoIntervencao() {
        return observacaoIntervencao;
    }

    public void setObservacaoIntervencao(String observacaoIntervencao) {
        this.observacaoIntervencao = observacaoIntervencao;
    }
}
