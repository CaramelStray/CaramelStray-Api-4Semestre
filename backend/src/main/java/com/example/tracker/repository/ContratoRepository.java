package com.example.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tracker.entity.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    List<Contrato> findByClienteId(Integer clienteId);

    boolean existsByClienteId(Integer clienteId);
}