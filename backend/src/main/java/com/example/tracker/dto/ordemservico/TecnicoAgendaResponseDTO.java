package com.example.tracker.dto.ordemservico;

import com.example.tracker.entity.Tecnico;
import com.example.tracker.enums.StatusTecnico;
import java.util.ArrayList;
import java.util.List;

public class TecnicoAgendaResponseDTO {

    private Integer id;
    private String nome;
    private String disponibilidade;
    private Boolean bloqueado;
    private List<AgendaOrdemResponseDTO> ordens = new ArrayList<>();

    public static TecnicoAgendaResponseDTO fromEntity(Tecnico tecnico) {
        TecnicoAgendaResponseDTO dto = new TecnicoAgendaResponseDTO();
        dto.setId(tecnico.getId());
        dto.setNome(tecnico.getNome());
        dto.setDisponibilidade(tecnico.getDisponibilidade());
        StatusTecnico status = StatusTecnico.from(tecnico.getDisponibilidade());
        dto.setBloqueado(status != null && status != StatusTecnico.DISPONIVEL);
        return dto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(String disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public List<AgendaOrdemResponseDTO> getOrdens() {
        return ordens;
    }

    public void setOrdens(List<AgendaOrdemResponseDTO> ordens) {
        this.ordens = ordens;
    }
}
