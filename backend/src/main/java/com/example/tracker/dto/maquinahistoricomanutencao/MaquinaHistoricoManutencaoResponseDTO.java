package com.example.tracker.dto.maquinahistoricomanutencao;

import com.example.tracker.entity.MaquinaHistoricoManutencao;
import java.time.LocalDateTime;

public class MaquinaHistoricoManutencaoResponseDTO {

    private Integer codigo;
    private Integer codigoMaquinaContrato;
    private Integer codigoSoftwareInstalado;
    private Integer codigoTipoManutencao;
    private String status;
    private String criticidade;
    private LocalDateTime vencimento;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFimExecucao;
    private String observacaoGeral;

    public static MaquinaHistoricoManutencaoResponseDTO fromEntity(MaquinaHistoricoManutencao entity) {
        MaquinaHistoricoManutencaoResponseDTO dto = new MaquinaHistoricoManutencaoResponseDTO();
        dto.setCodigo(entity.getCodigo());
        dto.setCodigoMaquinaContrato(
                entity.getMaquinaContrato() != null ? entity.getMaquinaContrato().getCodigo() : null);
        dto.setCodigoSoftwareInstalado(
                entity.getSoftwareInstalado() != null ? entity.getSoftwareInstalado().getCodigo() : null);
        dto.setCodigoTipoManutencao(
                entity.getTipoManutencao() != null ? entity.getTipoManutencao().getCodigo() : null);
        dto.setStatus(entity.getStatus());
        dto.setCriticidade(entity.getCriticidade());
        dto.setVencimento(entity.getVencimento());
        dto.setDataAgendamento(entity.getDataAgendamento());
        dto.setDataInicioExecucao(entity.getDataInicioExecucao());
        dto.setDataFimExecucao(entity.getDataFimExecucao());
        dto.setObservacaoGeral(entity.getObservacaoGeral());
        return dto;
    }

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
