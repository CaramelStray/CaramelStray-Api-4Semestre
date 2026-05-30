package com.example.tracker.service;

import com.example.tracker.dto.dashboard.DashboardCardDTO;
import com.example.tracker.dto.maquinachecklistmanutencao.MaquinaChecklistManutencaoResponseDTO;
import com.example.tracker.dto.ordemservico.TecnicosOrdensResponseDTO;
import com.example.tracker.dto.ordemservico.OrdemServicoCreateDTO;
import com.example.tracker.dto.ordemservico.OrdemServicoDadosBasicosResponseDTO;
import com.example.tracker.dto.ordemservico.OrdemServicoResponseDTO;
import com.example.tracker.entity.OrdemServico;
import java.time.LocalDate;
import java.util.List;

public interface OrdemServicoService {

    List<OrdemServico> listarTodos();

    List<OrdemServico> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim);

    List<TecnicosOrdensResponseDTO> buscarMinhasOrdens(String emailUsuario);

    OrdemServico buscarTecnicoOrdem(Integer id, String emailUsuario);

    List<OrdemServicoDadosBasicosResponseDTO> listarDadosBasicos();

    OrdemServico buscarPorId(Integer id);

    OrdemServicoDadosBasicosResponseDTO buscarDadosBasicosPorId(Integer id);

    OrdemServicoResponseDTO buscarCompletoPorId(Integer id);

    List<OrdemServico> buscarPorCliente(Integer codigoCliente);

    List<OrdemServico> buscarPorFuncionario(Integer codigoFuncionario);

    List<OrdemServico> buscarPorSoftwareInstalado(Integer codigoSoftwareInstalado);

    List<OrdemServico> buscarPorContrato(Integer codigoContrato);

    List<OrdemServico> buscarPorMaquinaContrato(Integer codigoMaquinaContrato);

    List<DashboardCardDTO> obterDashboardOrdens();

    OrdemServico cadastrar(OrdemServicoCreateDTO dto);

    OrdemServico atualizar(Integer id, OrdemServicoCreateDTO dto);

    OrdemServico atualizarStatusTecnico(Integer id, String novoStatus, String emailUsuario);

    void deletar(Integer id);

    List<MaquinaChecklistManutencaoResponseDTO> listarChecklistMaquina(Integer id);

    
    List<OrdemServico> buscarPorStatus(String status);

}