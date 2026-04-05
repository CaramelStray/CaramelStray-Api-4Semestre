package com.example.tracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tracker.entity.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    @EntityGraph(attributePaths = "cliente")
    List<Contrato> findAllByOrderByCodigoDesc();

    @EntityGraph(attributePaths = "cliente")
    Optional<Contrato> findWithClienteByCodigo(Integer codigo);

    @EntityGraph(attributePaths = "cliente")
    List<Contrato> findByClienteId(Integer clienteId);

    boolean existsByClienteId(Integer clienteId);
}
