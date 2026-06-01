package com.example.tracker.controller;

import com.example.tracker.dto.tecnico.TecnicoCreateDTO;
import com.example.tracker.dto.tecnico.TecnicoResponseDTO;
import com.example.tracker.service.TecnicoServiceImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tecnicos")
@RequiredArgsConstructor
public class TecnicoController {

    private final TecnicoServiceImpl tecnicoService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TecnicoResponseDTO> criar(@Valid @RequestBody TecnicoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tecnicoService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<TecnicoResponseDTO>> listar() {
        return ResponseEntity.ok(tecnicoService.listarTecnicos());
    }

    @GetMapping("/selecionaveis")
    public ResponseEntity<List<TecnicoResponseDTO>> listarSelecionaveis() {
        return ResponseEntity.ok(tecnicoService.listarSelecionaveis());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TecnicoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return tecnicoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TecnicoResponseDTO> atualizar(@PathVariable Integer id,
                                                        @Valid @RequestBody TecnicoCreateDTO dto) {
        return ResponseEntity.ok(tecnicoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        tecnicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}