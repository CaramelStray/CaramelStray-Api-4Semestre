package com.example.tracker.dto.ordemservico;

import com.example.tracker.entity.OrdemServico;
import java.time.LocalDateTime;

public class TecnicoOrdensResponseDTO {

    private Integer codigo;
    private String nomeCliente;
    private String status;
    private String criticidade;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataAgendamento;

    public static TecnicoOrdensResponseDTO fromEntity(OrdemServico os) {
        TecnicoOrdensResponseDTO dto = new TecnicoOrdensResponseDTO();
        dto.setCodigo(os.getCodigo());
        dto.setNomeCliente(os.getCliente() != null ? os.getCliente().getNomeEmpresa() : null);
        dto.setStatus(os.getStatus());
        dto.setCriticidade(os.getCriticidade());
        dto.setDataAbertura(os.getDataAbertura());
        dto.setDataAgendamento(os.getDataAgendamento());
        return dto;
    }

    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCriticidade() { return criticidade; }
    public void setCriticidade(String criticidade) { this.criticidade = criticidade; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDateTime getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(LocalDateTime dataAgendamento) { this.dataAgendamento = dataAgendamento; }
}
