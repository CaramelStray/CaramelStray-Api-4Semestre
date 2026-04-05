package com.example.tracker.service;

import com.example.tracker.dto.ordemservico.OrdemServicoCreateDTO;
import com.example.tracker.entity.OrdemServico;
import java.util.List;

public interface OrdemServicoService {

    List<OrdemServico> listarTodos();

    OrdemServico buscarPorId(Integer id);

    List<OrdemServico> buscarPorCliente(Integer codigoCliente);

    List<OrdemServico> buscarPorFuncionario(Integer codigoFuncionario);

    List<OrdemServico> buscarPorSoftwareInstalado(Integer codigoSoftwareInstalado);

    List<OrdemServico> buscarPorContrato(Integer codigoContrato);

    List<OrdemServico> buscarPorMaquinaContrato(Integer codigoMaquinaContrato);

    OrdemServico cadastrar(OrdemServicoCreateDTO dto);

    OrdemServico atualizar(Integer id, OrdemServicoCreateDTO dto);

    void deletar(Integer id);
}