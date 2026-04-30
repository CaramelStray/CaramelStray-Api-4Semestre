package com.example.tracker.repository;

import com.example.tracker.entity.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AtivoRepository extends JpaRepository<Ativo, Integer> {

    List<Ativo> findByCatalogoAtivoCodigo(Integer codigoCatalogoAtivo);

    List<Ativo> findByFuncionarioResponsavelId(Integer codigoFuncionario);

    List<Ativo> findByStatus(String status);
}
