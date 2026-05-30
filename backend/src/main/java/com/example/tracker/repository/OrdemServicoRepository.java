package com.example.tracker.repository;

import com.example.tracker.entity.OrdemServico;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Integer> {

    List<OrdemServico> findByClienteId(Integer codigoCliente);

    List<OrdemServico> findByFuncionarioId(Integer codigoFuncionario);

    @Query("""
        select distinct os
        from OrdemServico os
        left join os.tecnicos ost
        where os.funcionario.id = :codigoFuncionario
           or ost.tecnico.id = :codigoFuncionario
    """)
    List<OrdemServico> findByFuncionarioParticipanteId(@Param("codigoFuncionario") Integer codigoFuncionario);

    List<OrdemServico> findBySoftwareInstaladoCodigo(Integer codigoSoftwareInstalado);

    List<OrdemServico> findByContratoCodigo(Integer codigoContrato);

    List<OrdemServico> findByMaquinaContratoCodigo(Integer codigoMaquinaContrato);

    List<OrdemServico> findByStatus(String status);

    
    long countByStatus(String status);

    long countByStatusIn(List<String> statuses);
    List<OrdemServico> findByDataAberturaBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
    List<OrdemServico> findByDataAgendamentoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
    List<OrdemServico> findByStatus(String status);
    boolean existsByFuncionarioIdAndDataAgendamentoAndCodigoNotAndStatusNotIn(
            Integer codigoFuncionario,
            LocalDateTime dataAgendamento,
            Integer codigo,
            List<String> status);

    @Query("""
        select case when count(distinct os.codigo) > 0 then true else false end
        from OrdemServico os
        left join os.tecnicos ost
        where (os.funcionario.id = :codigoFuncionario or ost.tecnico.id = :codigoFuncionario)
          and os.dataAgendamento = :dataAgendamento
          and os.codigo <> :codigo
          and os.status not in :status
    """)
    boolean existsByFuncionarioParticipanteIdAndDataAgendamentoAndCodigoNotAndStatusNotIn(
            @Param("codigoFuncionario") Integer codigoFuncionario,
            @Param("dataAgendamento") LocalDateTime dataAgendamento,
            @Param("codigo") Integer codigo,
            @Param("status") List<String> status);

    boolean existsByFuncionarioIdAndStatusInAndCodigoNot(
            Integer codigoFuncionario,
            List<String> status,
            Integer codigo);

    @Query("""
        select case when count(distinct os.codigo) > 0 then true else false end
        from OrdemServico os
        left join os.tecnicos ost
        where (os.funcionario.id = :codigoFuncionario or ost.tecnico.id = :codigoFuncionario)
          and os.status in :status
          and os.codigo <> :codigo
    """)
    boolean existsByFuncionarioParticipanteIdAndStatusInAndCodigoNot(
            @Param("codigoFuncionario") Integer codigoFuncionario,
            @Param("status") List<String> status,
            @Param("codigo") Integer codigo);

    @Query("""
        SELECT os FROM OrdemServico os
        LEFT JOIN FETCH os.cliente
        LEFT JOIN FETCH os.funcionario
        LEFT JOIN FETCH os.tecnicos ost
        LEFT JOIN FETCH ost.tecnico
        LEFT JOIN FETCH os.softwareInstalado
        LEFT JOIN FETCH os.contrato
        LEFT JOIN FETCH os.maquinaContrato mc
        LEFT JOIN FETCH mc.catalogoMaquina
        WHERE os.codigo = :id
    """)
    Optional<OrdemServico> findByIdCompleto(Integer id);

    @Query("""
        SELECT os FROM OrdemServico os
        JOIN FETCH os.softwareInstalado si
        WHERE si.codigo IN :codigosSoftwareInstalado
          AND (
              os.status IS NULL OR UPPER(os.status) NOT IN (
                  'FINALIZADA',
                  'FINALIZADO',
                  'CONCLUIDA',
                  'CONCLUIDO',
                  'CANCELADA',
                  'CANCELADO'
              )
          )
    """)
    List<OrdemServico> findOrdensAbertasPorSoftwaresInstalados(
            @Param("codigosSoftwareInstalado") List<Integer> codigosSoftwareInstalado);
}
