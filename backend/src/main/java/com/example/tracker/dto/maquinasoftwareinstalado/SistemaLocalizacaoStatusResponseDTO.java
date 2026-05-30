package com.example.tracker.dto.maquinasoftwareinstalado;

import java.math.BigDecimal;

public class SistemaLocalizacaoStatusResponseDTO {

    private Integer codigoSistemaInstalado;
    private Integer codigoSoftware;
    private String nome;
    private String tipo;
    private String statusOperacional;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer codigoMaquinaContrato;
    private String numeroSerieMaquina;
    private Boolean manutencaoPendente;
    private Boolean manutencaoAtrasada;

    public Integer getCodigoSistemaInstalado() {
        return codigoSistemaInstalado;
    }

    public void setCodigoSistemaInstalado(Integer codigoSistemaInstalado) {
        this.codigoSistemaInstalado = codigoSistemaInstalado;
    }

    public Integer getCodigoSoftware() {
        return codigoSoftware;
    }

    public void setCodigoSoftware(Integer codigoSoftware) {
        this.codigoSoftware = codigoSoftware;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatusOperacional() {
        return statusOperacional;
    }

    public void setStatusOperacional(String statusOperacional) {
        this.statusOperacional = statusOperacional;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Integer getCodigoMaquinaContrato() {
        return codigoMaquinaContrato;
    }

    public void setCodigoMaquinaContrato(Integer codigoMaquinaContrato) {
        this.codigoMaquinaContrato = codigoMaquinaContrato;
    }

    public String getNumeroSerieMaquina() {
        return numeroSerieMaquina;
    }

    public void setNumeroSerieMaquina(String numeroSerieMaquina) {
        this.numeroSerieMaquina = numeroSerieMaquina;
    }

    public Boolean getManutencaoPendente() {
        return manutencaoPendente;
    }

    public void setManutencaoPendente(Boolean manutencaoPendente) {
        this.manutencaoPendente = manutencaoPendente;
    }

    public Boolean getManutencaoAtrasada() {
        return manutencaoAtrasada;
    }

    public void setManutencaoAtrasada(Boolean manutencaoAtrasada) {
        this.manutencaoAtrasada = manutencaoAtrasada;
    }
}
