package com.example.tracker.dto.maquinacontrato;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaContratoCreateDTO {

    @NotNull(message = "O codigo do contrato e obrigatorio.")
    private Integer codigoContrato;

    @NotNull(message = "O codigo do catalogo de maquina e obrigatorio.")
    private Integer codigoCatalogoMaquina;

    private String numeroSerie;
    private LocalDate dataInstalacao;
    private String manutencaoFeita;
    private Boolean trabalhoEmAltura;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
