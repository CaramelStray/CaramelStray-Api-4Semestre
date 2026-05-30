package com.example.tracker.dto.ciclo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CicloEmbarcacaoCreateDTO {

    @NotBlank(message = "Embarcacao e obrigatoria")
    private String embarcacao;

    @NotNull(message = "Data de chegada e obrigatoria")
    private LocalDateTime dataChegada;

    private LocalDateTime dataSaida;

    @NotBlank(message = "Local e obrigatorio")
    private String local;

    public String getEmbarcacao() { return embarcacao; }
    public void setEmbarcacao(String embarcacao) { this.embarcacao = embarcacao; }

    public LocalDateTime getDataChegada() { return dataChegada; }
    public void setDataChegada(LocalDateTime dataChegada) { this.dataChegada = dataChegada; }

    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
}
