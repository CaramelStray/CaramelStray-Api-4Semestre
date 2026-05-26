package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_srv_ciclo_embarcacao")
public class CicloEmbarcacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @Column(name = "embarcacao", nullable = false, length = 255)
    private String embarcacao;

    @Column(name = "data_chegada", nullable = false)
    private LocalDateTime dataChegada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Column(name = "local", nullable = false, length = 255)
    private String local;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getEmbarcacao() {
        return embarcacao;
    }

    public void setEmbarcacao(String embarcacao) {
        this.embarcacao = embarcacao;
    }

    public LocalDateTime getDataChegada() {
        return dataChegada;
    }

    public void setDataChegada(LocalDateTime dataChegada) {
        this.dataChegada = dataChegada;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
}
