package com.example.tracker.repository;

import com.example.tracker.entity.MaquinaChecklistManutencao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaquinaChecklistManutencaoRepository extends JpaRepository<MaquinaChecklistManutencao, Integer> {

    List<MaquinaChecklistManutencao> findByHistoricoManutencaoCodigoOrderByCodigoAsc(Integer codigoHistoricoManutencao);

    boolean existsByHistoricoManutencaoCodigoAndTarefaId(
            Integer codigoHistoricoManutencao,
            Integer codigoTarefa);
}
