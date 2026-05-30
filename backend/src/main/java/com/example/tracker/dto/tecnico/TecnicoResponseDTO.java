package com.example.tracker.dto.tecnico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.tracker.dto.TecnicoHabilidade.TecnicoHabilidadeResponseDTO;

public class TecnicoResponseDTO {

    private Integer id;
    private String email;
    private String nome;
    private String cpf;
    private String cargo;
    private String telefone;
    private String certificacao;
    private String estado;
    private String disponibilidade;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime ultimaAtualizacaoLocalizacao;
    private List<TecnicoHabilidadeResponseDTO> habilidades;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDateTime getUltimaAtualizacaoLocalizacao() {
        return ultimaAtualizacaoLocalizacao;
    }

    public void setUltimaAtualizacaoLocalizacao(LocalDateTime ultimaAtualizacaoLocalizacao) {
        this.ultimaAtualizacaoLocalizacao = ultimaAtualizacaoLocalizacao;
    }

    public List<TecnicoHabilidadeResponseDTO> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<TecnicoHabilidadeResponseDTO> habilidades) {
        this.habilidades = habilidades;
    }
}