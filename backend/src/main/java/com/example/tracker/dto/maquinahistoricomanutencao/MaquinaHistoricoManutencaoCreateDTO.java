package com.example.tracker.dto.maquinahistoricomanutencao;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaquinaHistoricoManutencaoCreateDTO {

    @NotNull(message = "O codigo da maquina do contrato e obrigatorio.")
    private Integer codigoMaquinaContrato;

    private Integer codigoSoftwareInstalado;

    @NotNull(message = "O codigo do tipo de manutencao e obrigatorio.")
    private Integer codigoTipoManutencao;

    private String status;
    private String criticidade;
    private LocalDateTime vencimento;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFimExecucao;
    private String observacaoGeral;
}
