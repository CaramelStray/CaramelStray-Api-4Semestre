package com.example.tracker.dto.ordemservico;

import com.example.tracker.entity.CatalogoMaquina;
import com.example.tracker.entity.Contrato;
import com.example.tracker.entity.MaquinaContrato;
import com.example.tracker.entity.OrdemServico;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TecnicoOrdemDetalhesResponseDTO {

    private Integer codigo;
    private String status;
    private String criticidade;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFimExecucao;
    private String observacaoGeral;

    private String nomeCliente;
    private String nomeResponsavelCliente;
    private String emailCliente;
    private String telefoneCliente;
    private String cidadeCliente;
    private String estadoRegiaoCliente;
    private String paisCliente;
    private String fusoHorarioCliente;
    private Boolean internacionalCliente;

    private Integer codigoContrato;
    private Boolean conexaoInternet;

    private String descricaoMaquina;
    private String especificacaoMaquina;
    private String limiteManutencaoMaquina;
    private String numeroSerieMaquina;
    private Boolean trabalhoEmAltura;
    private String manutencaoFeitaMaquina;

    private List<OrdemServicoChecklistAtivoResponseDTO> checklistAtivos;

    public static TecnicoOrdemDetalhesResponseDTO fromEntity(OrdemServico os) {
        TecnicoOrdemDetalhesResponseDTO dto = new TecnicoOrdemDetalhesResponseDTO();

        dto.setCodigo(os.getCodigo());
        dto.setStatus(os.getStatus());
        dto.setCriticidade(os.getCriticidade());
        dto.setDataAbertura(os.getDataAbertura());
        dto.setDataAgendamento(os.getDataAgendamento());
        dto.setDataInicioExecucao(os.getDataInicioExecucao());
        dto.setDataFimExecucao(os.getDataFimExecucao());
        dto.setObservacaoGeral(os.getObservacaoGeral());

        if (os.getCliente() != null) {
            dto.setNomeCliente(os.getCliente().getNomeEmpresa());
            dto.setNomeResponsavelCliente(os.getCliente().getNomeResponsavel());
            dto.setEmailCliente(os.getCliente().getEmailContato());
            dto.setTelefoneCliente(os.getCliente().getTelefoneContato());
            dto.setCidadeCliente(os.getCliente().getCidade());
            dto.setEstadoRegiaoCliente(os.getCliente().getEstadoRegiao());
            dto.setPaisCliente(os.getCliente().getPais());
            dto.setFusoHorarioCliente(os.getCliente().getFusoHorario());
            dto.setInternacionalCliente(os.getCliente().getInternacional());
        }

        Contrato contrato = os.getContrato();
        if (contrato != null) {
            dto.setCodigoContrato(contrato.getCodigo());
            dto.setConexaoInternet(contrato.getConexaoInternet());
        }

        MaquinaContrato maquinaContrato = os.getMaquinaContrato();
        if (maquinaContrato != null) {
            dto.setNumeroSerieMaquina(maquinaContrato.getNumeroSerie());
            dto.setTrabalhoEmAltura(maquinaContrato.getTrabalhoEmAltura());
            dto.setManutencaoFeitaMaquina(maquinaContrato.getManutencaoFeita());

            CatalogoMaquina catalogo = maquinaContrato.getCatalogoMaquina();
            if (catalogo != null) {
                dto.setDescricaoMaquina(catalogo.getDescricao());
                dto.setEspecificacaoMaquina(catalogo.getEspecificacao());
                dto.setLimiteManutencaoMaquina(catalogo.getLimiteManutencao());
            }
        }

        List<OrdemServicoChecklistAtivoResponseDTO> checklist = os.getChecklistAtivos() == null
                ? List.of()
                : os.getChecklistAtivos().stream()
                        .map(OrdemServicoChecklistAtivoResponseDTO::fromEntity)
                        .collect(Collectors.toList());
        dto.setChecklistAtivos(checklist);

        return dto;
    }

    public Integer getCodigo() { return codigo; }
    public void setCodigo(Integer codigo) { this.codigo = codigo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCriticidade() { return criticidade; }
    public void setCriticidade(String criticidade) { this.criticidade = criticidade; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDateTime getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(LocalDateTime dataAgendamento) { this.dataAgendamento = dataAgendamento; }

    public LocalDateTime getDataInicioExecucao() { return dataInicioExecucao; }
    public void setDataInicioExecucao(LocalDateTime dataInicioExecucao) { this.dataInicioExecucao = dataInicioExecucao; }

    public LocalDateTime getDataFimExecucao() { return dataFimExecucao; }
    public void setDataFimExecucao(LocalDateTime dataFimExecucao) { this.dataFimExecucao = dataFimExecucao; }

    public String getObservacaoGeral() { return observacaoGeral; }
    public void setObservacaoGeral(String observacaoGeral) { this.observacaoGeral = observacaoGeral; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getNomeResponsavelCliente() { return nomeResponsavelCliente; }
    public void setNomeResponsavelCliente(String nomeResponsavelCliente) { this.nomeResponsavelCliente = nomeResponsavelCliente; }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public String getTelefoneCliente() { return telefoneCliente; }
    public void setTelefoneCliente(String telefoneCliente) { this.telefoneCliente = telefoneCliente; }

    public String getCidadeCliente() { return cidadeCliente; }
    public void setCidadeCliente(String cidadeCliente) { this.cidadeCliente = cidadeCliente; }

    public String getEstadoRegiaoCliente() { return estadoRegiaoCliente; }
    public void setEstadoRegiaoCliente(String estadoRegiaoCliente) { this.estadoRegiaoCliente = estadoRegiaoCliente; }

    public String getPaisCliente() { return paisCliente; }
    public void setPaisCliente(String paisCliente) { this.paisCliente = paisCliente; }

    public String getFusoHorarioCliente() { return fusoHorarioCliente; }
    public void setFusoHorarioCliente(String fusoHorarioCliente) { this.fusoHorarioCliente = fusoHorarioCliente; }

    public Boolean getInternacionalCliente() { return internacionalCliente; }
    public void setInternacionalCliente(Boolean internacionalCliente) { this.internacionalCliente = internacionalCliente; }

    public Integer getCodigoContrato() { return codigoContrato; }
    public void setCodigoContrato(Integer codigoContrato) { this.codigoContrato = codigoContrato; }

    public Boolean getConexaoInternet() { return conexaoInternet; }
    public void setConexaoInternet(Boolean conexaoInternet) { this.conexaoInternet = conexaoInternet; }

    public String getDescricaoMaquina() { return descricaoMaquina; }
    public void setDescricaoMaquina(String descricaoMaquina) { this.descricaoMaquina = descricaoMaquina; }

    public String getEspecificacaoMaquina() { return especificacaoMaquina; }
    public void setEspecificacaoMaquina(String especificacaoMaquina) { this.especificacaoMaquina = especificacaoMaquina; }

    public String getLimiteManutencaoMaquina() { return limiteManutencaoMaquina; }
    public void setLimiteManutencaoMaquina(String limiteManutencaoMaquina) { this.limiteManutencaoMaquina = limiteManutencaoMaquina; }

    public String getNumeroSerieMaquina() { return numeroSerieMaquina; }
    public void setNumeroSerieMaquina(String numeroSerieMaquina) { this.numeroSerieMaquina = numeroSerieMaquina; }

    public Boolean getTrabalhoEmAltura() { return trabalhoEmAltura; }
    public void setTrabalhoEmAltura(Boolean trabalhoEmAltura) { this.trabalhoEmAltura = trabalhoEmAltura; }

    public String getManutencaoFeitaMaquina() { return manutencaoFeitaMaquina; }
    public void setManutencaoFeitaMaquina(String manutencaoFeitaMaquina) { this.manutencaoFeitaMaquina = manutencaoFeitaMaquina; }

    public List<OrdemServicoChecklistAtivoResponseDTO> getChecklistAtivos() { return checklistAtivos; }
    public void setChecklistAtivos(List<OrdemServicoChecklistAtivoResponseDTO> checklistAtivos) { this.checklistAtivos = checklistAtivos; }
}
