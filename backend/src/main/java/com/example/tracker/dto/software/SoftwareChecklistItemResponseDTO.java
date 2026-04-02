package com.example.tracker.dto.software;

import com.example.tracker.entity.CatalogoSoftwareChecklistPadrao;

public class SoftwareChecklistItemResponseDTO {

    private Integer tarefaId;
    private String descricao;
    private String categoria;
    private Boolean obrigatorio;

    public static SoftwareChecklistItemResponseDTO fromEntity(CatalogoSoftwareChecklistPadrao item) {
        SoftwareChecklistItemResponseDTO dto = new SoftwareChecklistItemResponseDTO();
        dto.setTarefaId(item.getTarefa() != null ? item.getTarefa().getId() : null);
        dto.setDescricao(item.getTarefa() != null ? item.getTarefa().getDescricao() : null);
        dto.setCategoria(item.getTarefa() != null ? item.getTarefa().getCategoria() : null);
        dto.setObrigatorio(item.getObrigatorio());
        return dto;
    }

    public Integer getTarefaId() {
        return tarefaId;
    }

    public void setTarefaId(Integer tarefaId) {
        this.tarefaId = tarefaId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }
}
