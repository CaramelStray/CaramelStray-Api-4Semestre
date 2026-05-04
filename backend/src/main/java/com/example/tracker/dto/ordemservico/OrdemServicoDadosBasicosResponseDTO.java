package com.example.tracker.dto.ordemservico;

import com.example.tracker.entity.CatalogoSoftware;
import com.example.tracker.entity.MaquinaSoftwareInstalado;
import com.example.tracker.entity.OrdemServico;

public class OrdemServicoDadosBasicosResponseDTO {

    private Integer codigoOrdemServico;
    private String nomeCliente;
    private String funcionario;
    private String softwareInstalado;
    private Integer numeroContrato;
    private String status;
    private String criticidade;
    private String tipoOrdem;

    public static OrdemServicoDadosBasicosResponseDTO fromEntity(OrdemServico ordemServico) {
        OrdemServicoDadosBasicosResponseDTO dto = new OrdemServicoDadosBasicosResponseDTO();

        dto.setCodigoOrdemServico(ordemServico.getCodigo());

        if (ordemServico.getCliente() != null) {
            dto.setNomeCliente(ordemServico.getCliente().getNomeEmpresa());
        }

        if (ordemServico.getFuncionario() != null) {
            dto.setFuncionario(ordemServico.getFuncionario().getNome());
        }

        dto.setSoftwareInstalado(formatarSoftwareInstalado(ordemServico.getSoftwareInstalado()));

        if (ordemServico.getContrato() != null) {
            dto.setNumeroContrato(ordemServico.getContrato().getCodigo());
        }

        dto.setStatus(ordemServico.getStatus());
        dto.setCriticidade(ordemServico.getCriticidade());
        dto.setTipoOrdem(ordemServico.getTipoOrdem());

        return dto;
    }

    private static String formatarSoftwareInstalado(MaquinaSoftwareInstalado softwareInstalado) {
        if (softwareInstalado == null || softwareInstalado.getSoftware() == null) {
            return null;
        }

        CatalogoSoftware software = softwareInstalado.getSoftware();
        String nome = software.getNome();
        String versaoInstalada = softwareInstalado.getVersaoInstalada();

        if (versaoInstalada == null || versaoInstalada.isBlank()) {
            return nome;
        }

        return nome + " " + versaoInstalada;
    }

    public Integer getCodigoOrdemServico() {
        return codigoOrdemServico;
    }

    public void setCodigoOrdemServico(Integer codigoOrdemServico) {
        this.codigoOrdemServico = codigoOrdemServico;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(String funcionario) {
        this.funcionario = funcionario;
    }

    public String getSoftwareInstalado() {
        return softwareInstalado;
    }

    public void setSoftwareInstalado(String softwareInstalado) {
        this.softwareInstalado = softwareInstalado;
    }

    public Integer getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(Integer numeroContrato) {
        this.numeroContrato = numeroContrato;
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
}
