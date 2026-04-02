package com.example.tracker.dto.software;

import com.example.tracker.entity.CatalogoSoftware;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class CatalogoSoftwareResponseDTO {

    private Integer id;
    private String identificador;
    private String status;
    private String nomeSoftware;
    private String versao;
    private String tipo;
    private String desenvolvedorFornecedor;
    private String urlDocumentacao;
    private String descricaoTecnica;
    private Boolean ativo;
    private Integer quantidadeChecklistPadrao;
    private Integer quantidadeItensObrigatorios;
    private LocalDateTime dataCadastro;
    private List<SoftwareChecklistItemResponseDTO> checklistPadrao;

    public static CatalogoSoftwareResponseDTO fromEntity(CatalogoSoftware software) {
        CatalogoSoftwareResponseDTO dto = new CatalogoSoftwareResponseDTO();
        dto.setId(software.getId());
        dto.setIdentificador(formatarIdentificador(software.getId()));
        dto.setStatus(Boolean.TRUE.equals(software.getAtivo()) ? "Ativo" : "Inativo");
        dto.setNomeSoftware(software.getNome());
        dto.setVersao(software.getVersao());
        dto.setTipo(software.getTipo());
        dto.setDesenvolvedorFornecedor(software.getDesenvolvedorFornecedor());
        dto.setUrlDocumentacao(software.getUrlDocumentacao());
        dto.setDescricaoTecnica(software.getDescricaoTecnica());
        dto.setAtivo(software.getAtivo());
        dto.setDataCadastro(software.getDataCadastro());

        List<SoftwareChecklistItemResponseDTO> checklist = software.getChecklistPadrao() == null
                ? List.of()
                : software.getChecklistPadrao().stream()
                        .map(SoftwareChecklistItemResponseDTO::fromEntity)
                        .sorted(Comparator.comparing(
                                item -> item.getDescricao() == null ? "" : item.getDescricao(),
                                String.CASE_INSENSITIVE_ORDER))
                        .toList();

        dto.setChecklistPadrao(checklist);
        dto.setQuantidadeChecklistPadrao(checklist.size());
        dto.setQuantidadeItensObrigatorios((int) checklist.stream()
                .filter(item -> Boolean.TRUE.equals(item.getObrigatorio()))
                .count());
        return dto;
    }

    private static String formatarIdentificador(Integer id) {
        if (id == null) {
            return null;
        }
        return String.format("SW-%04d", id);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNomeSoftware() {
        return nomeSoftware;
    }

    public void setNomeSoftware(String nomeSoftware) {
        this.nomeSoftware = nomeSoftware;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDesenvolvedorFornecedor() {
        return desenvolvedorFornecedor;
    }

    public void setDesenvolvedorFornecedor(String desenvolvedorFornecedor) {
        this.desenvolvedorFornecedor = desenvolvedorFornecedor;
    }

    public String getUrlDocumentacao() {
        return urlDocumentacao;
    }

    public void setUrlDocumentacao(String urlDocumentacao) {
        this.urlDocumentacao = urlDocumentacao;
    }

    public String getDescricaoTecnica() {
        return descricaoTecnica;
    }

    public void setDescricaoTecnica(String descricaoTecnica) {
        this.descricaoTecnica = descricaoTecnica;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getQuantidadeChecklistPadrao() {
        return quantidadeChecklistPadrao;
    }

    public void setQuantidadeChecklistPadrao(Integer quantidadeChecklistPadrao) {
        this.quantidadeChecklistPadrao = quantidadeChecklistPadrao;
    }

    public Integer getQuantidadeItensObrigatorios() {
        return quantidadeItensObrigatorios;
    }

    public void setQuantidadeItensObrigatorios(Integer quantidadeItensObrigatorios) {
        this.quantidadeItensObrigatorios = quantidadeItensObrigatorios;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public List<SoftwareChecklistItemResponseDTO> getChecklistPadrao() {
        return checklistPadrao;
    }

    public void setChecklistPadrao(List<SoftwareChecklistItemResponseDTO> checklistPadrao) {
        this.checklistPadrao = checklistPadrao;
    }
}
