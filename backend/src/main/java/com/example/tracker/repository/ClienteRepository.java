package com.example.tracker.repository;

import com.example.tracker.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByEmailContato(String emailContato);
    Optional<Cliente> findByDocumento(String documento);
}
