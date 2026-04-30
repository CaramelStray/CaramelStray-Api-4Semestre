package com.example.tracker.dto.CatalogoMaquinaChecklistPadrao;

import com.example.tracker.entity.CatalogoMaquinaChecklistPadrao;

public class CatalogoMaquinaChecklistPadraoResponseDTO {

    private Integer codigoCatalogoMaquina;
    private Integer codigoTarefa;
    private String descricaoTarefa;
    private String categoria;

    public static CatalogoMaquinaChecklistPadraoResponseDTO fromEntity(
            CatalogoMaquinaChecklistPadrao entity) {

        CatalogoMaquinaChecklistPadraoResponseDTO dto =
                new CatalogoMaquinaChecklistPadraoResponseDTO();

        if (entity.getCatalogoMaquina() != null) {
            dto.setCodigoCatalogoMaquina(entity.getCatalogoMaquina().getCodigo());
        }

        if (entity.getTarefa() != null) {
            dto.setCodigoTarefa(entity.getTarefa().getId());
            dto.setDescricaoTarefa(entity.getTarefa().getDescricao());
            dto.setCategoria(entity.getTarefa().getCategoria());
        }

        return dto;
    }

    public Integer getCodigoCatalogoMaquina() {
        return codigoCatalogoMaquina;
    }

    public void setCodigoCatalogoMaquina(Integer codigoCatalogoMaquina) {
        this.codigoCatalogoMaquina = codigoCatalogoMaquina;
    }

    public Integer getCodigoTarefa() {
        return codigoTarefa;
    }

    public void setCodigoTarefa(Integer codigoTarefa) {
        this.codigoTarefa = codigoTarefa;
    }

    public String getDescricaoTarefa() {
        return descricaoTarefa;
    }

    public void setDescricaoTarefa(String descricaoTarefa) {
        this.descricaoTarefa = descricaoTarefa;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    

}