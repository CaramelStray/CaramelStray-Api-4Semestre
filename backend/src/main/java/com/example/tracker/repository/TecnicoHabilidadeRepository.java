package com.example.tracker.repository;

import com.example.tracker.entity.TecnicoHabilidade;
import com.example.tracker.entity.TecnicoHabilidadeId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TecnicoHabilidadeRepository extends JpaRepository<TecnicoHabilidade, TecnicoHabilidadeId> {

    List<TecnicoHabilidade> findByTecnicoId(Integer tecnicoId);

    List<TecnicoHabilidade> findByHabilidadeCodigo(Integer habilidadeId);
}