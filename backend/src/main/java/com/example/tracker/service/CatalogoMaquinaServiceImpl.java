package com.example.tracker.service;

import com.example.tracker.dto.catalogo.CatalogoMaquinaCreateDTO;
import com.example.tracker.dto.catalogo.MaquinaChecklistItemCreateDTO;
import com.example.tracker.entity.CatalogoMaquinaChecklistPadrao;
import com.example.tracker.entity.CatalogoMaquinaChecklistPadraoId;
import com.example.tracker.entity.CatalogoTarefa;
import com.example.tracker.repository.CatalogoTarefaRepository;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.tracker.entity.CatalogoMaquina;
import com.example.tracker.repository.CatalogoMaquinaRepository;

@Service
public class CatalogoMaquinaServiceImpl implements CatalogoMaquinaService {

    private static final String CATEGORIA_TAREFA_MAQUINA = "MAQUINA";

    private final CatalogoMaquinaRepository repository;
    private final CatalogoTarefaRepository catalogoTarefaRepository;

    public CatalogoMaquinaServiceImpl(
            CatalogoMaquinaRepository repository,
            CatalogoTarefaRepository catalogoTarefaRepository) {
        this.repository = repository;
        this.catalogoTarefaRepository = catalogoTarefaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogoMaquina buscarPorId(Integer id) {
        Integer idNaoNulo = requireId(id, "Id da maquina e obrigatorio");
        return repository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Máquina não encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogoMaquina> listarTodos() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public CatalogoMaquina adicionar(CatalogoMaquinaCreateDTO dto) {
        validarEntrada(dto);
        CatalogoMaquina maquina = new CatalogoMaquina();
        aplicarCampos(maquina, dto);
        return repository.save(Objects.requireNonNull(maquina));
    }

    @Override
    @Transactional
    public CatalogoMaquina atualizar(Integer id, CatalogoMaquinaCreateDTO dto) {
        Integer idNaoNulo = requireId(id, "Id da maquina e obrigatorio");
        validarEntrada(dto);

        CatalogoMaquina maquina = repository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Máquina não encontrada"));

        aplicarCampos(maquina, dto);

        return repository.save(maquina);
    }

    @Override
    @Transactional
    public void remover(Integer id) {
        Integer idNaoNulo = requireId(id, "Id da maquina e obrigatorio");
        if (!repository.existsById(Objects.requireNonNull(idNaoNulo))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Máquina não encontrada");
        }
        repository.deleteById(Objects.requireNonNull(idNaoNulo));
    }

    private void validarEntrada(CatalogoMaquinaCreateDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da maquina sao obrigatorios");
        }

        if (limparString(dto.getDescricao()) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A descricao da maquina e obrigatoria");
        }

        validarChecklist(dto.getChecklistPadrao());
    }

    private void validarChecklist(List<MaquinaChecklistItemCreateDTO> checklistPadrao) {
        if (checklistPadrao == null || checklistPadrao.isEmpty()) {
            return;
        }

        Set<String> tarefasInformadas = new HashSet<>();
        for (MaquinaChecklistItemCreateDTO item : checklistPadrao) {
            if (item == null || limparString(item.getDescricao()) == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Todas as tarefas do checklist devem possuir descricao");
            }

            String chave = limparString(item.getDescricao()).toLowerCase();
            if (!tarefasInformadas.add(chave)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Nao e permitido informar tarefas duplicadas no checklist padrao");
            }
        }
    }

    private void aplicarCampos(CatalogoMaquina maquina, CatalogoMaquinaCreateDTO dto) {
        maquina.setDescricao(limparString(dto.getDescricao()));
        maquina.setEspecificacao(limparString(dto.getEspecificacao()));
        maquina.setLimiteManutencao(limparString(dto.getLimiteManutencao()));

        if (dto.getChecklistPadrao() == null || dto.getChecklistPadrao().isEmpty()) {
            maquina.limparChecklistPadrao();
            return;
        }

        Map<Integer, CatalogoMaquinaChecklistPadrao> itensAtuaisPorTarefaId = new HashMap<>();
        for (CatalogoMaquinaChecklistPadrao itemExistente : maquina.getChecklistPadrao()) {
            if (itemExistente != null
                    && itemExistente.getTarefa() != null
                    && itemExistente.getTarefa().getId() != null) {
                itensAtuaisPorTarefaId.put(itemExistente.getTarefa().getId(), itemExistente);
            }
        }

        Set<Integer> tarefaIdsMantidos = new HashSet<>();
        for (MaquinaChecklistItemCreateDTO itemDto : dto.getChecklistPadrao()) {
            CatalogoTarefa tarefa = buscarOuCriarTarefa(limparString(itemDto.getDescricao()));
            Integer tarefaId = tarefa.getId();
            tarefaIdsMantidos.add(tarefaId);

            CatalogoMaquinaChecklistPadrao itemExistente = itensAtuaisPorTarefaId.get(tarefaId);
            if (itemExistente != null) {
                continue;
            }

            CatalogoMaquinaChecklistPadrao item = new CatalogoMaquinaChecklistPadrao();
            item.setId(new CatalogoMaquinaChecklistPadraoId());
            item.setTarefa(tarefa);
            maquina.adicionarChecklistPadraoItem(item);
        }

        maquina.getChecklistPadrao().removeIf(itemExistente -> itemExistente == null
                || itemExistente.getTarefa() == null
                || itemExistente.getTarefa().getId() == null
                || !tarefaIdsMantidos.contains(itemExistente.getTarefa().getId()));
    }

    private CatalogoTarefa buscarOuCriarTarefa(String descricao) {
        return catalogoTarefaRepository
                .findByDescricaoIgnoreCaseAndCategoriaIgnoreCase(descricao, CATEGORIA_TAREFA_MAQUINA)
                .orElseGet(() -> {
                    CatalogoTarefa novaTarefa = new CatalogoTarefa();
                    novaTarefa.setDescricao(descricao);
                    novaTarefa.setCategoria(CATEGORIA_TAREFA_MAQUINA);
                    return catalogoTarefaRepository.save(novaTarefa);
                });
    }

    private String limparString(String valor) {
        if (valor == null) {
            return null;
        }
        String valorLimpo = valor.trim();
        if (valorLimpo.isEmpty()) {
            return null;
        }
        return valorLimpo;
    }

    private Integer requireId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return id;
    }
}
