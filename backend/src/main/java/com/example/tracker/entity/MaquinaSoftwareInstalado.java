package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_srv_maquina_software_instalado")
public class MaquinaSoftwareInstalado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @Column(name = "codigo_maquina_contrato", nullable = false)
    private Integer codigoMaquinaContrato;

    @Column(name = "codigo_software", nullable = false)
    private Integer codigoSoftware;

    @Column(name = "chave_licenca", length = 255)
    private String chaveLicenca;

    @Column(name = "versao_instalada", length = 100)
    private String versaoInstalada;

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

    public Integer getCodigoSoftware() {
        return codigoSoftware;
    }

    public void setCodigoSoftware(Integer codigoSoftware) {
        this.codigoSoftware = codigoSoftware;
    }

    public String getChaveLicenca() {
        return chaveLicenca;
    }

    public void setChaveLicenca(String chaveLicenca) {
        this.chaveLicenca = chaveLicenca;
    }

    public String getVersaoInstalada() {
        return versaoInstalada;
    }

    public void setVersaoInstalada(String versaoInstalada) {
        this.versaoInstalada = versaoInstalada;
    }
}
