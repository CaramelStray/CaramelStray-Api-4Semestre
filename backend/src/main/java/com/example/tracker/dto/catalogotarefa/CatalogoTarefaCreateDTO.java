package com.example.tracker.dto.catalogotarefa;

import jakarta.validation.constraints.NotBlank;

public class CatalogoTarefaCreateDTO {

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    private String categoria;

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
