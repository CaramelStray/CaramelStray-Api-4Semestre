package com.example.tracker.service;

import java.util.List;
import com.example.tracker.entity.Habilidade;

public interface HabilidadeService {

    Habilidade buscarPorId(Integer id);

    Habilidade buscarPorDescricao(String descricao);

    List<Habilidade> listarTodas();

    Habilidade adicionar(Habilidade habilidade);

    Habilidade atualizar(Integer id, Habilidade nova);

    void remover(Integer id);
}