package com.example.tracker.dto.maquinasoftwareinstalado;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaSoftwareInstaladoCreateDTO {

    @NotNull(message = "O codigo da maquina do contrato e obrigatorio.")
    private Integer codigoMaquinaContrato;

    @NotNull(message = "O codigo do software e obrigatorio.")
    private Integer codigoSoftware;

    private String chaveLicenca;
    private String versaoInstalada;
}
