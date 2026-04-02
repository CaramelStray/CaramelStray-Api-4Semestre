package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_cad_catalogo_software_checklist_padrao")
public class CatalogoSoftwareChecklistPadrao {

    @EmbeddedId
    private CatalogoSoftwareChecklistPadraoId id = new CatalogoSoftwareChecklistPadraoId();

    @MapsId("codigoCatalogoSoftware")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_catalogo_software", nullable = false)
    private CatalogoSoftware software;

    @MapsId("codigoCatalogoTarefa")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codigo_catalogo_tarefa", nullable = false)
    private CatalogoTarefa tarefa;

    @Column(nullable = false)
    private Boolean obrigatorio = false;

    public CatalogoSoftwareChecklistPadraoId getId() {
        return id;
    }

    public void setId(CatalogoSoftwareChecklistPadraoId id) {
        this.id = id == null ? new CatalogoSoftwareChecklistPadraoId() : id;
    }

    public CatalogoSoftware getSoftware() {
        return software;
    }

    public void setSoftware(CatalogoSoftware software) {
        this.software = software;
        if (software != null && software.getId() != null) {
            garantirId().setCodigoCatalogoSoftware(software.getId());
        }
    }

    public CatalogoTarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(CatalogoTarefa tarefa) {
        this.tarefa = tarefa;
        if (tarefa != null && tarefa.getId() != null) {
            garantirId().setCodigoCatalogoTarefa(tarefa.getId());
        }
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    private CatalogoSoftwareChecklistPadraoId garantirId() {
        if (this.id == null) {
            this.id = new CatalogoSoftwareChecklistPadraoId();
        }
        return this.id;
    }
}
