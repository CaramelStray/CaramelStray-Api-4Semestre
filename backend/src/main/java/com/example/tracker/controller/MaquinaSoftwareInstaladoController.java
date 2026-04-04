package com.example.tracker.controller;

import com.example.tracker.dto.maquinasoftwareinstalado.MaquinaSoftwareInstaladoCreateDTO;
import com.example.tracker.dto.maquinasoftwareinstalado.MaquinaSoftwareInstaladoResponseDTO;
import com.example.tracker.entity.MaquinaSoftwareInstalado;
import com.example.tracker.service.MaquinaSoftwareInstaladoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/maquinas-softwares-instalados")
public class MaquinaSoftwareInstaladoController {

    private final MaquinaSoftwareInstaladoService service;

    public MaquinaSoftwareInstaladoController(MaquinaSoftwareInstaladoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MaquinaSoftwareInstaladoResponseDTO>> listarTodos(
            @RequestParam(required = false) Integer codigoMaquinaContrato,
            @RequestParam(required = false) Integer codigoSoftware) {
        List<MaquinaSoftwareInstalado> itens;

        if (codigoMaquinaContrato != null) {
            itens = service.buscarPorMaquinaContrato(codigoMaquinaContrato);
        } else if (codigoSoftware != null) {
            itens = service.buscarPorSoftware(codigoSoftware);
        } else {
            itens = service.listarTodos();
        }

        List<MaquinaSoftwareInstaladoResponseDTO> response = itens.stream()
                .map(MaquinaSoftwareInstaladoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaquinaSoftwareInstaladoResponseDTO> buscarPorId(@PathVariable Integer id) {
        MaquinaSoftwareInstalado entidade = service.buscarPorId(id);
        return ResponseEntity.ok(MaquinaSoftwareInstaladoResponseDTO.fromEntity(entidade));
    }

    @PostMapping
    public ResponseEntity<MaquinaSoftwareInstaladoResponseDTO> criar(
            @Valid @RequestBody MaquinaSoftwareInstaladoCreateDTO dto) {
        MaquinaSoftwareInstalado criado = service.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MaquinaSoftwareInstaladoResponseDTO.fromEntity(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaquinaSoftwareInstaladoResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MaquinaSoftwareInstaladoCreateDTO dto) {
        MaquinaSoftwareInstalado atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(MaquinaSoftwareInstaladoResponseDTO.fromEntity(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
