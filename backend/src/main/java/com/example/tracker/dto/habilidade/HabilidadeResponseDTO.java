package com.example.tracker.dto.habilidade;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HabilidadeResponseDTO {

    private Integer codigo;
    private String descricao;
    private String observacao;
}