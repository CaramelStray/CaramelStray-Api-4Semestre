package com.example.tracker.controller;

import com.example.tracker.dto.catalogo.CatalogoMaquinaCreateDTO;
import com.example.tracker.dto.catalogo.CatalogoMaquinaResponseDTO;
import com.example.tracker.entity.CatalogoMaquina;
import com.example.tracker.service.CatalogoMaquinaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalogo-maquinas")
@RequiredArgsConstructor
public class CatalogoMaquinaController {

    private final CatalogoMaquinaService service;

    @GetMapping
    public ResponseEntity<List<CatalogoMaquinaResponseDTO>> listarTodas() {
        List<CatalogoMaquinaResponseDTO> lista = service.listarTodos()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogoMaquinaResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(toResponseDTO(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<CatalogoMaquinaResponseDTO> criar(
            @RequestBody @Valid CatalogoMaquinaCreateDTO dto) {

        CatalogoMaquina criada = service.adicionar(toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(criada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogoMaquinaResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid CatalogoMaquinaCreateDTO dto) {

        CatalogoMaquina atualizada = service.atualizar(id, toEntity(dto));
        return ResponseEntity.ok(toResponseDTO(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }


    private CatalogoMaquina toEntity(CatalogoMaquinaCreateDTO dto) {
        CatalogoMaquina m = new CatalogoMaquina();
        m.setDescricao(dto.getDescricao());
        m.setEspecificacao(dto.getEspecificacao());
        m.setLimiteManutencao(dto.getLimiteManutencao());
        return m;
    }

    private CatalogoMaquinaResponseDTO toResponseDTO(CatalogoMaquina m) {
        return new CatalogoMaquinaResponseDTO(
                m.getCodigo(),
                m.getDescricao(),
                m.getEspecificacao(),
                m.getLimiteManutencao()
        );
    }
}