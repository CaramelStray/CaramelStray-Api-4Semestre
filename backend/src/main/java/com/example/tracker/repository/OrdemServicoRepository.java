package com.example.tracker.repository;

import com.example.tracker.entity.OrdemServico;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Integer> {

    List<OrdemServico> findByClienteId(Integer codigoCliente);

    List<OrdemServico> findByFuncionarioId(Integer codigoFuncionario);

    List<OrdemServico> findBySoftwareInstaladoCodigo(Integer codigoSoftwareInstalado);

    List<OrdemServico> findByContratoCodigo(Integer codigoContrato);

    List<OrdemServico> findByMaquinaContratoCodigo(Integer codigoMaquinaContrato);

    @Query("""
        SELECT os FROM OrdemServico os
        LEFT JOIN FETCH os.cliente
        LEFT JOIN FETCH os.funcionario
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
