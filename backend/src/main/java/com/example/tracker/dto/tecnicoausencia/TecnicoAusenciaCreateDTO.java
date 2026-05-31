package com.example.tracker.dto.tecnicoausencia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TecnicoAusenciaCreateDTO {

    @NotBlank(message = "O tipo da ausencia e obrigatorio.")
    private String tipo;

    @NotNull(message = "A data de inicio e obrigatoria.")
    private LocalDate dataInicio;

    @NotNull(message = "A data de fim e obrigatoria.")
    private LocalDate dataFim;

    private String motivo;
    private String observacao;
    private String status;
}
