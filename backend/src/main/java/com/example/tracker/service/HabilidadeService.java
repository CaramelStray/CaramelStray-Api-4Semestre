package com.example.tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tracker.entity.Habilidade;
import com.example.tracker.repository.HabilidadeRepository;

@Service
public class HabilidadeService {

    private final HabilidadeRepository repository;

    public HabilidadeService(HabilidadeRepository repository) {
        this.repository = repository;
    }

    public Habilidade buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habilidade não encontrada"));
    }

    public Habilidade buscarPorDescricao(String descricao) {
        return repository.findByDescricao(descricao)
                .orElseThrow(() -> new RuntimeException("Habilidade não encontrada"));
    }

    public List<Habilidade> listar() {
        return repository.findAll();
    }

    public Habilidade adicionar(Habilidade habilidade) {
        if (repository.existsByDescricao(habilidade.getDescricao())) {
            throw new RuntimeException("Habilidade já existe");
        }
        return repository.save(habilidade);
    }

    public Habilidade editar(Integer id, Habilidade nova) {
        Habilidade habilidade = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habilidade não encontrada"));

        habilidade.setDescricao(nova.getDescricao());
        habilidade.setObservacao(nova.getObservacao());

        return repository.save(habilidade);
    }

    public void remover(Integer id) {
        repository.deleteById(id);
    }

}