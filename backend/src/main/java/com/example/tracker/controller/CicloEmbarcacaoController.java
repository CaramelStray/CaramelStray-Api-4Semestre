package com.example.tracker.controller;

import com.example.tracker.dto.ciclo.CicloEmbarcacaoCreateDTO;
import com.example.tracker.dto.ciclo.CicloEmbarcacaoResponseDTO;
import com.example.tracker.service.CicloEmbarcacaoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ciclos")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class CicloEmbarcacaoController {

    private final CicloEmbarcacaoService cicloEmbarcacaoService;

    @PostMapping
    public ResponseEntity<CicloEmbarcacaoResponseDTO> criar(@Valid @RequestBody CicloEmbarcacaoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cicloEmbarcacaoService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<CicloEmbarcacaoResponseDTO>> listar() {
        return ResponseEntity.ok(cicloEmbarcacaoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CicloEmbarcacaoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(cicloEmbarcacaoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CicloEmbarcacaoResponseDTO> atualizar(
            @PathVariable Integer id, @Valid @RequestBody CicloEmbarcacaoCreateDTO dto) {
        return ResponseEntity.ok(cicloEmbarcacaoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        cicloEmbarcacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
