package com.example.tracker.dto.contrato;

import com.example.tracker.entity.Contrato;
import java.time.LocalDate;
import java.util.List;
import com.example.tracker.dto.maquinacontrato.MaquinaContratoResponseDTO;

public class ContratoResponseDTO {

    private Integer codigo;
    private Integer codigoCliente;
    private String nomeCliente;
    private String emailContatoCliente;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String status;
    private Integer periodoManutencaoPreventiva;
    private Boolean conexaoInternet;
    private LocalDate vencimentoManutencaoPreventiva;
    private String descricao;

    private List<MaquinaContratoResponseDTO> maquinas;

    public static ContratoResponseDTO fromEntity(Contrato contrato) {
        ContratoResponseDTO dto = new ContratoResponseDTO();

        dto.setCodigo(contrato.getCodigo());
        if (contrato.getCliente() != null) {
            dto.setCodigoCliente(contrato.getCliente().getId());
            dto.setNomeCliente(contrato.getCliente().getNomeEmpresa());
            dto.setEmailContatoCliente(contrato.getCliente().getEmailContato());
        }
        dto.setDataInicio(contrato.getDataInicio());
        dto.setDataFim(contrato.getDataFim());
        dto.setStatus(contrato.getStatus());
        dto.setPeriodoManutencaoPreventiva(contrato.getPeriodoManutencaoPreventiva());
        dto.setConexaoInternet(contrato.getConexaoInternet());
        dto.setVencimentoManutencaoPreventiva(contrato.getVencimentoManutencaoPreventiva());
        dto.setDescricao(contrato.getDescricao());

        if (contrato.getMaquinas() != null) {
            dto.setMaquinas(
                contrato.getMaquinas()
                    .stream()
                    .map(MaquinaContratoResponseDTO::fromEntity)
                    .toList()
            );
        }

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

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEmailContatoCliente() {
        return emailContatoCliente;
    }

    public void setEmailContatoCliente(String emailContatoCliente) {
        this.emailContatoCliente = emailContatoCliente;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPeriodoManutencaoPreventiva() {
        return periodoManutencaoPreventiva;
    }

    public void setPeriodoManutencaoPreventiva(Integer periodoManutencaoPreventiva) {
        this.periodoManutencaoPreventiva = periodoManutencaoPreventiva;
    }

    public Boolean getConexaoInternet() {
        return conexaoInternet;
    }

    public void setConexaoInternet(Boolean conexaoInternet) {
        this.conexaoInternet = conexaoInternet;
    }

    public LocalDate getVencimentoManutencaoPreventiva() {
        return vencimentoManutencaoPreventiva;
    }

    public void setVencimentoManutencaoPreventiva(LocalDate vencimentoManutencaoPreventiva) {
        this.vencimentoManutencaoPreventiva = vencimentoManutencaoPreventiva;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public List<MaquinaContratoResponseDTO> getMaquinas() {
        return maquinas;
    }

    public void setMaquinas(List<MaquinaContratoResponseDTO> maquinas) {
        this.maquinas = maquinas;
    }
}
