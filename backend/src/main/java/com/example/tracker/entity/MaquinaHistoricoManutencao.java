package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_srv_maquina_historico_manutencao")
public class MaquinaHistoricoManutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @Column(name = "codigo_maquina_contrato", nullable = false)
    private Integer codigoMaquinaContrato;

    @Column(name = "codigo_software_instalado")
    private Integer codigoSoftwareInstalado;

    @Column(name = "codigo_tipo_manutencao", nullable = false)
    private Integer codigoTipoManutencao;

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

    public Integer getCodigoMaquinaContrato() {
        return codigoMaquinaContrato;
    }

    public void setCodigoMaquinaContrato(Integer codigoMaquinaContrato) {
        this.codigoMaquinaContrato = codigoMaquinaContrato;
    }

    public Integer getCodigoSoftwareInstalado() {
        return codigoSoftwareInstalado;
    }

    public void setCodigoSoftwareInstalado(Integer codigoSoftwareInstalado) {
        this.codigoSoftwareInstalado = codigoSoftwareInstalado;
    }

    public Integer getCodigoTipoManutencao() {
        return codigoTipoManutencao;
    }

    public void setCodigoTipoManutencao(Integer codigoTipoManutencao) {
        this.codigoTipoManutencao = codigoTipoManutencao;
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
}
