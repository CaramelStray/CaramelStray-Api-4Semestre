package com.example.tracker.repository;

import com.example.tracker.entity.MaquinaContrato;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaquinaContratoRepository extends JpaRepository<MaquinaContrato, Integer> {

    List<MaquinaContrato> findByCodigoContrato(Integer codigoContrato);

    List<MaquinaContrato> findByCodigoCatalogoMaquina(Integer codigoCatalogoMaquina);
}
