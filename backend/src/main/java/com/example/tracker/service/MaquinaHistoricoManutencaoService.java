package com.example.tracker.service;

import com.example.tracker.dto.maquinahistoricomanutencao.MaquinaHistoricoManutencaoCreateDTO;
import com.example.tracker.entity.MaquinaHistoricoManutencao;
import java.time.LocalDateTime;
import java.util.List;

public interface MaquinaHistoricoManutencaoService {

    List<MaquinaHistoricoManutencao> listarTodos();

    MaquinaHistoricoManutencao buscarPorId(Integer id);

    List<MaquinaHistoricoManutencao> buscarPorMaquinaContrato(Integer codigoMaquinaContrato);

    List<MaquinaHistoricoManutencao> buscarPorSoftwareInstalado(Integer codigoSoftwareInstalado);

    byte[] gerarRelatorioPdf(
            Integer codigoMaquinaContrato,
            Integer codigoSoftwareInstalado,
            Integer codigoTipoManutencao,
            String status,
            String criticidade,
            LocalDateTime vencimentoInicio,
            LocalDateTime vencimentoFim,
            Boolean somenteVencidas,
            Integer proximosDias);

    MaquinaHistoricoManutencao cadastrar(MaquinaHistoricoManutencaoCreateDTO dto);

    MaquinaHistoricoManutencao atualizar(Integer id, MaquinaHistoricoManutencaoCreateDTO dto);

    void deletar(Integer id);

    public List<Integer> listarFuncionarios(Integer codigoHistorico);
}
