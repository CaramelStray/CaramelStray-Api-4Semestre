package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrdemServicoTecnicoId implements Serializable {

    @Column(name = "codigo_ordem_servico")
    private Integer codigoOrdemServico;

    @Column(name = "codigo_funcionario")
    private Integer codigoFuncionario;

    public OrdemServicoTecnicoId() {
    }

    public OrdemServicoTecnicoId(Integer codigoOrdemServico, Integer codigoFuncionario) {
        this.codigoOrdemServico = codigoOrdemServico;
        this.codigoFuncionario = codigoFuncionario;
    }

    public Integer getCodigoOrdemServico() {
        return codigoOrdemServico;
    }

    public void setCodigoOrdemServico(Integer codigoOrdemServico) {
        this.codigoOrdemServico = codigoOrdemServico;
    }

    public Integer getCodigoFuncionario() {
        return codigoFuncionario;
    }

    public void setCodigoFuncionario(Integer codigoFuncionario) {
        this.codigoFuncionario = codigoFuncionario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrdemServicoTecnicoId that)) {
            return false;
        }
        return Objects.equals(codigoOrdemServico, that.codigoOrdemServico)
                && Objects.equals(codigoFuncionario, that.codigoFuncionario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoOrdemServico, codigoFuncionario);
    }
}
