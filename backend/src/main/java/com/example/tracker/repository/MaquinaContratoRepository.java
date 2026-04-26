package com.example.tracker.repository;

import com.example.tracker.entity.MaquinaContrato;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MaquinaContratoRepository extends JpaRepository<MaquinaContrato, Integer> {

    List<MaquinaContrato> findByContratoCodigo(Integer codigoContrato);

    List<MaquinaContrato> findByCatalogoMaquinaCodigo(Integer codigoCatalogoMaquina);

    @Query("""
                SELECT DISTINCT m FROM MaquinaContrato m
                LEFT JOIN FETCH m.softwaresInstalados s
                LEFT JOIN FETCH s.software
                WHERE m.codigo = :codigo
            """)
    Optional<MaquinaContrato> findByIdWithSoftwares(Integer codigo);

    @Query("""
                SELECT DISTINCT m FROM MaquinaContrato m
                LEFT JOIN FETCH m.softwaresInstalados s
                LEFT JOIN FETCH s.software
                WHERE m.contrato.codigo = :codigoContrato
            """)
    List<MaquinaContrato> findByContratoCodigoWithSoftwares(Integer codigoContrato);

    @Query("""
                SELECT DISTINCT m FROM MaquinaContrato m
                LEFT JOIN FETCH m.softwaresInstalados s
                LEFT JOIN FETCH s.software
            """)
    List<MaquinaContrato> findAllWithSoftwares();
}
