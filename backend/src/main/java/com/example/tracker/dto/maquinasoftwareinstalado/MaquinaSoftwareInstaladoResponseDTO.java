package com.example.tracker.dto.maquinasoftwareinstalado;

import com.example.tracker.entity.MaquinaSoftwareInstalado;

public class MaquinaSoftwareInstaladoResponseDTO {

    private Integer codigo;
    private Integer codigoMaquinaContrato;
    private Integer codigoSoftware;
    private String chaveLicenca;
    private String versaoInstalada;

    public static MaquinaSoftwareInstaladoResponseDTO fromEntity(MaquinaSoftwareInstalado entity) {
        MaquinaSoftwareInstaladoResponseDTO dto = new MaquinaSoftwareInstaladoResponseDTO();
        dto.setCodigo(entity.getCodigo());
        dto.setCodigoMaquinaContrato(entity.getCodigoMaquinaContrato());
        dto.setCodigoSoftware(entity.getCodigoSoftware());
        dto.setChaveLicenca(entity.getChaveLicenca());
        dto.setVersaoInstalada(entity.getVersaoInstalada());
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
