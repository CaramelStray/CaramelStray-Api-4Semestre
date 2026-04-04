package com.example.tracker.dto.catalogo;

import com.example.tracker.entity.CatalogoMaquina;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogoMaquinaResponseDTO {

    private Integer codigo;
    private String descricao;
    private String especificacao;
    private String limiteManutencao;
    private Integer quantidadeChecklistPadrao;
    private List<MaquinaChecklistItemResponseDTO> checklistPadrao;

    public static CatalogoMaquinaResponseDTO fromEntity(CatalogoMaquina maquina) {
        CatalogoMaquinaResponseDTO dto = new CatalogoMaquinaResponseDTO();
        dto.setCodigo(maquina.getCodigo());
        dto.setDescricao(maquina.getDescricao());
        dto.setEspecificacao(maquina.getEspecificacao());
        dto.setLimiteManutencao(maquina.getLimiteManutencao());

        List<MaquinaChecklistItemResponseDTO> checklist = maquina.getChecklistPadrao() == null
                ? List.of()
                : maquina.getChecklistPadrao().stream()
                        .map(MaquinaChecklistItemResponseDTO::fromEntity)
                        .collect(Collectors.toList());

        dto.setChecklistPadrao(checklist);
        dto.setQuantidadeChecklistPadrao(checklist.size());
        return dto;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEspecificacao() {
        return especificacao;
    }

    public void setEspecificacao(String especificacao) {
        this.especificacao = especificacao;
    }

    public String getLimiteManutencao() {
        return limiteManutencao;
    }

    public void setLimiteManutencao(String limiteManutencao) {
        this.limiteManutencao = limiteManutencao;
    }

    public Integer getQuantidadeChecklistPadrao() {
        return quantidadeChecklistPadrao;
    }

    public void setQuantidadeChecklistPadrao(Integer quantidadeChecklistPadrao) {
        this.quantidadeChecklistPadrao = quantidadeChecklistPadrao;
    }

    public List<MaquinaChecklistItemResponseDTO> getChecklistPadrao() {
        return checklistPadrao;
    }

    public void setChecklistPadrao(List<MaquinaChecklistItemResponseDTO> checklistPadrao) {
        this.checklistPadrao = checklistPadrao;
    }
}
