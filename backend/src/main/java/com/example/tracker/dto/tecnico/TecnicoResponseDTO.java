package com.example.tracker.dto.tecnico;

import com.example.tracker.entity.Tecnico;
import java.math.BigDecimal;

public class TecnicoResponseDTO {

    private Integer id;
    private Integer usuarioId;
    private String email;
    private String nome;
    private String cpf;
    private String cargo;
    private String telefone;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String certificacao;
    private String estado;
    private String disponibilidade;

    public static TecnicoResponseDTO fromEntity(Tecnico tecnico) {
        TecnicoResponseDTO dto = new TecnicoResponseDTO();
        dto.setId(tecnico.getId());
        dto.setUsuarioId(tecnico.getUsuario() != null ? tecnico.getUsuario().getId() : null);
        dto.setEmail(tecnico.getUsuario() != null ? tecnico.getUsuario().getEmail() : null);
        dto.setNome(tecnico.getNome());
        dto.setCpf(tecnico.getCpf());
        dto.setCargo(tecnico.getCargo());
        dto.setTelefone(tecnico.getTelefone());
        dto.setLatitude(tecnico.getLatitude());
        dto.setLongitude(tecnico.getLongitude());
        dto.setCertificacao(tecnico.getCertificacao());
        dto.setEstado(tecnico.getEstado());
        dto.setDisponibilidade(tecnico.getDisponibilidade());
        return dto;
    }

    // GETTERS E SETTERS

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getCertificacao() {
        return certificacao;
    }

    public void setCertificacao(String certificacao) {
        this.certificacao = certificacao;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(String disponibilidade) {
        this.disponibilidade = disponibilidade;
    }
}