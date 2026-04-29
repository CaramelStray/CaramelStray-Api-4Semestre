package com.example.tracker.dto.viagem;

import com.example.tracker.entity.Viagem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViagemResponseDTO {

    private Integer codigo;
    private Integer codigoTipoViagem;
    private String descricaoTipoViagem;
    private Integer codigoCliente;
    private String nomeCliente;
    private Integer codigoFuncionarioResponsavel;
    private String nomeFuncionarioResponsavel;
    private Integer codigoOrdemServico;
    private String status;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataSaidaPrevista;
    private LocalDateTime dataSaidaReal;
    private LocalDateTime dataRetornoPrevisto;
    private LocalDateTime dataRetornoReal;
    private String origem;
    private String destino;
    private BigDecimal kmPrevisto;
    private BigDecimal kmReal;
    private String observacao;
    private List<ViagemParadaResponseDTO> paradas;

    public static ViagemResponseDTO fromEntity(Viagem viagem) {
        ViagemResponseDTO dto = new ViagemResponseDTO();

        dto.setCodigo(viagem.getCodigo());

        if (viagem.getTipoViagem() != null) {
            dto.setCodigoTipoViagem(viagem.getTipoViagem().getCodigo());
            dto.setDescricaoTipoViagem(viagem.getTipoViagem().getDescricao());
        }

        if (viagem.getCliente() != null) {
            dto.setCodigoCliente(viagem.getCliente().getId());
            dto.setNomeCliente(viagem.getCliente().getNomeEmpresa());
        }

        if (viagem.getFuncionarioResponsavel() != null) {
            dto.setCodigoFuncionarioResponsavel(viagem.getFuncionarioResponsavel().getId());
            dto.setNomeFuncionarioResponsavel(viagem.getFuncionarioResponsavel().getNome());
        }

        if (viagem.getOrdemServico() != null) {
            dto.setCodigoOrdemServico(viagem.getOrdemServico().getCodigo());
        }

        dto.setStatus(viagem.getStatus());
        dto.setDataCadastro(viagem.getDataCadastro());
        dto.setDataSaidaPrevista(viagem.getDataSaidaPrevista());
        dto.setDataSaidaReal(viagem.getDataSaidaReal());
        dto.setDataRetornoPrevisto(viagem.getDataRetornoPrevisto());
        dto.setDataRetornoReal(viagem.getDataRetornoReal());
        dto.setOrigem(viagem.getOrigem());
        dto.setDestino(viagem.getDestino());
        dto.setKmPrevisto(viagem.getKmPrevisto());
        dto.setKmReal(viagem.getKmReal());
        dto.setObservacao(viagem.getObservacao());

        if (viagem.getParadas() != null) {
            dto.setParadas(viagem.getParadas().stream()
                    .map(ViagemParadaResponseDTO::fromEntity)
                    .toList());
        }

        return dto;
    }
}
