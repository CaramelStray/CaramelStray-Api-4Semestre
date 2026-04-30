package com.example.tracker.controller;

import com.example.tracker.dto.CatalogoMaquinaChecklistPadrao.CatalogoMaquinaChecklistPadraoCreateDTO;
import com.example.tracker.dto.CatalogoMaquinaChecklistPadrao.CatalogoMaquinaChecklistPadraoResponseDTO;
import com.example.tracker.entity.CatalogoMaquinaChecklistPadrao;
import com.example.tracker.service.CatalogoMaquinaChecklistPadraoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalogo-maquinas/{maquinaId}/checklist")
public class CatalogoMaquinaChecklistPadraoController {

    private final CatalogoMaquinaChecklistPadraoService service;

    public CatalogoMaquinaChecklistPadraoController(CatalogoMaquinaChecklistPadraoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CatalogoMaquinaChecklistPadraoResponseDTO>> listar(
            @PathVariable Integer maquinaId) {

        return ResponseEntity.ok(
                service.listarPorMaquina(maquinaId)
                        .stream()
                        .map(CatalogoMaquinaChecklistPadraoResponseDTO::fromEntity)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/{tarefaId}")
public ResponseEntity<CatalogoMaquinaChecklistPadraoResponseDTO> buscarPorId(
        @PathVariable Integer maquinaId,
        @PathVariable Integer tarefaId) {

    CatalogoMaquinaChecklistPadrao item =
            service.buscarPorMaquinaETarefa(maquinaId, tarefaId);

    return ResponseEntity.ok(
            CatalogoMaquinaChecklistPadraoResponseDTO.fromEntity(item));
}

    @PostMapping
    public ResponseEntity<CatalogoMaquinaChecklistPadraoResponseDTO> adicionar(
            @PathVariable Integer maquinaId,
            @Valid @RequestBody CatalogoMaquinaChecklistPadraoCreateDTO dto) {

        CatalogoMaquinaChecklistPadrao item = service.adicionar(maquinaId, dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CatalogoMaquinaChecklistPadraoResponseDTO.fromEntity(item));
    }

    @PutMapping("/{tarefaId}")
    public ResponseEntity<CatalogoMaquinaChecklistPadraoResponseDTO> atualizar(
            @PathVariable Integer maquinaId,
            @PathVariable Integer tarefaId,
            @Valid @RequestBody CatalogoMaquinaChecklistPadraoCreateDTO dto) {

        CatalogoMaquinaChecklistPadrao item = service.atualizar(maquinaId, tarefaId, dto);

        return ResponseEntity.ok(
                CatalogoMaquinaChecklistPadraoResponseDTO.fromEntity(item));
    }

    @DeleteMapping("/{tarefaId}")
    public ResponseEntity<Void> remover(
            @PathVariable Integer maquinaId,
            @PathVariable Integer tarefaId) {

        service.remover(maquinaId, tarefaId);

        return ResponseEntity.noContent().build();
    }
}