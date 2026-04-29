package com.example.tracker.dto.ordemservico;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdemServicoChecklistAtivoCreateDTO {

    @NotNull(message = "O codigo do ativo e obrigatorio.")
    private Integer codigoAtivo;

    private Integer codigoFuncionario;
    private String descricaoAtivo;
    private Boolean levado;
    private Boolean devolvido;
    private String observacao;
}
