package com.example.tracker.dto.software;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoftwareChecklistItemCreateDTO {

    @NotBlank(message = "A descricao da tarefa do checklist e obrigatoria.")
    private String descricao;

    private Boolean obrigatorio;
}
