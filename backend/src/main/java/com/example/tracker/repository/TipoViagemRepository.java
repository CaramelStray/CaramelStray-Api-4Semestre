package com.example.tracker.repository;

import com.example.tracker.entity.TipoViagem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoViagemRepository extends JpaRepository<TipoViagem, Integer> {

    Optional<TipoViagem> findByDescricao(String descricao);

    List<TipoViagem> findByAtivoTrue();
}
