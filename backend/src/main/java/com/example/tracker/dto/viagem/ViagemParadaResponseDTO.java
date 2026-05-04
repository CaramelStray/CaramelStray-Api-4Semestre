package com.example.tracker.dto.viagem;

import com.example.tracker.entity.ViagemParada;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViagemParadaResponseDTO {

    private Integer codigo;
    private Integer codigoViagem;
    private Integer ordem;
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

    public static ViagemParadaResponseDTO fromEntity(ViagemParada parada) {
        ViagemParadaResponseDTO dto = new ViagemParadaResponseDTO();

        dto.setCodigo(parada.getCodigo());

        if (parada.getViagem() != null) {
            dto.setCodigoViagem(parada.getViagem().getCodigo());
        }

        dto.setOrdem(parada.getOrdem());
        dto.setDescricaoLocal(parada.getDescricaoLocal());
        dto.setEndereco(parada.getEndereco());
        dto.setCidade(parada.getCidade());
        dto.setEstadoRegiao(parada.getEstadoRegiao());
        dto.setLatitude(parada.getLatitude());
        dto.setLongitude(parada.getLongitude());
        dto.setDataChegadaPrevista(parada.getDataChegadaPrevista());
        dto.setDataChegadaReal(parada.getDataChegadaReal());
        dto.setDataSaidaPrevista(parada.getDataSaidaPrevista());
        dto.setDataSaidaReal(parada.getDataSaidaReal());
        dto.setObservacao(parada.getObservacao());

        return dto;
    }
}
