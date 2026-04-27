package com.example.tracker.dto.maquinachecklistmanutencao;

import com.example.tracker.entity.MaquinaChecklistManutencao;

public class MaquinaChecklistManutencaoResponseDTO {

    private Integer codigo;
    private Integer codigoHistoricoManutencao;
    private Integer codigoTarefa;
    private String descricaoTarefa;
    private String categoriaTarefa;
    private Boolean realizado;
    private String observacaoTarefa;

    public static MaquinaChecklistManutencaoResponseDTO fromEntity(MaquinaChecklistManutencao entity) {
        MaquinaChecklistManutencaoResponseDTO dto = new MaquinaChecklistManutencaoResponseDTO();
        dto.setCodigo(entity.getCodigo());
        dto.setCodigoHistoricoManutencao(
                entity.getHistoricoManutencao() != null ? entity.getHistoricoManutencao().getCodigo() : null);
        dto.setCodigoTarefa(entity.getTarefa() != null ? entity.getTarefa().getId() : null);
        dto.setDescricaoTarefa(entity.getTarefa() != null ? entity.getTarefa().getDescricao() : null);
        dto.setCategoriaTarefa(entity.getTarefa() != null ? entity.getTarefa().getCategoria() : null);
        dto.setRealizado(entity.getRealizado());
        dto.setObservacaoTarefa(entity.getObservacaoTarefa());
        return dto;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigoHistoricoManutencao() {
        return codigoHistoricoManutencao;
    }

    public void setCodigoHistoricoManutencao(Integer codigoHistoricoManutencao) {
        this.codigoHistoricoManutencao = codigoHistoricoManutencao;
    }

    public Integer getCodigoTarefa() {
        return codigoTarefa;
    }

    public void setCodigoTarefa(Integer codigoTarefa) {
        this.codigoTarefa = codigoTarefa;
    }

    public String getDescricaoTarefa() {
        return descricaoTarefa;
    }

    public void setDescricaoTarefa(String descricaoTarefa) {
        this.descricaoTarefa = descricaoTarefa;
    }

    public String getCategoriaTarefa() {
        return categoriaTarefa;
    }

    public void setCategoriaTarefa(String categoriaTarefa) {
        this.categoriaTarefa = categoriaTarefa;
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
