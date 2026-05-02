package com.example.tracker.dto.catalogoativo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogoAtivoCreateDTO {

    @NotBlank(message = "A descrição do produto é obrigatória.")
    private String descricaoProduto;

    private String modelo;

    private String marca;

    private String descricao;

    private String especificacao;

    @NotBlank(message = "O tipo do ativo é obrigatório.")
    private String tipo;
}
