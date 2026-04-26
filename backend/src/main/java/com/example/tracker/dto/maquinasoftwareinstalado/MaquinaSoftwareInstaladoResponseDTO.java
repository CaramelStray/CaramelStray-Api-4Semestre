package com.example.tracker.dto.maquinasoftwareinstalado;

import com.example.tracker.entity.MaquinaSoftwareInstalado;

public class MaquinaSoftwareInstaladoResponseDTO {

    private Integer codigo;
    private Integer codigoMaquinaContrato;
    private Integer codigoSoftware;
    private String descricaoSoftware;
    private String chaveLicenca;
    private String versaoInstalada;

    public static MaquinaSoftwareInstaladoResponseDTO fromEntity(MaquinaSoftwareInstalado entity) {
        MaquinaSoftwareInstaladoResponseDTO dto = new MaquinaSoftwareInstaladoResponseDTO();

        dto.setCodigo(entity.getCodigo());

        if (entity.getMaquinaContrato() != null) {
            dto.setCodigoMaquinaContrato(entity.getMaquinaContrato().getCodigo());
        }

        if (entity.getSoftware() != null) {
            dto.setCodigoSoftware(entity.getSoftware().getId());
            dto.setDescricaoSoftware(entity.getSoftware().getNome());
        }

        dto.setChaveLicenca(entity.getChaveLicenca());
        dto.setVersaoInstalada(entity.getVersaoInstalada());

        return dto;
    }

    // getters e setters

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

    public String getDescricaoSoftware() {
        return descricaoSoftware;
    }

    public void setDescricaoSoftware(String descricaoSoftware) {
        this.descricaoSoftware = descricaoSoftware;
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