package com.example.tracker.dto.viagem;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViagemUpdateDTO {

    private Integer codigoTipoViagem;
    private Integer codigoCliente;
    private Integer codigoFuncionarioResponsavel;
    private Integer codigoOrdemServico;
    private String status;
    private LocalDateTime dataSaidaPrevista;
    private LocalDateTime dataSaidaReal;
    private LocalDateTime dataRetornoPrevisto;
    private LocalDateTime dataRetornoReal;
    private String origem;
    private String destino;
    private BigDecimal kmPrevisto;
    private BigDecimal kmReal;
    private String observacao;

    @Valid
    private List<ViagemParadaCreateDTO> paradas;
}
