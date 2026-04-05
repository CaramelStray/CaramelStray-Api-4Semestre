package com.example.tracker.dto.ordemservico;

import com.example.tracker.entity.OrdemServico;
import java.time.LocalDateTime;

public class OrdemServicoResponseDTO {

    private Integer codigo;
    private Integer codigoCliente;
    private Integer codigoFuncionario;
    private Integer codigoSoftwareInstalado;
    private Integer codigoContrato;
    private Integer codigoMaquinaContrato;
    private String status;
    private String criticidade;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFimExecucao;
    private String observacaoGeral;

    public static OrdemServicoResponseDTO fromEntity(OrdemServico os) {
        OrdemServicoResponseDTO dto = new OrdemServicoResponseDTO();

        dto.setCodigo(os.getCodigo());

        if (os.getCliente() != null) {
            dto.setCodigoCliente(os.getCliente().getId());
        }

        if (os.getFuncionario() != null) {
            dto.setCodigoFuncionario(os.getFuncionario().getId());
        }

        if (os.getSoftwareInstalado() != null) {
            dto.setCodigoSoftwareInstalado(os.getSoftwareInstalado().getCodigo());
        }

        if (os.getContrato() != null) {
            dto.setCodigoContrato(os.getContrato().getCodigo());
        }

        if (os.getMaquinaContrato() != null) {
            dto.setCodigoMaquinaContrato(os.getMaquinaContrato().getCodigo());
        }

        dto.setStatus(os.getStatus());
        dto.setCriticidade(os.getCriticidade());
        dto.setDataAbertura(os.getDataAbertura());
        dto.setDataAgendamento(os.getDataAgendamento());
        dto.setDataInicioExecucao(os.getDataInicioExecucao());
        dto.setDataFimExecucao(os.getDataFimExecucao());
        dto.setObservacaoGeral(os.getObservacaoGeral());

        return dto;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(Integer codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Integer getCodigoFuncionario() {
        return codigoFuncionario;
    }

    public void setCodigoFuncionario(Integer codigoFuncionario) {
        this.codigoFuncionario = codigoFuncionario;
    }

    public Integer getCodigoSoftwareInstalado() {
        return codigoSoftwareInstalado;
    }

    public void setCodigoSoftwareInstalado(Integer codigoSoftwareInstalado) {
        this.codigoSoftwareInstalado = codigoSoftwareInstalado;
    }

    public Integer getCodigoContrato() {
        return codigoContrato;
    }

    public void setCodigoContrato(Integer codigoContrato) {
        this.codigoContrato = codigoContrato;
    }

    public Integer getCodigoMaquinaContrato() {
        return codigoMaquinaContrato;
    }

    public void setCodigoMaquinaContrato(Integer codigoMaquinaContrato) {
        this.codigoMaquinaContrato = codigoMaquinaContrato;
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

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
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