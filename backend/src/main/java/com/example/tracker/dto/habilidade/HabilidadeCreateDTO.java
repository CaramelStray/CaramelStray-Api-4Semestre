package com.example.tracker.dto.habilidade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HabilidadeCreateDTO {

    @NotBlank(message = "A descricao da habilidade é obrigatória.")
    @Size(max = 100, message = "A descricao deve ter no máximo 100 caracteres.")
    private String descricao;

    @Size(max = 255, message = "A observacao deve ter no máximo 255 caracteres.")
    private String observacao;
}