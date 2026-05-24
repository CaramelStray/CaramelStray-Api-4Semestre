package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_srv_ordem_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Integer codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "codigo_funcionario")
    private Tecnico funcionario;

    @ManyToOne
    @JoinColumn(name = "codigo_software_instalado")
    private MaquinaSoftwareInstalado softwareInstalado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_contrato", nullable = false)
    private Contrato contrato;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_maquina_contrato", nullable = false)
    private MaquinaContrato maquinaContrato;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "criticidade", length = 50)
    private String criticidade;

    @Column(name = "tipo_ordem", length = 50)
    private String tipoOrdem;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_agendamento")
    private LocalDateTime dataAgendamento;

    @Column(name = "data_inicio_execucao")
    private LocalDateTime dataInicioExecucao;

    @Column(name = "data_fim_execucao")
    private LocalDateTime dataFimExecucao;

    @Column(name = "observacao_geral")
    private String observacaoGeral;

    @OneToMany(mappedBy = "ordemServico")
    private List<OrdemServicoChecklistAtivo> checklistAtivos = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico")
    private List<OrdemServicoTecnico> tecnicos = new ArrayList<>();

    @OneToOne(mappedBy = "ordemServico")
    private MaquinaHistoricoManutencao historicoManutencao;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Tecnico getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Tecnico funcionario) {
        this.funcionario = funcionario;
    }

    public MaquinaSoftwareInstalado getSoftwareInstalado() {
        return softwareInstalado;
    }

    public void setSoftwareInstalado(MaquinaSoftwareInstalado softwareInstalado) {
        this.softwareInstalado = softwareInstalado;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public MaquinaContrato getMaquinaContrato() {
        return maquinaContrato;
    }

    public void setMaquinaContrato(MaquinaContrato maquinaContrato) {
        this.maquinaContrato = maquinaContrato;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCriticidade() {
        return criticidade;
    }

    public void setCriticidade(String criticidade) {
        this.criticidade = criticidade;
    }

    public String getTipoOrdem() {
        return tipoOrdem;
    }

    public void setTipoOrdem(String tipoOrdem) {
        this.tipoOrdem = tipoOrdem;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalDateTime getDataInicioExecucao() {
        return dataInicioExecucao;
    }

    public void setDataInicioExecucao(LocalDateTime dataInicioExecucao) {
        this.dataInicioExecucao = dataInicioExecucao;
    }

    public LocalDateTime getDataFimExecucao() {
        return dataFimExecucao;
    }

    public void setDataFimExecucao(LocalDateTime dataFimExecucao) {
        this.dataFimExecucao = dataFimExecucao;
    }

    public String getObservacaoGeral() {
        return observacaoGeral;
    }

    public void setObservacaoGeral(String observacaoGeral) {
        this.observacaoGeral = observacaoGeral;
    }

    public List<OrdemServicoChecklistAtivo> getChecklistAtivos() {
        return checklistAtivos;
    }

    public void setChecklistAtivos(List<OrdemServicoChecklistAtivo> checklistAtivos) {
        this.checklistAtivos = checklistAtivos;
    }

    public List<OrdemServicoTecnico> getTecnicos() {
        return tecnicos;
    }

    public void setTecnicos(List<OrdemServicoTecnico> tecnicos) {
        this.tecnicos = tecnicos;
    }

    public MaquinaHistoricoManutencao getHistoricoManutencao() {
        return historicoManutencao;
    }

    public void setHistoricoManutencao(MaquinaHistoricoManutencao historicoManutencao) {
        this.historicoManutencao = historicoManutencao;
    }
}
