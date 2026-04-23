package com.example.tracker.controller;

import com.example.tracker.dto.maquinacontrato.MaquinaContratoCreateDTO;
import com.example.tracker.dto.maquinacontrato.MaquinaContratoResponseDTO;
import com.example.tracker.service.MaquinaContratoService;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/maquinas-contrato")
public class MaquinaContratoController {

    private final MaquinaContratoService service;

    public MaquinaContratoController(MaquinaContratoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MaquinaContratoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaquinaContratoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/contrato/{codigoContrato}")
    public ResponseEntity<List<MaquinaContratoResponseDTO>> buscarPorContrato(
            @PathVariable Integer codigoContrato) {

        return ResponseEntity.ok(service.buscarPorContrato(codigoContrato));
    }

    @GetMapping("/catalogo-maquina/{codigoCatalogoMaquina}")
    public ResponseEntity<List<MaquinaContratoResponseDTO>> buscarPorCatalogoMaquina(
            @PathVariable Integer codigoCatalogoMaquina) {

        return ResponseEntity.ok(service.buscarPorCatalogoMaquina(codigoCatalogoMaquina));
    }

    @PostMapping
    public ResponseEntity<MaquinaContratoResponseDTO> criar(
            @Valid @RequestBody MaquinaContratoCreateDTO maquinaContratoDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.cadastrar(maquinaContratoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaquinaContratoResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MaquinaContratoCreateDTO maquinaContratoDTO) {

        return ResponseEntity.ok(service.atualizar(id, maquinaContratoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}