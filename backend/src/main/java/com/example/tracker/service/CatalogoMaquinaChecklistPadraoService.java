package com.example.tracker.service;

import com.example.tracker.dto.CatalogoMaquinaChecklistPadrao.CatalogoMaquinaChecklistPadraoCreateDTO;
import com.example.tracker.entity.CatalogoMaquinaChecklistPadrao;
import java.util.List;

public interface CatalogoMaquinaChecklistPadraoService {

    List<CatalogoMaquinaChecklistPadrao> listarPorMaquina(Integer maquinaId);

    CatalogoMaquinaChecklistPadrao adicionar(Integer maquinaId, CatalogoMaquinaChecklistPadraoCreateDTO dto);

    CatalogoMaquinaChecklistPadrao atualizar(Integer maquinaId,
            Integer tarefaIdAtual,
            CatalogoMaquinaChecklistPadraoCreateDTO dto);

    void remover(Integer maquinaId, Integer tarefaId);
    
    CatalogoMaquinaChecklistPadrao buscarPorMaquinaETarefa(Integer maquinaId, Integer tarefaId);
}