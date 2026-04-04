package com.example.tracker.controller;

import com.example.tracker.dto.TecnicoHabilidade.TecnicoHabilidadeCreateDTO;
import com.example.tracker.dto.TecnicoHabilidade.TecnicoHabilidadeResponseDTO;
import com.example.tracker.service.TecnicoHabilidadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tecnico-habilidades")
public class TecnicoHabilidadeController {

    private final TecnicoHabilidadeService service;

    public TecnicoHabilidadeController(TecnicoHabilidadeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TecnicoHabilidadeResponseDTO> adicionar(
            @RequestBody TecnicoHabilidadeCreateDTO dto) {

        return ResponseEntity.ok(service.adicionarHabilidade(dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> remover(
            @RequestParam Integer tecnicoId,
            @RequestParam Integer habilidadeId) {

        service.removerHabilidade(tecnicoId, habilidadeId);
        return ResponseEntity.noContent().build();
    }
}