package com.example.tracker.repository;

import com.example.tracker.entity.TecnicoAusencia;
import com.example.tracker.enums.StatusAusenciaTecnico;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TecnicoAusenciaRepository extends JpaRepository<TecnicoAusencia, Integer> {

    @Query("""
            select a
            from TecnicoAusencia a
            left join fetch a.tecnico t
            where (:codigoTecnico is null or t.id = :codigoTecnico)
              and (:status is null or a.status = :status)
              and (:dataInicio is null or a.dataFim >= :dataInicio)
              and (:dataFim is null or a.dataInicio <= :dataFim)
            order by a.dataInicio asc, a.codigo asc
            """)
    List<TecnicoAusencia> filtrar(
            @Param("codigoTecnico") Integer codigoTecnico,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("status") StatusAusenciaTecnico status);

    @Query("""
            select case when count(a) > 0 then true else false end
            from TecnicoAusencia a
            where a.tecnico.id = :codigoTecnico
              and a.status = :status
              and (:codigoAusencia is null or a.codigo <> :codigoAusencia)
              and a.dataInicio <= :dataFim
              and a.dataFim >= :dataInicio
            """)
    boolean existsSobreposicaoAtiva(
            @Param("codigoTecnico") Integer codigoTecnico,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("codigoAusencia") Integer codigoAusencia,
            @Param("status") StatusAusenciaTecnico status);

    @Query("""
            select case when count(a) > 0 then true else false end
            from TecnicoAusencia a
            where a.tecnico.id = :codigoTecnico
              and a.status = :status
              and a.dataInicio <= :data
              and a.dataFim >= :data
            """)
    boolean existsAusenciaAtivaNaData(
            @Param("codigoTecnico") Integer codigoTecnico,
            @Param("data") LocalDate data,
            @Param("status") StatusAusenciaTecnico status);
}
