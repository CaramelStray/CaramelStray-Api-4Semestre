package com.example.tracker.controller;

import com.example.tracker.dto.catalogo.CatalogoMaquinaCreateDTO;
import com.example.tracker.dto.catalogo.CatalogoMaquinaResponseDTO;
import com.example.tracker.service.CatalogoMaquinaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalogo-maquinas")
public class CatalogoMaquinaController {

    private final CatalogoMaquinaService service;

    public CatalogoMaquinaController(CatalogoMaquinaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CatalogoMaquinaResponseDTO>> listarTodas() {
        List<CatalogoMaquinaResponseDTO> lista = service.listarTodos()
                .stream()
                .map(CatalogoMaquinaResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogoMaquinaResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(CatalogoMaquinaResponseDTO.fromEntity(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<CatalogoMaquinaResponseDTO> criar(
            @RequestBody @Valid CatalogoMaquinaCreateDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CatalogoMaquinaResponseDTO.fromEntity(service.adicionar(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogoMaquinaResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid CatalogoMaquinaCreateDTO dto) {

        return ResponseEntity.ok(CatalogoMaquinaResponseDTO.fromEntity(service.atualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
