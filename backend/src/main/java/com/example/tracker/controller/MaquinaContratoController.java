package com.example.tracker.controller;

import com.example.tracker.dto.maquinacontrato.MaquinaContratoCreateDTO;
import com.example.tracker.dto.maquinacontrato.MaquinaContratoResponseDTO;
import com.example.tracker.entity.MaquinaContrato;
import com.example.tracker.service.MaquinaContratoService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maquinas-contrato")
public class MaquinaContratoController {

    private final MaquinaContratoService service;

    public MaquinaContratoController(MaquinaContratoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MaquinaContratoResponseDTO>> listarTodos() {
        List<MaquinaContratoResponseDTO> maquinasContrato = service.listarTodos().stream()
                .map(MaquinaContratoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(maquinasContrato);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaquinaContratoResponseDTO> buscarPorId(@PathVariable Integer id) {
        MaquinaContrato maquinaContrato = service.buscarPorId(id);
        return ResponseEntity.ok(MaquinaContratoResponseDTO.fromEntity(maquinaContrato));
    }

    @GetMapping("/contrato/{codigoContrato}")
    public ResponseEntity<List<MaquinaContratoResponseDTO>> buscarPorContrato(@PathVariable Integer codigoContrato) {
        List<MaquinaContratoResponseDTO> maquinasContrato = service.buscarPorContrato(codigoContrato).stream()
                .map(MaquinaContratoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(maquinasContrato);
    }

    @GetMapping("/catalogo-maquina/{codigoCatalogoMaquina}")
    public ResponseEntity<List<MaquinaContratoResponseDTO>> buscarPorCatalogoMaquina(
            @PathVariable Integer codigoCatalogoMaquina) {
        List<MaquinaContratoResponseDTO> maquinasContrato = service.buscarPorCatalogoMaquina(codigoCatalogoMaquina)
                .stream()
                .map(MaquinaContratoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(maquinasContrato);
    }

    @PostMapping
    public ResponseEntity<MaquinaContratoResponseDTO> criar(
            @Valid @RequestBody MaquinaContratoCreateDTO maquinaContratoDTO) {
        MaquinaContrato criado = service.cadastrar(maquinaContratoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(MaquinaContratoResponseDTO.fromEntity(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaquinaContratoResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MaquinaContratoCreateDTO maquinaContratoDTO) {
        MaquinaContrato atualizado = service.atualizar(id, maquinaContratoDTO);
        return ResponseEntity.ok(MaquinaContratoResponseDTO.fromEntity(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
