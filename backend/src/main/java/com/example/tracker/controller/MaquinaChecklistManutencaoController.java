package com.example.tracker.controller;

import com.example.tracker.dto.maquinachecklistmanutencao.MaquinaChecklistManutencaoResponseDTO;
import com.example.tracker.dto.maquinachecklistmanutencao.MaquinaChecklistManutencaoUpdateDTO;
import com.example.tracker.entity.MaquinaChecklistManutencao;
import com.example.tracker.service.MaquinaChecklistManutencaoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maquinas-historicos-manutencao/{codigoHistoricoManutencao}/checklist")
public class MaquinaChecklistManutencaoController {

    private final MaquinaChecklistManutencaoService service;

    public MaquinaChecklistManutencaoController(MaquinaChecklistManutencaoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_TECNICO')")
    public ResponseEntity<List<MaquinaChecklistManutencaoResponseDTO>> listarPorHistorico(
            @PathVariable Integer codigoHistoricoManutencao) {
        List<MaquinaChecklistManutencaoResponseDTO> response = service.listarPorHistorico(codigoHistoricoManutencao)
                .stream()
                .map(MaquinaChecklistManutencaoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/gerar")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_TECNICO')")
    public ResponseEntity<List<MaquinaChecklistManutencaoResponseDTO>> gerarChecklistPadrao(
            @PathVariable Integer codigoHistoricoManutencao) {
        List<MaquinaChecklistManutencaoResponseDTO> response = service.gerarChecklistPadrao(codigoHistoricoManutencao)
                .stream()
                .map(MaquinaChecklistManutencaoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{codigoChecklist}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_TECNICO')")
    public ResponseEntity<MaquinaChecklistManutencaoResponseDTO> atualizarItem(
            @PathVariable Integer codigoHistoricoManutencao,
            @PathVariable Integer codigoChecklist,
            @Valid @RequestBody MaquinaChecklistManutencaoUpdateDTO dto) {
        MaquinaChecklistManutencao atualizado = service.atualizarItem(
                codigoHistoricoManutencao,
                codigoChecklist,
                dto);

        return ResponseEntity.ok(MaquinaChecklistManutencaoResponseDTO.fromEntity(atualizado));
    }
}
