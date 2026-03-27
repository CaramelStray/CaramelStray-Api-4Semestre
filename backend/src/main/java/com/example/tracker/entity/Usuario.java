package com.example.tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tb_cad_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", insertable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "tb_cad_usuario_perfil",
        joinColumns = @JoinColumn(name = "codigo_usuario"),
        inverseJoinColumns = @JoinColumn(name = "codigo_perfil")
    )
    private Set<Perfil> perfis;

    public Integer getId() 
    { return id; }

    public void setId(Integer id) 
    { this.id = id; }

    public String getEmail() 
    { return email; }
    
    public void setEmail(String email) 
    { this.email = email; }
    
    public String getSenha() 
    { return senha; }
    
    public void setSenha(String senha) 
    { this.senha = senha; }
    
    public Boolean getAtivo() 
    { return ativo; }
    
    public void setAtivo(Boolean ativo) 
    { this.ativo = ativo; }
    
    public LocalDateTime getDataCriacao() 
    { return dataCriacao; }
    
    public void setDataCriacao(LocalDateTime dataCriacao) 
    { this.dataCriacao = dataCriacao; }
    
    public Set<Perfil> getPerfis() 
    { return perfis; }
    
    public void setPerfis(Set<Perfil> perfis) 
    { this.perfis = perfis; }
}
