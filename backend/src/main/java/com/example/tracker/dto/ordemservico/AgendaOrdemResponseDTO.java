package com.example.tracker.dto.ordemservico;

import com.example.tracker.entity.OrdemServico;
import java.time.LocalDateTime;

public class AgendaOrdemResponseDTO {

    private Integer codigo;
    private String nomeCliente;
    private String status;
    private String criticidade;
    private String tipoOrdem;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFimExecucao;
    private Boolean possuiConflito;

    public static AgendaOrdemResponseDTO fromEntity(OrdemServico os) {
        AgendaOrdemResponseDTO dto = new AgendaOrdemResponseDTO();
        dto.setCodigo(os.getCodigo());
        dto.setNomeCliente(os.getCliente() != null ? os.getCliente().getNomeEmpresa() : null);
        dto.setStatus(os.getStatus());
        dto.setCriticidade(os.getCriticidade());
        dto.setTipoOrdem(os.getTipoOrdem());
        dto.setDataAgendamento(os.getDataAgendamento());
        dto.setDataInicioExecucao(os.getDataInicioExecucao());
        dto.setDataFimExecucao(os.getDataFimExecucao());
        dto.setPossuiConflito(false);
        return dto;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
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

    public String getTipoOrdem() {
        return tipoOrdem;
    }

    public void setTipoOrdem(String tipoOrdem) {
        this.tipoOrdem = tipoOrdem;
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

    public Boolean getPossuiConflito() {
        return possuiConflito;
    }

    public void setPossuiConflito(Boolean possuiConflito) {
        this.possuiConflito = possuiConflito;
    }
}
