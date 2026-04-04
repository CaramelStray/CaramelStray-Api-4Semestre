package com.example.tracker.dto.TecnicoHabilidade;

import java.time.LocalDate;

public class TecnicoHabilidadeCreateDTO {

    private Integer tecnicoId;
    private Integer habilidadeId;
    private Integer nivel;
    private LocalDate dataValidade;

    public Integer getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Integer tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public Integer getHabilidadeId() {
        return habilidadeId;
    }

    public void setHabilidadeId(Integer habilidadeId) {
        this.habilidadeId = habilidadeId;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }
}