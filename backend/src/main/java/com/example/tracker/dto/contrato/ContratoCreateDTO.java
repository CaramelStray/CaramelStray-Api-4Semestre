package com.example.tracker.dto.contrato;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContratoCreateDTO {

    @NotNull(message = "O codigo do cliente e obrigatorio.")
    private Integer codigoCliente;

    @NotNull(message = "A data de inicio e obrigatoria.")
    private LocalDate dataInicio;

    private LocalDate dataFim;

    @NotBlank(message = "O status do contrato e obrigatorio.")
    private String status;

    private Integer periodoManutencaoPreventiva;
    private Boolean conexaoInternet;
    private LocalDate vencimentoManutencaoPreventiva;
}
