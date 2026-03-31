package com.example.tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_cad_contrato")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @Column(name = "codigo_cliente", nullable = false)
    private Integer codigoCliente;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "periodo_manutencao_preventiva")
    private Integer periodoManutencaoPreventiva;

    @Column(name = "conexao_internet")
    private Boolean conexaoInternet;

    @Column(name = "vencimento_manutencao_preventiva")
    private LocalDate vencimentoManutencaoPreventiva;

    public Contrato() {
    }

    public Contrato(Integer codigoCliente, LocalDate dataInicio, LocalDate dataFim,
            String status, Integer periodoManutencaoPreventiva, Boolean conexaoInternet,
            LocalDate vencimentoManutencaoPreventiva) {
        this.codigoCliente = codigoCliente;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.periodoManutencaoPreventiva = periodoManutencaoPreventiva;
        this.conexaoInternet = conexaoInternet;
        this.vencimentoManutencaoPreventiva = vencimentoManutencaoPreventiva;
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
}