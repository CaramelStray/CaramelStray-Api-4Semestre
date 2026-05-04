package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_srv_maquina_historico_manutencao")
public class MaquinaHistoricoManutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_maquina_contrato", nullable = false)
    private MaquinaContrato maquinaContrato;

    @ManyToOne
    @JoinColumn(name = "codigo_software_instalado")
    private MaquinaSoftwareInstalado softwareInstalado;

    @ManyToOne
    @JoinColumn(name = "codigo_tipo_manutencao")
    private TipoManutencao tipoManutencao;

    @ManyToOne
    @JoinColumn(name = "codigo_ordem_servico")
    private OrdemServico ordemServico;

    @OneToMany(mappedBy = "historicoManutencao")
    private List<MaquinaChecklistManutencao> checklistItems = new ArrayList<>();

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "criticidade", length = 50)
    private String criticidade;

    @Column(name = "vencimento")
    private LocalDateTime vencimento;

    @Column(name = "data_agendamento")
    private LocalDateTime dataAgendamento;

    @Column(name = "data_inicio_execucao")
    private LocalDateTime dataInicioExecucao;

    @Column(name = "data_fim_execucao")
    private LocalDateTime dataFimExecucao;

    @Column(name = "observacao_geral")
    private String observacaoGeral;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public MaquinaContrato getMaquinaContrato() {
        return maquinaContrato;
    }

    public void setMaquinaContrato(MaquinaContrato maquinaContrato) {
        this.maquinaContrato = maquinaContrato;
    }

    public MaquinaSoftwareInstalado getSoftwareInstalado() {
        return softwareInstalado;
    }

    public void setSoftwareInstalado(MaquinaSoftwareInstalado softwareInstalado) {
        this.softwareInstalado = softwareInstalado;
    }

    public TipoManutencao getTipoManutencao() {
        return tipoManutencao;
    }

    public void setTipoManutencao(TipoManutencao tipoManutencao) {
        this.tipoManutencao = tipoManutencao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCriticidade() {
        return criticidade;
    }

    public void setCriticidade(String criticidade) {
        this.criticidade = criticidade;
    }

    public LocalDateTime getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDateTime vencimento) {
        this.vencimento = vencimento;
    }

    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalDateTime getDataInicioExecucao() {
        return dataInicioExecucao;
    }

    public void setDataInicioExecucao(LocalDateTime dataInicioExecucao) {
        this.dataInicioExecucao = dataInicioExecucao;
    }

    public LocalDateTime getDataFimExecucao() {
        return dataFimExecucao;
    }

    public void setDataFimExecucao(LocalDateTime dataFimExecucao) {
        this.dataFimExecucao = dataFimExecucao;
    }

    public String getObservacaoGeral() {
        return observacaoGeral;
    }

    public void setObservacaoGeral(String observacaoGeral) {
        this.observacaoGeral = observacaoGeral;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }

    public List<MaquinaChecklistManutencao> getChecklistItems() {
        return checklistItems;
    }

    public void setChecklistItems(List<MaquinaChecklistManutencao> checklistItems) {
        this.checklistItems = checklistItems;
    }
}
