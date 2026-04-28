package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_srv_maquina_checklist_manutencao")
public class MaquinaChecklistManutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_historico_manutencao", nullable = false)
    private MaquinaHistoricoManutencao historicoManutencao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_tarefa", nullable = false)
    private CatalogoTarefa tarefa;

    @Column(name = "realizado")
    private Boolean realizado;

    @Column(name = "observacao_tarefa")
    private String observacaoTarefa;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public MaquinaHistoricoManutencao getHistoricoManutencao() {
        return historicoManutencao;
    }

    public void setHistoricoManutencao(MaquinaHistoricoManutencao historicoManutencao) {
        this.historicoManutencao = historicoManutencao;
    }

    public CatalogoTarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(CatalogoTarefa tarefa) {
        this.tarefa = tarefa;
    }

    public Boolean getRealizado() {
        return realizado;
    }

    public void setRealizado(Boolean realizado) {
        this.realizado = realizado;
    }

    public String getObservacaoTarefa() {
        return observacaoTarefa;
    }

    public void setObservacaoTarefa(String observacaoTarefa) {
        this.observacaoTarefa = observacaoTarefa;
    }
}
