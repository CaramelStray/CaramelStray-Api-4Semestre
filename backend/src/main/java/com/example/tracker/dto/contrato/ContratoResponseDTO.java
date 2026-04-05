package com.example.tracker.dto.contrato;

import com.example.tracker.entity.Contrato;
import java.time.LocalDate;

public class ContratoResponseDTO {

    private Integer codigo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String status;
    private Integer periodoManutencaoPreventiva;
    private Boolean conexaoInternet;
    private LocalDate vencimentoManutencaoPreventiva;
    private String descricao;

    public static ContratoResponseDTO fromEntity(Contrato contrato) {
        ContratoResponseDTO dto = new ContratoResponseDTO();
        dto.setCodigo(contrato.getCodigo());
        dto.setDataInicio(contrato.getDataInicio());
        dto.setDataFim(contrato.getDataFim());
        dto.setStatus(contrato.getStatus());
        dto.setPeriodoManutencaoPreventiva(contrato.getPeriodoManutencaoPreventiva());
        dto.setConexaoInternet(contrato.getConexaoInternet());
        dto.setVencimentoManutencaoPreventiva(contrato.getVencimentoManutencaoPreventiva());
        dto.setDescricao(contrato.getDescricao());
        return dto;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPeriodoManutencaoPreventiva() {
        return periodoManutencaoPreventiva;
    }

    public void setPeriodoManutencaoPreventiva(Integer periodoManutencaoPreventiva) {
        this.periodoManutencaoPreventiva = periodoManutencaoPreventiva;
    }

    public Boolean getConexaoInternet() {
        return conexaoInternet;
    }

    public void setConexaoInternet(Boolean conexaoInternet) {
        this.conexaoInternet = conexaoInternet;
    }

    public LocalDate getVencimentoManutencaoPreventiva() {
        return vencimentoManutencaoPreventiva;
    }

    public void setVencimentoManutencaoPreventiva(LocalDate vencimentoManutencaoPreventiva) {
        this.vencimentoManutencaoPreventiva = vencimentoManutencaoPreventiva;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}