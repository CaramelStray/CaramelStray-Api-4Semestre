package com.example.tracker.controller;

import com.example.tracker.dto.habilidade.HabilidadeCreateDTO;
import com.example.tracker.dto.habilidade.HabilidadeResponseDTO;
import com.example.tracker.entity.Habilidade;
import com.example.tracker.service.HabilidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/habilidades")
@RequiredArgsConstructor
public class HabilidadeController {

    private final HabilidadeService service;

    @GetMapping
    public ResponseEntity<List<HabilidadeResponseDTO>> listarTodas() {
        List<HabilidadeResponseDTO> lista = service.listarTodas()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabilidadeResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(toResponseDTO(service.buscarPorId(id)));
    }

    @GetMapping("/descricao")
    public ResponseEntity<HabilidadeResponseDTO> buscarPorDescricao(@RequestParam String descricao) {
        return ResponseEntity.ok(toResponseDTO(service.buscarPorDescricao(descricao)));
    }

    @PostMapping
    public ResponseEntity<HabilidadeResponseDTO> criar(
            @RequestBody @Valid HabilidadeCreateDTO dto) {

        Habilidade criada = service.adicionar(toEntity(dto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponseDTO(criada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabilidadeResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid HabilidadeCreateDTO dto) {

        Habilidade atualizada = service.atualizar(id, toEntity(dto));

        return ResponseEntity.ok(toResponseDTO(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }


    private Habilidade toEntity(HabilidadeCreateDTO dto) {
        Habilidade h = new Habilidade();
        h.setDescricao(dto.getDescricao());
        h.setObservacao(dto.getObservacao());
        return h;
    }

    private HabilidadeResponseDTO toResponseDTO(Habilidade h) {
        return new HabilidadeResponseDTO(
                h.getCodigo(),
                h.getDescricao(),
                h.getObservacao()
        );
    }
}