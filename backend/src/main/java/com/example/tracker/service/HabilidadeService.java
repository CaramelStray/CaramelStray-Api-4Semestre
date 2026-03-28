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

    public List<Habilidade> listar() {
        return repository.findAll();
    }

    public Habilidade adicionar(Habilidade habilidade) {
        if (repository.existsByDescricao(habilidade.getDescricao())) {
            throw new RuntimeException("Habilidade já existe");
        }
        return repository.save(habilidade);
    }

    public Habilidade editar(Long id, Habilidade nova) {
        Habilidade habilidade = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habilidade não encontrada"));

        habilidade.setDescricao(nova.getDescricao());
        habilidade.setObservacao(nova.getObservacao());

        return repository.save(habilidade);
    }

    public void remover(Long id) {
        repository.deleteById(id);
    }

}