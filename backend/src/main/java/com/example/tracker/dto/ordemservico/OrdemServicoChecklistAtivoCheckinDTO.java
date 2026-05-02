package com.example.tracker.dto.ordemservico;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdemServicoChecklistAtivoCheckinDTO {

    private String statusIntervencao;
    private LocalDateTime dataIntervencao;
    private String observacaoIntervencao;
}
