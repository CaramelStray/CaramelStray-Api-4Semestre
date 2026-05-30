package com.example.tracker.repository;

import com.example.tracker.entity.OrdemServicoChecklistAtivo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemServicoChecklistAtivoRepository extends JpaRepository<OrdemServicoChecklistAtivo, Integer> {

    List<OrdemServicoChecklistAtivo> findByOrdemServicoCodigoOrderByCodigoAsc(Integer codigoOrdemServico);

    Optional<OrdemServicoChecklistAtivo> findByCodigoAndOrdemServicoCodigo(
            Integer codigo,
            Integer codigoOrdemServico);

    Optional<OrdemServicoChecklistAtivo> findByOrdemServicoCodigoAndAtivoCodigo(
            Integer codigoOrdemServico,
            Integer codigoAtivo);

    boolean existsByOrdemServicoCodigoAndLevadoFalse(Integer codigoOrdemServico);

    boolean existsByOrdemServicoCodigoAndLevadoTrueAndDevolvidoFalse(Integer codigoOrdemServico);

    void deleteByOrdemServicoCodigo(Integer codigoOrdemServico);
}
