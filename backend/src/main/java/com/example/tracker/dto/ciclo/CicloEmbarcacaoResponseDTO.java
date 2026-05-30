package com.example.tracker.dto.ciclo;

import com.example.tracker.entity.CicloEmbarcacao;
import java.time.LocalDateTime;

public class CicloEmbarcacaoResponseDTO {

    private Integer codigo;
    private String embarcacao;
    private LocalDateTime dataChegada;
    private LocalDateTime dataSaida;
    private String local;

    public static CicloEmbarcacaoResponseDTO fromEntity(CicloEmbarcacao ciclo) {
        CicloEmbarcacaoResponseDTO dto = new CicloEmbarcacaoResponseDTO();
        dto.setCodigo(ciclo.getCodigo());
        dto.setEmbarcacao(ciclo.getEmbarcacao());
        dto.setDataChegada(ciclo.getDataChegada());
        dto.setDataSaida(ciclo.getDataSaida());
        dto.setLocal(ciclo.getLocal());
        return dto;
    }

    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public String getEmbarcacao() { return embarcacao; }
    public void setEmbarcacao(String embarcacao) { this.embarcacao = embarcacao; }

    public LocalDateTime getDataChegada() { return dataChegada; }
    public void setDataChegada(LocalDateTime dataChegada) { this.dataChegada = dataChegada; }

    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
}
