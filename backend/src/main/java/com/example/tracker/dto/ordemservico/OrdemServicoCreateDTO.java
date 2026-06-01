package com.example.tracker.dto.ordemservico;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdemServicoCreateDTO {

    @NotNull(message = "O codigo do cliente e obrigatorio.")
    private Integer codigoCliente;

    private Integer codigoFuncionario;

    private List<Integer> codigosFuncionarios;

    private Integer codigoSoftwareInstalado;

    @NotNull(message = "O codigo do contrato e obrigatorio.")
    private Integer codigoContrato;

    @NotNull(message = "O codigo da maquina do contrato e obrigatorio.")
    private Integer codigoMaquinaContrato;

    private String status;
    private String criticidade;
    private String tipoOrdem;

    // pode vir null → service resolve com now()
    private LocalDateTime dataAbertura;

    private LocalDateTime dataAgendamento;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFimExecucao;

    private String observacaoGeral;

    private Integer previsaoManutencao;

    @Valid
    private List<OrdemServicoChecklistAtivoCreateDTO> checklistAtivos;
}
