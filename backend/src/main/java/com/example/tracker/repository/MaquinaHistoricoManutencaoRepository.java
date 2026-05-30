package com.example.tracker.repository;

import com.example.tracker.entity.MaquinaHistoricoManutencao;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaquinaHistoricoManutencaoRepository extends JpaRepository<MaquinaHistoricoManutencao, Integer> {

    List<MaquinaHistoricoManutencao> findByMaquinaContratoCodigo(Integer codigoMaquinaContrato);

    List<MaquinaHistoricoManutencao> findBySoftwareInstaladoCodigo(Integer codigoSoftwareInstalado);

    Optional<MaquinaHistoricoManutencao> findByOrdemServicoCodigo(Integer codigoOrdemServico);

    @Query("""
            select h
            from MaquinaHistoricoManutencao h
            left join fetch h.maquinaContrato mc
            left join fetch mc.catalogoMaquina
            left join fetch h.softwareInstalado si
            left join fetch si.software
            left join fetch h.tipoManutencao
            where (:codigoMaquinaContrato is null or mc.codigo = :codigoMaquinaContrato)
              and (:codigoSoftwareInstalado is null or si.codigo = :codigoSoftwareInstalado)
              and (:codigoTipoManutencao is null or h.tipoManutencao.codigo = :codigoTipoManutencao)
              and (:status is null or lower(h.status) = :status)
              and (:criticidade is null or lower(h.criticidade) = :criticidade)
              and (:vencimentoInicio is null or h.vencimento >= :vencimentoInicio)
              and (:vencimentoFim is null or h.vencimento <= :vencimentoFim)
            order by h.vencimento asc, h.codigo asc
            """)
    List<MaquinaHistoricoManutencao> filtrarParaRelatorio(
            @Param("codigoMaquinaContrato") Integer codigoMaquinaContrato,
            @Param("codigoSoftwareInstalado") Integer codigoSoftwareInstalado,
            @Param("codigoTipoManutencao") Integer codigoTipoManutencao,
            @Param("status") String status,
            @Param("criticidade") String criticidade,
            @Param("vencimentoInicio") LocalDateTime vencimentoInicio,
            @Param("vencimentoFim") LocalDateTime vencimentoFim);

    @Query(
            value = """
                    select case when count(*) > 0 then true else false end
                    from tb_srv_maquina_funcionario_manutencao
                    where codigo_historico_manutencao = :codigoHistorico
                      and codigo_funcionario = :codigoFuncionario
                    """,
            nativeQuery = true)
    boolean existsTecnicoVinculado(
            @Param("codigoHistorico") Integer codigoHistorico,
            @Param("codigoFuncionario") Integer codigoFuncionario);

    @Query(
            value = """
                    select case when count(*) > 0 then true else false end
                    from tb_srv_maquina_historico_manutencao h
                    join tb_srv_maquina_funcionario_manutencao f
                      on f.codigo_historico_manutencao = h.codigo
                    where f.codigo_funcionario = :codigoFuncionario
                      and h.data_fim_execucao is null
                      and (h.status is null or upper(h.status) not in ('FINALIZADA', 'FINALIZADO', 'CONCLUIDA', 'CONCLUIDO', 'CANCELADA', 'CANCELADO'))
                      and (upper(h.status) = 'EM_EXECUCAO' or h.data_inicio_execucao is not null)
                    """,
            nativeQuery = true)
    boolean existsHistoricoAtivoPorTecnico(@Param("codigoFuncionario") Integer codigoFuncionario);
}
