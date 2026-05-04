package com.example.tracker.controller;

import com.example.tracker.dto.catalogotarefa.CatalogoTarefaCreateDTO;
import com.example.tracker.entity.CatalogoTarefa;
import com.example.tracker.repository.CatalogoTarefaRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalogo-tarefas")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class CatalogoTarefaController {

    private final CatalogoTarefaRepository repository;

    public CatalogoTarefaController(CatalogoTarefaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<CatalogoTarefa>> listar() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<CatalogoTarefa> criar(@Valid @RequestBody CatalogoTarefaCreateDTO dto) {
        CatalogoTarefa tarefa = new CatalogoTarefa();
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setCategoria(dto.getCategoria());
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(tarefa));
    }
}
