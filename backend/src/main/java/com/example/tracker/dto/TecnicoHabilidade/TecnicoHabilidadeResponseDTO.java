package com.example.tracker.dto.TecnicoHabilidade;

import java.time.LocalDate;

public class TecnicoHabilidadeResponseDTO {

    private Integer habilidadeId;
    private String descricaoHabilidade;
    private Integer nivel;
    private LocalDate dataValidade;

    public Integer getHabilidadeId() {
        return habilidadeId;
    }

    public void setHabilidadeId(Integer habilidadeId) {
        this.habilidadeId = habilidadeId;
    }

    public String getDescricaoHabilidade() {
        return descricaoHabilidade;
    }

    public void setDescricaoHabilidade(String descricaoHabilidade) {
        this.descricaoHabilidade = descricaoHabilidade;
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