package com.example.tracker.repository;

import com.example.tracker.entity.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissaoRepository extends JpaRepository<Permissao, Integer> {
}