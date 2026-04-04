package com.example.tracker.dto.catalogo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaChecklistItemCreateDTO {

    @NotBlank(message = "A descricao da tarefa do checklist e obrigatoria.")
    private String descricao;
}
