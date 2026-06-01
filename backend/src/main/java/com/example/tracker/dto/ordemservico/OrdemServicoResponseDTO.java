package com.example.tracker.dto.ordemservico;

import com.example.tracker.dto.CatalogoMaquinaChecklistPadrao.CatalogoMaquinaChecklistPadraoResponseDTO;
import com.example.tracker.entity.OrdemServico;
import com.example.tracker.entity.OrdemServicoTecnico;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class OrdemServicoResponseDTO {

    private Integer codigo;
    private Integer codigoCliente;
    private Integer codigoFuncionario;
    private List<Integer> codigosFuncionarios;
    private Integer codigoSoftwareInstalado;
    private Integer codigoContrato;
    private Integer codigoMaquinaContrato;
    private String status;
    private String criticidade;
    private String tipoOrdem;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFimExecucao;
    private String observacaoGeral;
    private Integer previsaoManutencao;
    private Integer quantidadeChecklistAtivos;
    private Integer codigoHistoricoManutencao;
    private List<OrdemServicoChecklistAtivoResponseDTO> checklistAtivos;
    private List<CatalogoMaquinaChecklistPadraoResponseDTO> checklistMaquina;

    public static OrdemServicoResponseDTO fromEntity(OrdemServico os) {
    OrdemServicoResponseDTO dto = new OrdemServicoResponseDTO();

    dto.setCodigo(os.getCodigo());

    if (os.getCliente() != null) {
        dto.setCodigoCliente(os.getCliente().getId());
    }

    if (os.getFuncionario() != null) {
        dto.setCodigoFuncionario(os.getFuncionario().getId());
    }

    List<Integer> codigosFuncionarios = os.getTecnicos() == null
            ? List.of()
            : os.getTecnicos().stream()
                    .map(OrdemServicoTecnico::getTecnico)
                    .filter(tecnico -> tecnico != null && tecnico.getId() != null)
                    .map(tecnico -> tecnico.getId())
                    .distinct()
                    .collect(Collectors.toList());
    if (codigosFuncionarios.isEmpty() && os.getFuncionario() != null) {
        codigosFuncionarios = List.of(os.getFuncionario().getId());
    }
    dto.setCodigosFuncionarios(codigosFuncionarios);

    if (os.getSoftwareInstalado() != null) {
        dto.setCodigoSoftwareInstalado(os.getSoftwareInstalado().getCodigo());
    }

    if (os.getContrato() != null) {
        dto.setCodigoContrato(os.getContrato().getCodigo());
    }

    if (os.getMaquinaContrato() != null) {
        dto.setCodigoMaquinaContrato(os.getMaquinaContrato().getCodigo());
    }

    dto.setStatus(os.getStatus());
    dto.setCriticidade(os.getCriticidade());
    dto.setTipoOrdem(os.getTipoOrdem());
    dto.setDataAbertura(os.getDataAbertura());
    dto.setDataAgendamento(os.getDataAgendamento());
    dto.setDataInicioExecucao(os.getDataInicioExecucao());
    dto.setDataFimExecucao(os.getDataFimExecucao());
    dto.setObservacaoGeral(os.getObservacaoGeral());
    dto.setPrevisaoManutencao(os.getPrevisaoManutencao());

    List<OrdemServicoChecklistAtivoResponseDTO> checklistAtivos = os.getChecklistAtivos() == null
            ? List.of()
            : os.getChecklistAtivos().stream()
                    .map(OrdemServicoChecklistAtivoResponseDTO::fromEntity)
                    .collect(Collectors.toList());

    dto.setChecklistAtivos(checklistAtivos);
    dto.setQuantidadeChecklistAtivos(checklistAtivos.size());

    if (os.getHistoricoManutencao() != null) {
        dto.setCodigoHistoricoManutencao(os.getHistoricoManutencao().getCodigo());
    }

    List<CatalogoMaquinaChecklistPadraoResponseDTO> checklistMaquina =
            (os.getMaquinaContrato() == null ||
             os.getMaquinaContrato().getCatalogoMaquina() == null ||
             os.getMaquinaContrato().getCatalogoMaquina().getChecklistPadrao() == null)
            ? List.of()
            : os.getMaquinaContrato()
                .getCatalogoMaquina()
                .getChecklistPadrao()
                .stream()
                .map(CatalogoMaquinaChecklistPadraoResponseDTO::fromEntity)
                .collect(Collectors.toList());

    dto.setChecklistMaquina(checklistMaquina);

    return dto;
}

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(Integer codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Integer getCodigoFuncionario() {
        return codigoFuncionario;
    }

    public void setCodigoFuncionario(Integer codigoFuncionario) {
        this.codigoFuncionario = codigoFuncionario;
    }

    public List<Integer> getCodigosFuncionarios() {
        return codigosFuncionarios;
    }

    public void setCodigosFuncionarios(List<Integer> codigosFuncionarios) {
        this.codigosFuncionarios = codigosFuncionarios;
    }

    public Integer getCodigoSoftwareInstalado() {
        return codigoSoftwareInstalado;
    }

    public void setCodigoSoftwareInstalado(Integer codigoSoftwareInstalado) {
        this.codigoSoftwareInstalado = codigoSoftwareInstalado;
    }

    public Integer getCodigoContrato() {
        return codigoContrato;
    }

    public void setCodigoContrato(Integer codigoContrato) {
        this.codigoContrato = codigoContrato;
    }

    public Integer getCodigoMaquinaContrato() {
        return codigoMaquinaContrato;
    }

    public void setCodigoMaquinaContrato(Integer codigoMaquinaContrato) {
        this.codigoMaquinaContrato = codigoMaquinaContrato;
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

    public Integer getPrevisaoManutencao() {
        return previsaoManutencao;
    }

    public void setPrevisaoManutencao(Integer previsaoManutencao) {
        this.previsaoManutencao = previsaoManutencao;
    }

    public Integer getQuantidadeChecklistAtivos() {
        return quantidadeChecklistAtivos;
    }

    public void setQuantidadeChecklistAtivos(Integer quantidadeChecklistAtivos) {
        this.quantidadeChecklistAtivos = quantidadeChecklistAtivos;
    }

    public Integer getCodigoHistoricoManutencao() {
        return codigoHistoricoManutencao;
    }

    public void setCodigoHistoricoManutencao(Integer codigoHistoricoManutencao) {
        this.codigoHistoricoManutencao = codigoHistoricoManutencao;
    }

    public List<OrdemServicoChecklistAtivoResponseDTO> getChecklistAtivos() {
        return checklistAtivos;
    }

    public void setChecklistAtivos(List<OrdemServicoChecklistAtivoResponseDTO> checklistAtivos) {
        this.checklistAtivos = checklistAtivos;
    }

    public List<CatalogoMaquinaChecklistPadraoResponseDTO> getChecklistMaquina() {
        return checklistMaquina;
    }

    public void setChecklistMaquina(List<CatalogoMaquinaChecklistPadraoResponseDTO> checklistMaquina) {
        this.checklistMaquina = checklistMaquina;
    }
}
