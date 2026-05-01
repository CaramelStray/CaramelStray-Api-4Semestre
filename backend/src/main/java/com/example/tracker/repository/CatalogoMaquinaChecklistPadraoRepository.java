package com.example.tracker.repository;

import com.example.tracker.entity.CatalogoMaquinaChecklistPadrao;
import com.example.tracker.entity.CatalogoMaquinaChecklistPadraoId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogoMaquinaChecklistPadraoRepository
        extends JpaRepository<CatalogoMaquinaChecklistPadrao, CatalogoMaquinaChecklistPadraoId> {

    List<CatalogoMaquinaChecklistPadrao> findByCatalogoMaquina_Codigo(Integer maquinaId);

    void deleteByCatalogoMaquina_CodigoAndTarefa_Id(Integer maquinaId, Integer tarefaId);
}