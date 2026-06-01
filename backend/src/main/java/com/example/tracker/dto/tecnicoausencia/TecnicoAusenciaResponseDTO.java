package com.example.tracker.dto.tecnicoausencia;

import com.example.tracker.entity.TecnicoAusencia;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TecnicoAusenciaResponseDTO {

    private Integer codigo;
    private Integer codigoTecnico;
    private String nomeTecnico;
    private String tipo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String motivo;
    private String observacao;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public static TecnicoAusenciaResponseDTO fromEntity(TecnicoAusencia ausencia) {
        TecnicoAusenciaResponseDTO dto = new TecnicoAusenciaResponseDTO();

        dto.setCodigo(ausencia.getCodigo());
        if (ausencia.getTecnico() != null) {
            dto.setCodigoTecnico(ausencia.getTecnico().getId());
            dto.setNomeTecnico(ausencia.getTecnico().getNome());
        }
        if (ausencia.getTipo() != null) {
            dto.setTipo(ausencia.getTipo().name());
        }
        dto.setDataInicio(ausencia.getDataInicio());
        dto.setDataFim(ausencia.getDataFim());
        dto.setMotivo(ausencia.getMotivo());
        dto.setObservacao(ausencia.getObservacao());
        if (ausencia.getStatus() != null) {
            dto.setStatus(ausencia.getStatus().name());
        }
        dto.setDataCriacao(ausencia.getDataCriacao());
        dto.setDataAtualizacao(ausencia.getDataAtualizacao());

        return dto;
    }
}
