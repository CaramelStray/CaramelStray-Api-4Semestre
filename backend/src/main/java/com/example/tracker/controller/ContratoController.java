package com.example.tracker.controller;

import com.example.tracker.dto.contrato.ContratoCreateDTO;
import com.example.tracker.dto.contrato.ContratoResponseDTO;
import com.example.tracker.entity.Contrato;
import com.example.tracker.service.ContratoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contratos")
public class ContratoController {

    private final ContratoService service;

    public ContratoController(ContratoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ContratoResponseDTO>> listarTodos() {
        List<ContratoResponseDTO> contratos = service.listarTodos().stream()
                .map(ContratoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contratos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoResponseDTO> buscarPorId(@PathVariable Integer id) {
        Contrato contrato = service.buscarPorId(id);
        return ResponseEntity.ok(ContratoResponseDTO.fromEntity(contrato));
    }

    @GetMapping("/cliente/{codigoCliente}")
    public ResponseEntity<List<ContratoResponseDTO>> buscarPorCliente(@PathVariable Integer codigoCliente) {
        List<ContratoResponseDTO> contratos = service.buscarPorCliente(codigoCliente).stream()
                .map(ContratoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contratos);
    }

    @PostMapping
    public ResponseEntity<ContratoResponseDTO> criar(@Valid @RequestBody ContratoCreateDTO contratoDTO) {
        Contrato criado = service.cadastrar(contratoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ContratoResponseDTO.fromEntity(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContratoResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ContratoCreateDTO contratoDTO) {
        Contrato atualizado = service.atualizar(id, contratoDTO);
        return ResponseEntity.ok(ContratoResponseDTO.fromEntity(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
