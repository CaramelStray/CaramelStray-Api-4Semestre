package com.example.tracker.controller;

import com.example.tracker.dto.tecnicoausencia.TecnicoAusenciaCreateDTO;
import com.example.tracker.dto.tecnicoausencia.TecnicoAusenciaResponseDTO;
import com.example.tracker.entity.TecnicoAusencia;
import com.example.tracker.service.TecnicoAusenciaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TecnicoAusenciaController {

    private final TecnicoAusenciaService tecnicoAusenciaService;

    @PostMapping("/tecnicos/{codigoTecnico}/ausencias")
    public ResponseEntity<TecnicoAusenciaResponseDTO> criar(
            @PathVariable Integer codigoTecnico,
            @Valid @RequestBody TecnicoAusenciaCreateDTO dto) {
        TecnicoAusencia ausencia = tecnicoAusenciaService.criar(codigoTecnico, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(TecnicoAusenciaResponseDTO.fromEntity(ausencia));
    }

    @GetMapping("/tecnicos/{codigoTecnico}/ausencias")
    public ResponseEntity<List<TecnicoAusenciaResponseDTO>> listarPorTecnico(
            @PathVariable Integer codigoTecnico,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(toDTO(tecnicoAusenciaService.listar(codigoTecnico, dataInicio, dataFim, status)));
    }

    @GetMapping("/tecnico-ausencias")
    public ResponseEntity<List<TecnicoAusenciaResponseDTO>> listar(
            @RequestParam(required = false) Integer codigoTecnico,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(toDTO(tecnicoAusenciaService.listar(codigoTecnico, dataInicio, dataFim, status)));
    }

    @GetMapping("/tecnico-ausencias/{codigoAusencia}")
    public ResponseEntity<TecnicoAusenciaResponseDTO> buscarPorId(@PathVariable Integer codigoAusencia) {
        return ResponseEntity.ok(TecnicoAusenciaResponseDTO.fromEntity(tecnicoAusenciaService.buscarPorId(codigoAusencia)));
    }

    @PutMapping("/tecnico-ausencias/{codigoAusencia}")
    public ResponseEntity<TecnicoAusenciaResponseDTO> atualizar(
            @PathVariable Integer codigoAusencia,
            @Valid @RequestBody TecnicoAusenciaCreateDTO dto) {
        TecnicoAusencia ausencia = tecnicoAusenciaService.atualizar(codigoAusencia, dto);
        return ResponseEntity.ok(TecnicoAusenciaResponseDTO.fromEntity(ausencia));
    }

    @PatchMapping("/tecnico-ausencias/{codigoAusencia}/cancelar")
    public ResponseEntity<TecnicoAusenciaResponseDTO> cancelar(@PathVariable Integer codigoAusencia) {
        return ResponseEntity.ok(TecnicoAusenciaResponseDTO.fromEntity(tecnicoAusenciaService.cancelar(codigoAusencia)));
    }

    @DeleteMapping("/tecnico-ausencias/{codigoAusencia}")
    public ResponseEntity<Void> deletar(@PathVariable Integer codigoAusencia) {
        tecnicoAusenciaService.deletar(codigoAusencia);
        return ResponseEntity.noContent().build();
    }

    private List<TecnicoAusenciaResponseDTO> toDTO(List<TecnicoAusencia> ausencias) {
        return ausencias.stream()
                .map(TecnicoAusenciaResponseDTO::fromEntity)
                .toList();
    }
}
