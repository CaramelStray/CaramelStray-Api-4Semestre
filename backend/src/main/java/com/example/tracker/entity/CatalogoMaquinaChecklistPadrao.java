package com.example.tracker.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_cad_catalogo_maquina_checklist_padrao")
public class CatalogoMaquinaChecklistPadrao {

    @EmbeddedId
    private CatalogoMaquinaChecklistPadraoId id = new CatalogoMaquinaChecklistPadraoId();

    @MapsId("codigoCatalogoMaquina")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_catalogo_maquina", nullable = false)
    private CatalogoMaquina catalogoMaquina;

    @MapsId("codigoTarefa")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codigo_tarefa", nullable = false)
    private CatalogoTarefa tarefa;

    public CatalogoMaquinaChecklistPadraoId getId() {
        return id;
    }

    public void setId(CatalogoMaquinaChecklistPadraoId id) {
        this.id = id == null ? new CatalogoMaquinaChecklistPadraoId() : id;
    }

    public CatalogoMaquina getCatalogoMaquina() {
        return catalogoMaquina;
    }

    public void setCatalogoMaquina(CatalogoMaquina catalogoMaquina) {
        this.catalogoMaquina = catalogoMaquina;
        if (catalogoMaquina != null && catalogoMaquina.getCodigo() != null) {
            garantirId().setCodigoCatalogoMaquina(catalogoMaquina.getCodigo());
        }
    }

    public CatalogoTarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(CatalogoTarefa tarefa) {
        this.tarefa = tarefa;
        if (tarefa != null && tarefa.getId() != null) {
            garantirId().setCodigoTarefa(tarefa.getId());
        }
    }

    private CatalogoMaquinaChecklistPadraoId garantirId() {
        if (this.id == null) {
            this.id = new CatalogoMaquinaChecklistPadraoId();
        }
        return this.id;
    }
}
