package com.example.tracker.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.tracker.entity.CatalogoMaquina;
import com.example.tracker.repository.CatalogoMaquinaRepository;

@Service
public class CatalogoMaquinaServiceImpl implements CatalogoMaquinaService {

    private final CatalogoMaquinaRepository repository;

    public CatalogoMaquinaServiceImpl(CatalogoMaquinaRepository repository) {
        this.repository = repository;
    }

    @Override
    public CatalogoMaquina buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Máquina não encontrada"));
    }

    @Override
    public List<CatalogoMaquina> listarTodos() {
        return repository.findAll();
    }

    @Override
    public CatalogoMaquina adicionar(CatalogoMaquina maquina) {
        return repository.save(maquina);
    }

    @Override
    public CatalogoMaquina atualizar(Integer id, CatalogoMaquina nova) {
        CatalogoMaquina maquina = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Máquina não encontrada"));

        maquina.setDescricao(nova.getDescricao());
        maquina.setEspecificacao(nova.getEspecificacao());
        maquina.setLimiteManutencao(nova.getLimiteManutencao());

        return repository.save(maquina);
    }

    @Override
    public void remover(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Máquina não encontrada");
        }
        repository.deleteById(id);
    }
}