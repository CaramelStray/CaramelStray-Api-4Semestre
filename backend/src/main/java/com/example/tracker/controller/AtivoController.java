package com.example.tracker.controller;

import com.example.tracker.dto.ativo.AtivoCreateDTO;
import com.example.tracker.dto.ativo.AtivoResponseDTO;
import com.example.tracker.service.AtivoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ativos")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AtivoController {

    private final AtivoService ativoService;

    @PostMapping
    public ResponseEntity<AtivoResponseDTO> criar(@Valid @RequestBody AtivoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ativoService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AtivoResponseDTO>> listar() {
        return ResponseEntity.ok(ativoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtivoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ativoService.buscarPorId(id));
    }

    @GetMapping("/catalogo/{codigoCatalogoAtivo}")
    public ResponseEntity<List<AtivoResponseDTO>> buscarPorCatalogoAtivo(
            @PathVariable Integer codigoCatalogoAtivo) {
        return ResponseEntity.ok(ativoService.buscarPorCatalogoAtivo(codigoCatalogoAtivo));
    }

    @GetMapping("/funcionario/{codigoFuncionario}")
    public ResponseEntity<List<AtivoResponseDTO>> buscarPorFuncionario(
            @PathVariable Integer codigoFuncionario) {
        return ResponseEntity.ok(ativoService.buscarPorFuncionario(codigoFuncionario));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AtivoResponseDTO>> buscarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(ativoService.buscarPorStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtivoResponseDTO> atualizar(
            @PathVariable Integer id, @Valid @RequestBody AtivoCreateDTO dto) {
        return ResponseEntity.ok(ativoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        ativoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
