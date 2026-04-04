package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_srv_maquina_contrato")
public class MaquinaContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @Column(name = "codigo_contrato", nullable = false)
    private Integer codigoContrato;

    @Column(name = "codigo_catalogo_maquina", nullable = false)
    private Integer codigoCatalogoMaquina;

    @Column(name = "numero_serie")
    private String numeroSerie;

    @Column(name = "data_instalacao")
    private LocalDate dataInstalacao;

    @Column(name = "manutencao_feita")
    private String manutencaoFeita;

    @Column(name = "trabalho_em_altura", nullable = false)
    private Boolean trabalhoEmAltura = false;

    @Column(name = "latitude", precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6)
    private BigDecimal longitude;

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
