package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_srv_maquina_contrato")
public class MaquinaContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_contrato", nullable = false)
    private Contrato contrato;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_catalogo_maquina", nullable = false)
    private CatalogoMaquina catalogoMaquina;

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

    @OneToMany(mappedBy = "maquinaContrato", fetch = FetchType.LAZY)
    private List<MaquinaSoftwareInstalado> softwaresInstalados = new ArrayList<>();

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public CatalogoMaquina getCatalogoMaquina() {
        return catalogoMaquina;
    }

    public void setCatalogoMaquina(CatalogoMaquina catalogoMaquina) {
        this.catalogoMaquina = catalogoMaquina;
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

    public List<MaquinaSoftwareInstalado> getSoftwaresInstalados() {
        return softwaresInstalados;
    }

    public void setSoftwaresInstalados(List<MaquinaSoftwareInstalado> softwaresInstalados) {
        this.softwaresInstalados = softwaresInstalados;
    }
}
