package com.example.tracker.repository;

import com.example.tracker.entity.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtivoRepository extends JpaRepository<Ativo, Integer> {
}
