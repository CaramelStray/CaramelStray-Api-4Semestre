package com.example.tracker.dto.viagem;

import com.example.tracker.entity.TipoViagem;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoViagemResponseDTO {

    private Integer codigo;
    private String descricao;
    private String observacao;
    private Boolean ativo;
    private LocalDateTime dataCadastro;

    public static TipoViagemResponseDTO fromEntity(TipoViagem tipoViagem) {
        TipoViagemResponseDTO dto = new TipoViagemResponseDTO();

        dto.setCodigo(tipoViagem.getCodigo());
        dto.setDescricao(tipoViagem.getDescricao());
        dto.setObservacao(tipoViagem.getObservacao());
        dto.setAtivo(tipoViagem.getAtivo());
        dto.setDataCadastro(tipoViagem.getDataCadastro());

        return dto;
    }
}
