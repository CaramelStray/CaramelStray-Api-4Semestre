package com.example.tracker.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissoes",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permissao_id")
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
