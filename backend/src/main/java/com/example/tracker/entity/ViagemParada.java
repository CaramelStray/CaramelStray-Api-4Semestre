package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_srv_viagem_parada")
public class ViagemParada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_viagem", nullable = false)
    private Viagem viagem;

    @Column(name = "ordem", nullable = false)
    private Integer ordem;

    @Column(name = "descricao_local", nullable = false, length = 255)
    private String descricaoLocal;

    @Column(name = "endereco", length = 255)
    private String endereco;

    @Column(name = "cidade", length = 100)
    private String cidade;

    @Column(name = "estado_regiao", length = 100)
    private String estadoRegiao;

    @Column(name = "latitude", precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(name = "data_chegada_prevista")
    private LocalDateTime dataChegadaPrevista;

    @Column(name = "data_chegada_real")
    private LocalDateTime dataChegadaReal;

    @Column(name = "data_saida_prevista")
    private LocalDateTime dataSaidaPrevista;

    @Column(name = "data_saida_real")
    private LocalDateTime dataSaidaReal;

    @Column(name = "observacao")
    private String observacao;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Viagem getViagem() {
        return viagem;
    }

    public void setViagem(Viagem viagem) {
        this.viagem = viagem;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getDescricaoLocal() {
        return descricaoLocal;
    }

    public void setDescricaoLocal(String descricaoLocal) {
        this.descricaoLocal = descricaoLocal;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstadoRegiao() {
        return estadoRegiao;
    }

    public void setEstadoRegiao(String estadoRegiao) {
        this.estadoRegiao = estadoRegiao;
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

    public LocalDateTime getDataChegadaPrevista() {
        return dataChegadaPrevista;
    }

    public void setDataChegadaPrevista(LocalDateTime dataChegadaPrevista) {
        this.dataChegadaPrevista = dataChegadaPrevista;
    }

    public LocalDateTime getDataChegadaReal() {
        return dataChegadaReal;
    }

    public void setDataChegadaReal(LocalDateTime dataChegadaReal) {
        this.dataChegadaReal = dataChegadaReal;
    }

    public LocalDateTime getDataSaidaPrevista() {
        return dataSaidaPrevista;
    }

    public void setDataSaidaPrevista(LocalDateTime dataSaidaPrevista) {
        this.dataSaidaPrevista = dataSaidaPrevista;
    }

    public LocalDateTime getDataSaidaReal() {
        return dataSaidaReal;
    }

    public void setDataSaidaReal(LocalDateTime dataSaidaReal) {
        this.dataSaidaReal = dataSaidaReal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
