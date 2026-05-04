package com.example.tracker.dto.maquinachecklistmanutencao;

import jakarta.validation.constraints.NotNull;

public class MaquinaChecklistManutencaoAdicionarDTO {

    @NotNull(message = "Codigo da tarefa e obrigatorio")
    private Integer codigoTarefa;

    public Integer getCodigoTarefa() {
        return codigoTarefa;
    }

    public void setCodigoTarefa(Integer codigoTarefa) {
        this.codigoTarefa = codigoTarefa;
    }
}
