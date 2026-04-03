package com.example.tracker.repository;

import com.example.tracker.entity.CatalogoTarefa;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogoTarefaRepository extends JpaRepository<CatalogoTarefa, Integer> {

    Optional<CatalogoTarefa> findByDescricaoIgnoreCaseAndCategoriaIgnoreCase(String descricao, String categoria);
}
