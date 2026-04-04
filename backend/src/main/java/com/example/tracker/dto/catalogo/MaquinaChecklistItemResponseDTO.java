package com.example.tracker.dto.catalogo;

import com.example.tracker.entity.CatalogoMaquinaChecklistPadrao;

public class MaquinaChecklistItemResponseDTO {

    private Integer tarefaId;
    private String descricao;
    private String categoria;

    public static MaquinaChecklistItemResponseDTO fromEntity(CatalogoMaquinaChecklistPadrao item) {
        MaquinaChecklistItemResponseDTO dto = new MaquinaChecklistItemResponseDTO();
        dto.setTarefaId(item.getTarefa() != null ? item.getTarefa().getId() : null);
        dto.setDescricao(item.getTarefa() != null ? item.getTarefa().getDescricao() : null);
        dto.setCategoria(item.getTarefa() != null ? item.getTarefa().getCategoria() : null);
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
}
