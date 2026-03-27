package com.example.tracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_cad_permissao")
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer id;

    @Column(name = "descricao", nullable = false)
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
