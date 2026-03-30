package com.example.tracker.repository;

import com.example.tracker.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer>, JpaSpecificationExecutor<Cliente> {
    Optional<Cliente> findByEmailContato(String emailContato);
    Optional<Cliente> findByDocumento(String documento);
}
