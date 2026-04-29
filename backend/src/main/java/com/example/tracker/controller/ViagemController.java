package com.example.tracker.controller;

import com.example.tracker.dto.viagem.ViagemCreateDTO;
import com.example.tracker.dto.viagem.ViagemResponseDTO;
import com.example.tracker.entity.Viagem;
import com.example.tracker.service.ViagemService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/viagens")
public class ViagemController {

    private final ViagemService service;

    public ViagemController(ViagemService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ViagemResponseDTO>> listarTodas() {
        List<ViagemResponseDTO> viagens = service.listarTodas().stream()
                .map(ViagemResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(viagens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViagemResponseDTO> buscarPorId(@PathVariable Integer id) {
        Viagem viagem = service.buscarPorId(id);
        return ResponseEntity.ok(ViagemResponseDTO.fromEntity(viagem));
    }

    @GetMapping("/cliente/{codigoCliente}")
    public ResponseEntity<List<ViagemResponseDTO>> buscarPorCliente(@PathVariable Integer codigoCliente) {
        List<ViagemResponseDTO> viagens = service.buscarPorCliente(codigoCliente).stream()
                .map(ViagemResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(viagens);
    }

    @GetMapping("/status")
    public ResponseEntity<List<ViagemResponseDTO>> buscarPorStatus(@RequestParam String status) {
        List<ViagemResponseDTO> viagens = service.buscarPorStatus(status).stream()
                .map(ViagemResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(viagens);
    }

    @PostMapping
    public ResponseEntity<ViagemResponseDTO> criar(@Valid @RequestBody ViagemCreateDTO viagemDTO) {
        Viagem criada = service.salvar(viagemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ViagemResponseDTO.fromEntity(criada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViagemResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ViagemCreateDTO viagemDTO) {
        Viagem atualizada = service.atualizar(id, viagemDTO);
        return ResponseEntity.ok(ViagemResponseDTO.fromEntity(atualizada));
    }
}