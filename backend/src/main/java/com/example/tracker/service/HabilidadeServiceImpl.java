package com.example.tracker.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.tracker.entity.Habilidade;
import com.example.tracker.repository.HabilidadeRepository;

@Service
public class HabilidadeServiceImpl implements HabilidadeService {

    private final HabilidadeRepository repository;

    public HabilidadeServiceImpl(HabilidadeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Habilidade buscarPorId(Integer id) {
        Integer idNaoNulo = requireId(id, "Id da habilidade e obrigatorio");
        return repository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habilidade não encontrada"));
    }

    @Override
    public Habilidade buscarPorDescricao(String descricao) {
        return repository.findByDescricao(descricao)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habilidade não encontrada"));
    }

    @Override
    public List<Habilidade> listarTodas() {
        return repository.findAll();
    }

    @Override
    public Habilidade adicionar(Habilidade habilidade) {
        if (habilidade == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da habilidade sao obrigatorios");
        }
        if (repository.existsByDescricao(habilidade.getDescricao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Habilidade já existe");
        }
        return repository.save(Objects.requireNonNull(habilidade));
    }

    @Override
    public Habilidade atualizar(Integer id, Habilidade nova) {
        Integer idNaoNulo = requireId(id, "Id da habilidade e obrigatorio");
        if (nova == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da habilidade sao obrigatorios");
        }
        Habilidade habilidade = repository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habilidade não encontrada"));

        if (repository.existsByDescricao(nova.getDescricao()) &&
                !habilidade.getDescricao().equals(nova.getDescricao())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Habilidade já existe");
        }

        habilidade.setDescricao(nova.getDescricao());
        habilidade.setObservacao(nova.getObservacao());

        return repository.save(habilidade);
    }

    @Override
    public void remover(Integer id) {
        Integer idNaoNulo = requireId(id, "Id da habilidade e obrigatorio");
        if (!repository.existsById(Objects.requireNonNull(idNaoNulo))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Habilidade não encontrada");
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
