package com.example.tracker.controller;

import com.example.tracker.dto.software.CatalogoSoftwareCreateDTO;
import com.example.tracker.dto.software.CatalogoSoftwareResponseDTO;
import com.example.tracker.entity.CatalogoSoftware;
import com.example.tracker.service.CatalogoSoftwareService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/catalogo-softwares", "/softwares"})
@RequiredArgsConstructor
public class CatalogoSoftwareController {

    private final CatalogoSoftwareService catalogoSoftwareService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CatalogoSoftwareResponseDTO> criarSoftware(
            @Valid @RequestBody CatalogoSoftwareCreateDTO dto) {
        CatalogoSoftware softwareCriado = catalogoSoftwareService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(CatalogoSoftwareResponseDTO.fromEntity(softwareCriado));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<CatalogoSoftwareResponseDTO>> listarSoftwares(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        List<CatalogoSoftwareResponseDTO> softwares = catalogoSoftwareService
                .listarSoftwares(termo, tipo, ativo, page, size).stream()
                .map(CatalogoSoftwareResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(softwares);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CatalogoSoftwareResponseDTO> buscarSoftwarePorId(@PathVariable Integer id) {
        return catalogoSoftwareService.buscarPorId(id)
                .map(CatalogoSoftwareResponseDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CatalogoSoftwareResponseDTO> atualizarSoftware(
            @PathVariable Integer id,
            @Valid @RequestBody CatalogoSoftwareCreateDTO dto) {
        return catalogoSoftwareService.atualizar(id, dto)
                .map(CatalogoSoftwareResponseDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> removerSoftware(@PathVariable Integer id) {
        boolean removido = catalogoSoftwareService.remover(id);
        if (!removido) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
