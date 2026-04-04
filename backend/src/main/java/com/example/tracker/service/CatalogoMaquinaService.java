package com.example.tracker.service;

import com.example.tracker.dto.catalogo.CatalogoMaquinaCreateDTO;
import java.util.List;
import com.example.tracker.entity.CatalogoMaquina;

public interface CatalogoMaquinaService {

    CatalogoMaquina buscarPorId(Integer id);

    List<CatalogoMaquina> listarTodos();

    CatalogoMaquina adicionar(CatalogoMaquinaCreateDTO dto);

    CatalogoMaquina atualizar(Integer id, CatalogoMaquinaCreateDTO dto);

    void remover(Integer id);
}
