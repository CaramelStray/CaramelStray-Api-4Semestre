package com.example.tracker.dto.viagem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViagemParadaCreateDTO {

    @NotNull(message = "A ordem da parada e obrigatoria.")
    private Integer ordem;

    @NotBlank(message = "A descricao do local e obrigatoria.")
    private String descricaoLocal;

    private String endereco;
    private String cidade;
    private String estadoRegiao;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime dataChegadaPrevista;
    private LocalDateTime dataChegadaReal;
    private LocalDateTime dataSaidaPrevista;
    private LocalDateTime dataSaidaReal;
    private String observacao;
}
