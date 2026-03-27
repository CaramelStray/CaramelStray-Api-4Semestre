package com.example.tracker.dto.cliente;

import com.example.tracker.entity.Cliente;
import java.time.LocalDateTime;

public class ClienteResponseDTO {

    private Integer id;
    private Integer usuarioId;
    private String emailUsuarioCadastro;
    private String nomeEmpresa;
    private String documento;
    private String emailContato;
    private String telefoneContato;
    private String nomeResponsavel;
    private String pais;
    private String estadoRegiao;
    private String cidade;
    private String classificacaoDistancia;
    private String fusoHorario;
    private Boolean ativo;
    private LocalDateTime dataCadastro;

    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setUsuarioId(cliente.getUsuario() != null ? cliente.getUsuario().getId() : null);
        dto.setEmailUsuarioCadastro(cliente.getUsuario() != null ? cliente.getUsuario().getEmail() : null);
        dto.setNomeEmpresa(cliente.getNomeEmpresa());
        dto.setDocumento(cliente.getDocumento());
        dto.setEmailContato(cliente.getEmailContato());
        dto.setTelefoneContato(cliente.getTelefoneContato());
        dto.setNomeResponsavel(cliente.getNomeResponsavel());
        dto.setPais(cliente.getPais());
        dto.setEstadoRegiao(cliente.getEstadoRegiao());
        dto.setCidade(cliente.getCidade());
        dto.setClassificacaoDistancia(cliente.getClassificacaoDistancia());
        dto.setFusoHorario(cliente.getFusoHorario());
        dto.setAtivo(cliente.getAtivo());
        dto.setDataCadastro(cliente.getDataCadastro());
        return dto;
    }

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

    public String getEmailUsuarioCadastro() {
        return emailUsuarioCadastro;
    }

    public void setEmailUsuarioCadastro(String emailUsuarioCadastro) {
        this.emailUsuarioCadastro = emailUsuarioCadastro;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmailContato() {
        return emailContato;
    }

    public void setEmailContato(String emailContato) {
        this.emailContato = emailContato;
    }

    public String getTelefoneContato() {
        return telefoneContato;
    }

    public void setTelefoneContato(String telefoneContato) {
        this.telefoneContato = telefoneContato;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstadoRegiao() {
        return estadoRegiao;
    }

    public void setEstadoRegiao(String estadoRegiao) {
        this.estadoRegiao = estadoRegiao;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getClassificacaoDistancia() {
        return classificacaoDistancia;
    }

    public void setClassificacaoDistancia(String classificacaoDistancia) {
        this.classificacaoDistancia = classificacaoDistancia;
    }

    public String getFusoHorario() {
        return fusoHorario;
    }

    public void setFusoHorario(String fusoHorario) {
        this.fusoHorario = fusoHorario;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
