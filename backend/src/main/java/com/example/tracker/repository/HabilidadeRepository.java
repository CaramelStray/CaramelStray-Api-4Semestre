package com.example.tracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tracker.entity.Habilidade;

public interface HabilidadeRepository extends JpaRepository<Habilidade, Long> {

    Optional<Habilidade> findByDescricao(String descricao);

    boolean existsByDescricao(String descricao);

}