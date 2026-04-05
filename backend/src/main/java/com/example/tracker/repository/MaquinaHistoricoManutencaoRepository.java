package com.example.tracker.repository;

import com.example.tracker.entity.MaquinaHistoricoManutencao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaquinaHistoricoManutencaoRepository extends JpaRepository<MaquinaHistoricoManutencao, Integer> {

    List<MaquinaHistoricoManutencao> findByMaquinaContratoCodigo(Integer codigoMaquinaContrato);

    List<MaquinaHistoricoManutencao> findBySoftwareInstaladoCodigo(Integer codigoSoftwareInstalado);
}
