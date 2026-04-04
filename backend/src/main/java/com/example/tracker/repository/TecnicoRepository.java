package com.example.tracker.repository;

import com.example.tracker.entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface TecnicoRepository extends JpaRepository<Tecnico, Integer> {

    Optional<Tecnico> findByCpf(String cpf);

    List<Tecnico> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT t FROM Tecnico t LEFT JOIN FETCH t.habilidades h LEFT JOIN FETCH h.habilidade")
    List<Tecnico> findAllComHabilidades();

}