package com.example.tracker.controller;

import com.example.tracker.dto.catalogoativo.CatalogoAtivoCreateDTO;
import com.example.tracker.dto.catalogoativo.CatalogoAtivoResponseDTO;
import com.example.tracker.service.CatalogoAtivoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalogo-ativos")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class CatalogoAtivoController {

    private final CatalogoAtivoService catalogoAtivoService;

    @PostMapping
    public ResponseEntity<CatalogoAtivoResponseDTO> criar(@Valid @RequestBody CatalogoAtivoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoAtivoService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<CatalogoAtivoResponseDTO>> listar() {
        return ResponseEntity.ok(catalogoAtivoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogoAtivoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(catalogoAtivoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogoAtivoResponseDTO> atualizar(
            @PathVariable Integer id, @Valid @RequestBody CatalogoAtivoCreateDTO dto) {
        return ResponseEntity.ok(catalogoAtivoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        catalogoAtivoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
