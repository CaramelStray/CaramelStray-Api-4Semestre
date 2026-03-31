package com.example.tracker.controller;

import com.example.tracker.entity.Habilidade;
import com.example.tracker.service.HabilidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habilidades")
@RequiredArgsConstructor
public class HabilidadeController {

    private final HabilidadeService service;

    @GetMapping
    public ResponseEntity<List<Habilidade>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Habilidade> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/descricao")
    public ResponseEntity<Habilidade> buscarPorDescricao(@RequestParam String descricao) {
        return ResponseEntity.ok(service.buscarPorDescricao(descricao));
    }

    @PostMapping
    public ResponseEntity<Habilidade> criar(@RequestBody Habilidade habilidade) {
        Habilidade criada = service.adicionar(habilidade);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Habilidade> atualizar(@PathVariable Integer id,
            @RequestBody Habilidade habilidade) {
        return ResponseEntity.ok(service.atualizar(id, habilidade));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}