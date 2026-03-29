package com.example.tracker.controller;

import com.example.tracker.dto.tecnico.TecnicoCreateDTO;
import com.example.tracker.dto.tecnico.TecnicoResponseDTO;
import com.example.tracker.entity.Tecnico;
import com.example.tracker.service.TecnicoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tecnicos")
@RequiredArgsConstructor
public class TecnicoController {

    private final TecnicoService tecnicoService;

    @PostMapping
    public ResponseEntity<TecnicoResponseDTO> criar(@Valid @RequestBody TecnicoCreateDTO dto,
                                                    Authentication authentication) {
        Tecnico tecnico = tecnicoService.criar(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TecnicoResponseDTO.fromEntity(tecnico));
    }

    @GetMapping
    public ResponseEntity<List<TecnicoResponseDTO>> listar() {
        List<TecnicoResponseDTO> lista = tecnicoService.listarTecnicos()
                .stream()
                .map(TecnicoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TecnicoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return tecnicoService.buscarPorId(id)
                .map(TecnicoResponseDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TecnicoResponseDTO> atualizar(@PathVariable Integer id,
                                                        @Valid @RequestBody TecnicoCreateDTO dto) {
        Tecnico tecnico = tecnicoService.atualizar(id, dto);
        return ResponseEntity.ok(TecnicoResponseDTO.fromEntity(tecnico));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        tecnicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}