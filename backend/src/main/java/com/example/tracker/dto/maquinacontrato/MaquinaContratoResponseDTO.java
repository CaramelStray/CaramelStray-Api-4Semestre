package com.example.tracker.dto.maquinacontrato;

import com.example.tracker.entity.MaquinaContrato;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MaquinaContratoResponseDTO {

    private Integer codigo;
    private Integer codigoContrato;
    private Integer codigoCatalogoMaquina;
    private String numeroSerie;
    private LocalDate dataInstalacao;
    private String manutencaoFeita;
    private Boolean trabalhoEmAltura;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public static MaquinaContratoResponseDTO fromEntity(MaquinaContrato maquinaContrato) {
        MaquinaContratoResponseDTO dto = new MaquinaContratoResponseDTO();
        dto.setCodigo(maquinaContrato.getCodigo());
        if (maquinaContrato.getContrato() != null) {
            dto.setCodigoContrato(maquinaContrato.getContrato().getCodigo());
        }
        if (maquinaContrato.getCatalogoMaquina() != null) {
            dto.setCodigoCatalogoMaquina(maquinaContrato.getCatalogoMaquina().getCodigo());
        }
        dto.setNumeroSerie(maquinaContrato.getNumeroSerie());
        dto.setDataInstalacao(maquinaContrato.getDataInstalacao());
        dto.setManutencaoFeita(maquinaContrato.getManutencaoFeita());
        dto.setTrabalhoEmAltura(maquinaContrato.getTrabalhoEmAltura());
        dto.setLatitude(maquinaContrato.getLatitude());
        dto.setLongitude(maquinaContrato.getLongitude());
        return dto;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigoContrato() {
        return codigoContrato;
    }

    public void setCodigoContrato(Integer codigoContrato) {
        this.codigoContrato = codigoContrato;
    }

    public Integer getCodigoCatalogoMaquina() {
        return codigoCatalogoMaquina;
    }

    public void setCodigoCatalogoMaquina(Integer codigoCatalogoMaquina) {
        this.codigoCatalogoMaquina = codigoCatalogoMaquina;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public LocalDate getDataInstalacao() {
        return dataInstalacao;
    }

    public void setDataInstalacao(LocalDate dataInstalacao) {
        this.dataInstalacao = dataInstalacao;
    }

    public String getManutencaoFeita() {
        return manutencaoFeita;
    }

    public void setManutencaoFeita(String manutencaoFeita) {
        this.manutencaoFeita = manutencaoFeita;
    }

    public Boolean getTrabalhoEmAltura() {
        return trabalhoEmAltura;
    }

    public void setTrabalhoEmAltura(Boolean trabalhoEmAltura) {
        this.trabalhoEmAltura = trabalhoEmAltura;
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
}
