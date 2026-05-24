package com.example.tracker.dto.QuantidadeOrdens;

public class OrdensPorStatusDTO {

    private String status;
    private Long quantidade;

    public OrdensPorStatusDTO(String status, Long quantidade) {
        this.status = status;
        this.quantidade = quantidade;
    }

    public String getStatus() {
        return status;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }
}