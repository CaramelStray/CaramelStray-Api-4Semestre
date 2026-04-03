package com.example.tracker.service;

import java.util.List;
import com.example.tracker.entity.CatalogoMaquina;

public interface CatalogoMaquinaService {

    CatalogoMaquina buscarPorId(Integer id);

    List<CatalogoMaquina> listarTodos();

    CatalogoMaquina adicionar(CatalogoMaquina maquina);

    CatalogoMaquina atualizar(Integer id, CatalogoMaquina maquina);

    void remover(Integer id);
}