package com.example.tracker.dto.ordemservico;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdemServicoCreateDTO {

    @NotNull(message = "O codigo do cliente e obrigatorio.")
    private Integer codigoCliente;

    private Integer codigoFuncionario;

    private Integer codigoSoftwareInstalado;

    @NotNull(message = "O codigo do contrato e obrigatorio.")
    private Integer codigoContrato;

    @NotNull(message = "O codigo da maquina do contrato e obrigatorio.")
    private Integer codigoMaquinaContrato;

    private String status;
    private String criticidade;

    // pode vir null → service resolve com now()
    private LocalDateTime dataAbertura;

    private LocalDateTime dataAgendamento;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFimExecucao;

    private String observacaoGeral;
}