package com.example.tracker.repository;

import com.example.tracker.entity.MaquinaSoftwareInstalado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaquinaSoftwareInstaladoRepository extends JpaRepository<MaquinaSoftwareInstalado, Integer> {

    List<MaquinaSoftwareInstalado> findByCodigoMaquinaContrato(Integer codigoMaquinaContrato);

    List<MaquinaSoftwareInstalado> findByCodigoSoftware(Integer codigoSoftware);
}
