package com.example.tracker.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_srv_viagem")
public class Viagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_tipo_viagem", nullable = false)
    private TipoViagem tipoViagem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "codigo_funcionario_responsavel")
    private Tecnico funcionarioResponsavel;

    @ManyToOne
    @JoinColumn(name = "codigo_ordem_servico")
    private OrdemServico ordemServico;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "ABERTA";

    @Column(name = "data_cadastro", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "data_saida_prevista")
    private LocalDateTime dataSaidaPrevista;

    @Column(name = "data_saida_real")
    private LocalDateTime dataSaidaReal;

    @Column(name = "data_retorno_previsto")
    private LocalDateTime dataRetornoPrevisto;

    @Column(name = "data_retorno_real")
    private LocalDateTime dataRetornoReal;

    @Column(name = "origem", length = 255)
    private String origem;

    @Column(name = "destino", length = 255)
    private String destino;

    @Column(name = "km_previsto", precision = 10, scale = 2)
    private BigDecimal kmPrevisto;

    @Column(name = "km_real", precision = 10, scale = 2)
    private BigDecimal kmReal;

    @Column(name = "observacao")
    private String observacao;

    @OneToMany(mappedBy = "viagem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<ViagemParada> paradas = new ArrayList<>();

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public TipoViagem getTipoViagem() {
        return tipoViagem;
    }

    public void setTipoViagem(TipoViagem tipoViagem) {
        this.tipoViagem = tipoViagem;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Tecnico getFuncionarioResponsavel() {
        return funcionarioResponsavel;
    }

    public void setFuncionarioResponsavel(Tecnico funcionarioResponsavel) {
        this.funcionarioResponsavel = funcionarioResponsavel;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
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

    public LocalDateTime getDataRetornoPrevisto() {
        return dataRetornoPrevisto;
    }

    public void setDataRetornoPrevisto(LocalDateTime dataRetornoPrevisto) {
        this.dataRetornoPrevisto = dataRetornoPrevisto;
    }

    public LocalDateTime getDataRetornoReal() {
        return dataRetornoReal;
    }

    public void setDataRetornoReal(LocalDateTime dataRetornoReal) {
        this.dataRetornoReal = dataRetornoReal;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public BigDecimal getKmPrevisto() {
        return kmPrevisto;
    }

    public void setKmPrevisto(BigDecimal kmPrevisto) {
        this.kmPrevisto = kmPrevisto;
    }

    public BigDecimal getKmReal() {
        return kmReal;
    }

    public void setKmReal(BigDecimal kmReal) {
        this.kmReal = kmReal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ViagemParada> getParadas() {
        return paradas;
    }

    public void setParadas(List<ViagemParada> paradas) {
        this.paradas = paradas;
    }
}
