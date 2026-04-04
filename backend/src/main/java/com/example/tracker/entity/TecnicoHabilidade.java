package com.example.tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_cad_funcionario_habilidade")
public class TecnicoHabilidade {

    @EmbeddedId
    private TecnicoHabilidadeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tecnicoId")
    @JoinColumn(name = "codigo_funcionario")
    private Tecnico tecnico;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("habilidadeId")
    @JoinColumn(name = "codigo_habilidade")
    private Habilidade habilidade;

    @Column(name = "nivel")
    private Integer nivel;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    public TecnicoHabilidadeId getId() {
        return id;
    }

    public void setId(TecnicoHabilidadeId id) {
        this.id = id;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public Habilidade getHabilidade() {
        return habilidade;
    }

    public void setHabilidade(Habilidade habilidade) {
        this.habilidade = habilidade;
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