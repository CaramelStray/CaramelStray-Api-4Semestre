package com.example.tracker.service;

import com.example.tracker.dto.maquinahistoricomanutencao.MaquinaHistoricoManutencaoCreateDTO;
import com.example.tracker.entity.MaquinaHistoricoManutencao;
import java.util.List;

public interface MaquinaHistoricoManutencaoService {

    List<MaquinaHistoricoManutencao> listarTodos();

    MaquinaHistoricoManutencao buscarPorId(Integer id);

    List<MaquinaHistoricoManutencao> buscarPorMaquinaContrato(Integer codigoMaquinaContrato);

    List<MaquinaHistoricoManutencao> buscarPorSoftwareInstalado(Integer codigoSoftwareInstalado);

    MaquinaHistoricoManutencao cadastrar(MaquinaHistoricoManutencaoCreateDTO dto);

    MaquinaHistoricoManutencao atualizar(Integer id, MaquinaHistoricoManutencaoCreateDTO dto);

    void deletar(Integer id);
}
