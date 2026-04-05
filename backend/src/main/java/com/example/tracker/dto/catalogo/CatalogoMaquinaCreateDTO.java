package com.example.tracker.dto.catalogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogoMaquinaCreateDTO {

    @NotBlank(message = "A descricao é obrigatória.")
    private String descricao;

    private String especificacao;

    private String limiteManutencao;

    @Valid
    private List<MaquinaChecklistItemCreateDTO> checklistPadrao;
}
