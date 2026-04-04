package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TecnicoHabilidadeId implements Serializable {

    @Column(name = "codigo_funcionario")
    private Integer tecnicoId;

    @Column(name = "codigo_habilidade")
    private Integer habilidadeId;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TecnicoHabilidadeId)) return false;
        TecnicoHabilidadeId that = (TecnicoHabilidadeId) o;
        return Objects.equals(tecnicoId, that.tecnicoId) &&
               Objects.equals(habilidadeId, that.habilidadeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tecnicoId, habilidadeId);
    }
}