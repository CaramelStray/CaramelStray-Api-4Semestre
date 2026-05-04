package com.example.tracker.service;

import com.example.tracker.dto.ordemservico.OrdemServicoChecklistAtivoCheckinDTO;
import com.example.tracker.dto.ordemservico.OrdemServicoChecklistAtivoCreateDTO;
import com.example.tracker.entity.OrdemServicoChecklistAtivo;
import java.util.List;

public interface OrdemServicoChecklistAtivoService {

    List<OrdemServicoChecklistAtivo> listarPorOrdemServico(Integer codigoOrdemServico);

    List<OrdemServicoChecklistAtivo> listarItensIntervencaoTecnico(Integer codigoOrdemServico, String emailUsuario);

    OrdemServicoChecklistAtivo buscarItem(Integer codigoOrdemServico, Integer codigoItem);

    OrdemServicoChecklistAtivo adicionar(Integer codigoOrdemServico, OrdemServicoChecklistAtivoCreateDTO dto);

    List<OrdemServicoChecklistAtivo> substituirChecklist(
            Integer codigoOrdemServico,
            List<OrdemServicoChecklistAtivoCreateDTO> itens);

    OrdemServicoChecklistAtivo atualizar(
            Integer codigoOrdemServico,
            Integer codigoItem,
            OrdemServicoChecklistAtivoCreateDTO dto);

    OrdemServicoChecklistAtivo registrarCheckinTecnico(
            Integer codigoOrdemServico,
            Integer codigoItem,
            OrdemServicoChecklistAtivoCheckinDTO dto,
            String emailUsuario);

    OrdemServicoChecklistAtivo marcarLevado(Integer codigoOrdemServico, Integer codigoItem, String emailUsuario);

    OrdemServicoChecklistAtivo desmarcarLevado(Integer codigoOrdemServico, Integer codigoItem, String emailUsuario);

    OrdemServicoChecklistAtivo marcarDevolvido(Integer codigoOrdemServico, Integer codigoItem, String emailUsuario);

    OrdemServicoChecklistAtivo desmarcarDevolvido(Integer codigoOrdemServico, Integer codigoItem, String emailUsuario);

    void remover(Integer codigoOrdemServico, Integer codigoItem);
}
