package com.example.tracker.dto.catalogoativo;

import com.example.tracker.entity.CatalogoAtivo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogoAtivoResponseDTO {

    private Integer codigo;
    private String descricaoProduto;
    private String modelo;
    private String marca;
    private String descricao;
    private String especificacao;
    private String tipo;

    public static CatalogoAtivoResponseDTO fromEntity(CatalogoAtivo entity) {
        CatalogoAtivoResponseDTO dto = new CatalogoAtivoResponseDTO();
        dto.setCodigo(entity.getCodigo());
        dto.setDescricaoProduto(entity.getDescricaoProduto());
        dto.setModelo(entity.getModelo());
        dto.setMarca(entity.getMarca());
        dto.setDescricao(entity.getDescricao());
        dto.setEspecificacao(entity.getEspecificacao());
        dto.setTipo(entity.getTipo());
        return dto;
    }
}
