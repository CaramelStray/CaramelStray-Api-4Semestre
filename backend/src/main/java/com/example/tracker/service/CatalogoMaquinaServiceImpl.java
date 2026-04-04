package com.example.tracker.service;

import java.util.List;
import java.util.Objects;

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
        Integer idNaoNulo = requireId(id, "Id da maquina e obrigatorio");
        return repository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Máquina não encontrada"));
    }

    @Override
    public List<CatalogoMaquina> listarTodos() {
        return repository.findAll();
    }

    @Override
    public CatalogoMaquina adicionar(CatalogoMaquina maquina) {
        if (maquina == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da maquina sao obrigatorios");
        }
        return repository.save(Objects.requireNonNull(maquina));
    }

    @Override
    public CatalogoMaquina atualizar(Integer id, CatalogoMaquina nova) {
        Integer idNaoNulo = requireId(id, "Id da maquina e obrigatorio");
        if (nova == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da maquina sao obrigatorios");
        }
        CatalogoMaquina maquina = repository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Máquina não encontrada"));

        maquina.setDescricao(nova.getDescricao());
        maquina.setEspecificacao(nova.getEspecificacao());
        maquina.setLimiteManutencao(nova.getLimiteManutencao());

        return repository.save(maquina);
    }

    @Override
    public void remover(Integer id) {
        Integer idNaoNulo = requireId(id, "Id da maquina e obrigatorio");
        if (!repository.existsById(Objects.requireNonNull(idNaoNulo))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Máquina não encontrada");
        }
        repository.deleteById(Objects.requireNonNull(idNaoNulo));
    }

    private Integer requireId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return id;
    }
}
