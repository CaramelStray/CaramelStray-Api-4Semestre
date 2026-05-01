package com.example.tracker.controller;

import com.example.tracker.dto.viagem.TipoViagemResponseDTO;
import com.example.tracker.repository.TipoViagemRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tipos-viagem")
public class TipoViagemController {

    private final TipoViagemRepository tipoViagemRepository;

    public TipoViagemController(TipoViagemRepository tipoViagemRepository) {
        this.tipoViagemRepository = tipoViagemRepository;
    }

    @GetMapping
    public ResponseEntity<List<TipoViagemResponseDTO>> listarAtivos() {
        List<TipoViagemResponseDTO> tipos = tipoViagemRepository.findByAtivoTrue().stream()
                .map(TipoViagemResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tipos);
    }
}