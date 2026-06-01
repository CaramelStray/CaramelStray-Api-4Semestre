package com.example.tracker.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_srv_ordem_servico_funcionario")
public class OrdemServicoTecnico {

    @EmbeddedId
    private OrdemServicoTecnicoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codigoOrdemServico")
    @JoinColumn(name = "codigo_ordem_servico")
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codigoFuncionario")
    @JoinColumn(name = "codigo_funcionario")
    private Tecnico tecnico;

    public OrdemServicoTecnico() {
    }

    public OrdemServicoTecnico(OrdemServico ordemServico, Tecnico tecnico) {
        this.ordemServico = ordemServico;
        this.tecnico = tecnico;
        this.id = new OrdemServicoTecnicoId(
                ordemServico == null ? null : ordemServico.getCodigo(),
                tecnico == null ? null : tecnico.getId());
    }

    public OrdemServicoTecnicoId getId() {
        return id;
    }

    public void setId(OrdemServicoTecnicoId id) {
        this.id = id;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }
}
