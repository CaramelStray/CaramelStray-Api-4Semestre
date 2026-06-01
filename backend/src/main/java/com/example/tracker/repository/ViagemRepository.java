package com.example.tracker.repository;

import com.example.tracker.entity.Viagem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ViagemRepository extends JpaRepository<Viagem, Integer> {

    List<Viagem> findByClienteId(Integer codigoCliente);

    List<Viagem> findByFuncionarioResponsavelId(Integer codigoFuncionarioResponsavel);

    List<Viagem> findByOrdemServicoCodigo(Integer codigoOrdemServico);

    List<Viagem> findByTipoViagemCodigo(Integer codigoTipoViagem);

    List<Viagem> findByStatus(String status);

    boolean existsByOrdemServicoCodigoAndFuncionarioResponsavelId(Integer codigoOrdemServico, Integer codigoFuncionario);

    @Query("""
        SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END
        FROM Viagem v
        WHERE v.funcionarioResponsavel.id = :codigoFuncionario
          AND v.dataRetornoReal IS NULL
          AND (v.status IS NULL OR UPPER(v.status) NOT IN ('FINALIZADA', 'CANCELADA'))
          AND (UPPER(v.status) = 'EM_ANDAMENTO' OR v.dataSaidaReal IS NOT NULL)
    """)
    boolean existsRotaAtivaPorTecnico(@Param("codigoFuncionario") Integer codigoFuncionario);
}
