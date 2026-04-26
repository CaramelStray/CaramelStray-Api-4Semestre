package com.example.tracker.service;

import com.example.tracker.dto.ordemservico.OrdemServicoChecklistAtivoCreateDTO;
import com.example.tracker.entity.OrdemServicoChecklistAtivo;
import java.util.List;

public interface OrdemServicoChecklistAtivoService {

    List<OrdemServicoChecklistAtivo> listarPorOrdemServico(Integer codigoOrdemServico);

    OrdemServicoChecklistAtivo buscarItem(Integer codigoOrdemServico, Integer codigoItem);

    OrdemServicoChecklistAtivo adicionar(Integer codigoOrdemServico, OrdemServicoChecklistAtivoCreateDTO dto);

    List<OrdemServicoChecklistAtivo> substituirChecklist(
            Integer codigoOrdemServico,
            List<OrdemServicoChecklistAtivoCreateDTO> itens);

    OrdemServicoChecklistAtivo atualizar(
            Integer codigoOrdemServico,
            Integer codigoItem,
            OrdemServicoChecklistAtivoCreateDTO dto);

    void remover(Integer codigoOrdemServico, Integer codigoItem);
}
