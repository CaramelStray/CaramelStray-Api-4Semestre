package com.example.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.tracker.entity.CatalogoMaquina;

public interface CatalogoMaquinaRepository extends JpaRepository<CatalogoMaquina, Integer> {

    boolean existsByDescricao(String descricao);
}