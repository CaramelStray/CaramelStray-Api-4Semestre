package com.example.tracker.repository;

import com.example.tracker.entity.CatalogoSoftware;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CatalogoSoftwareRepository
        extends JpaRepository<CatalogoSoftware, Integer>, JpaSpecificationExecutor<CatalogoSoftware> {

    Optional<CatalogoSoftware> findByNomeIgnoreCaseAndVersaoIgnoreCase(String nome, String versao);
}
