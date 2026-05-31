package com.example.tracker.dto.ordemservico;

import com.example.tracker.entity.OrdemServico;
import java.math.BigDecimal;

public class OrdemMapaResponseDTO {
    private Integer codigo;
    private Integer codigoCliente;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String endereco;
    private String status;

    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }
    public Integer getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(Integer codigoCliente) { this.codigoCliente = codigoCliente; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static OrdemMapaResponseDTO fromEntity(OrdemServico o) {
        OrdemMapaResponseDTO dto = new OrdemMapaResponseDTO();
        dto.setCodigo(o.getCodigo());
        if (o.getCliente() != null) dto.setCodigoCliente(o.getCliente().getId());
        if (o.getMaquinaContrato() != null) {
            dto.setLatitude(o.getMaquinaContrato().getLatitude());
            dto.setLongitude(o.getMaquinaContrato().getLongitude());
        }
        StringBuilder end = new StringBuilder();
        if (o.getCliente() != null) {
            if (o.getCliente().getRua() != null) end.append(o.getCliente().getRua());
            if (o.getCliente().getNumero() != null) end.append(" ").append(o.getCliente().getNumero());
            if (o.getCliente().getCidade() != null) end.append(" - ").append(o.getCliente().getCidade());
        }
        dto.setEndereco(end.toString());
        dto.setStatus(o.getStatus());
        return dto;
    }
}
