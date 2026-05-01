package com.example.tracker.service;

import com.example.tracker.dto.CatalogoMaquinaChecklistPadrao.CatalogoMaquinaChecklistPadraoCreateDTO;
import com.example.tracker.entity.*;
import com.example.tracker.repository.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CatalogoMaquinaChecklistPadraoServiceImpl
        implements CatalogoMaquinaChecklistPadraoService {

    private final CatalogoMaquinaChecklistPadraoRepository repository;
    private final CatalogoMaquinaRepository maquinaRepository;
    private final CatalogoTarefaRepository tarefaRepository;

    public CatalogoMaquinaChecklistPadraoServiceImpl(
            CatalogoMaquinaChecklistPadraoRepository repository,
            CatalogoMaquinaRepository maquinaRepository,
            CatalogoTarefaRepository tarefaRepository) {
        this.repository = repository;
        this.maquinaRepository = maquinaRepository;
        this.tarefaRepository = tarefaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogoMaquinaChecklistPadrao> listarPorMaquina(Integer maquinaId) {
        Integer id = requireId(maquinaId, "Maquina obrigatoria");
        return repository.findByCatalogoMaquina_Codigo(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogoMaquinaChecklistPadrao buscarPorMaquinaETarefa(Integer maquinaId, Integer tarefaId) {

        Integer id = requireId(maquinaId, "Maquina obrigatoria");

        return repository.findByCatalogoMaquina_Codigo(id).stream()
                .filter(i -> i.getTarefa().getId().equals(tarefaId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item do checklist nao encontrado"));
    }

    @Override
    @Transactional
    public CatalogoMaquinaChecklistPadrao adicionar(Integer maquinaId,
            CatalogoMaquinaChecklistPadraoCreateDTO dto) {

        Integer id = requireId(maquinaId, "Maquina obrigatoria");

        CatalogoMaquina maquina = maquinaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Maquina nao encontrada"));

        CatalogoTarefa tarefa = tarefaRepository.findById(dto.getCodigoTarefa())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarefa nao encontrada"));

        repository.findByCatalogoMaquina_Codigo(id).stream()
                .filter(i -> i.getTarefa().getId().equals(tarefa.getId()))
                .findFirst()
                .ifPresent(i -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Tarefa ja existe no checklist da maquina");
                });

        CatalogoMaquinaChecklistPadrao item = new CatalogoMaquinaChecklistPadrao();
        item.setId(new CatalogoMaquinaChecklistPadraoId());
        item.setCatalogoMaquina(maquina);
        item.setTarefa(tarefa);

        return repository.save(item);
    }

    @Override
    @Transactional
    public CatalogoMaquinaChecklistPadrao atualizar(Integer maquinaId,
            Integer tarefaIdAtual,
            CatalogoMaquinaChecklistPadraoCreateDTO dto) {

        Integer id = requireId(maquinaId, "Maquina obrigatoria");

        CatalogoMaquinaChecklistPadrao item = repository
                .findByCatalogoMaquina_Codigo(id).stream()
                .filter(i -> i.getTarefa().getId().equals(tarefaIdAtual))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item do checklist nao encontrado"));

        if (dto.getCodigoTarefa() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "codigoTarefa e obrigatorio");
        }

        CatalogoTarefa novaTarefa = tarefaRepository.findById(dto.getCodigoTarefa())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tarefa nao encontrada"));

        boolean jaExiste = repository.findByCatalogoMaquina_Codigo(id).stream()
                .filter(i -> !i.getTarefa().getId().equals(tarefaIdAtual))
                .anyMatch(i -> i.getTarefa().getId().equals(novaTarefa.getId()));

        if (jaExiste) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tarefa ja existe no checklist da maquina");
        }

        item.setTarefa(novaTarefa);

        return repository.save(item);
    }

    @Override
    @Transactional
    public void remover(Integer maquinaId, Integer tarefaId) {

        Integer id = requireId(maquinaId, "Maquina obrigatoria");

        repository.deleteByCatalogoMaquina_CodigoAndTarefa_Id(id, tarefaId);
    }

    private Integer requireId(Integer id, String msg) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }
        return id;
    }
}