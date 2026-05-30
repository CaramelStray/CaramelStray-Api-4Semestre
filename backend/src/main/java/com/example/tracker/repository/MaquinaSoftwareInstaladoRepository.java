package com.example.tracker.repository;

import com.example.tracker.entity.MaquinaSoftwareInstalado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MaquinaSoftwareInstaladoRepository extends JpaRepository<MaquinaSoftwareInstalado, Integer> {

    List<MaquinaSoftwareInstalado> findByMaquinaContratoCodigo(Integer codigoMaquinaContrato);

    List<MaquinaSoftwareInstalado> findBySoftwareId(Integer codigoSoftware);

    @Query("""
        SELECT msi FROM MaquinaSoftwareInstalado msi
        JOIN FETCH msi.software
        JOIN FETCH msi.maquinaContrato mc
        WHERE mc.latitude IS NOT NULL
          AND mc.longitude IS NOT NULL
    """)
    List<MaquinaSoftwareInstalado> findAllComLocalizacao();
}
