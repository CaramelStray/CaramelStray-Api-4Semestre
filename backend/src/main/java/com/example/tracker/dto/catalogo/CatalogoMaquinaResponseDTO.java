package com.example.tracker.dto.catalogo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CatalogoMaquinaResponseDTO {

    private Integer codigo;
    private String descricao;
    private String especificacao;
    private String limiteManutencao;
}