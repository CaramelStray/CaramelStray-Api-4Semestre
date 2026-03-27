package com.example.tracker.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tb_cad_perfil")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer id;

    @Column(name = "descricao", nullable = false)
    private String nome;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "tb_cad_perfil_permissao",
        joinColumns = @JoinColumn(name = "codigo_perfil"),
        inverseJoinColumns = @JoinColumn(name = "codigo_permissao")
    )
    private Set<Permissao> permissoes;

    public Integer getId() 
    { return id; }
    
    public void setId(Integer id) 
    { this.id = id; }
    
    public String getNome() 
    { return nome; }
    
    public void setNome(String nome) 
    { this.nome = nome; }
    
    public Set<Permissao> getPermissoes() 
    { return permissoes; }
    
    public void setPermissoes(Set<Permissao> permissoes) 
    { this.permissoes = permissoes; }
}
