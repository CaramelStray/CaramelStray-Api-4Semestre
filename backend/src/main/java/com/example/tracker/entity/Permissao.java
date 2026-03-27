package com.example.tracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "permissoes")
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    public Integer getId() 
    { return id; }
    
    public void setId(Integer id) 
    { this.id = id; }
    
    public String getNome() 
    { return nome; }
    
    public void setNome(String nome) 
    { this.nome = nome; }
}
