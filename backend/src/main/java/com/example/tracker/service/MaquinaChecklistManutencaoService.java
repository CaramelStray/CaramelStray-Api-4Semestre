package com.example.tracker.service;

import com.example.tracker.dto.maquinachecklistmanutencao.MaquinaChecklistManutencaoUpdateDTO;
import com.example.tracker.entity.MaquinaChecklistManutencao;
import java.util.List;

public interface MaquinaChecklistManutencaoService {

    List<MaquinaChecklistManutencao> listarPorHistorico(Integer codigoHistoricoManutencao);

    List<MaquinaChecklistManutencao> gerarChecklistPadrao(Integer codigoHistoricoManutencao);

    MaquinaChecklistManutencao atualizarItem(
            Integer codigoHistoricoManutencao,
            Integer codigoChecklist,
            MaquinaChecklistManutencaoUpdateDTO dto);
}
