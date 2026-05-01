package com.example.tracker.dto.CatalogoMaquinaChecklistPadrao;

import jakarta.validation.constraints.NotNull;

public class CatalogoMaquinaChecklistPadraoCreateDTO {

    @NotNull
    private Integer codigoTarefa;

    public Integer getCodigoTarefa() {
        return codigoTarefa;
    }

    public void setCodigoTarefa(Integer codigoTarefa) {
        this.codigoTarefa = codigoTarefa;
    }
}