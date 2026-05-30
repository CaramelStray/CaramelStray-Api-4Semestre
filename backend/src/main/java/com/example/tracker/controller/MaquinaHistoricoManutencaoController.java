package com.example.tracker.controller;

import com.example.tracker.dto.maquinahistoricomanutencao.MaquinaHistoricoManutencaoCreateDTO;
import com.example.tracker.dto.maquinahistoricomanutencao.MaquinaHistoricoManutencaoResponseDTO;
import com.example.tracker.entity.MaquinaHistoricoManutencao;
import com.example.tracker.service.MaquinaHistoricoManutencaoService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/maquinas-historicos-manutencao")
public class MaquinaHistoricoManutencaoController {

    private final MaquinaHistoricoManutencaoService service;

    public MaquinaHistoricoManutencaoController(MaquinaHistoricoManutencaoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MaquinaHistoricoManutencaoResponseDTO>> listarTodos(
            @RequestParam(required = false) Integer codigoMaquinaContrato,
            @RequestParam(required = false) Integer codigoSoftwareInstalado) {
        List<MaquinaHistoricoManutencao> itens;

        if (codigoMaquinaContrato != null) {
            itens = service.buscarPorMaquinaContrato(codigoMaquinaContrato);
        } else if (codigoSoftwareInstalado != null) {
            itens = service.buscarPorSoftwareInstalado(codigoSoftwareInstalado);
        } else {
            itens = service.listarTodos();
        }

        List<MaquinaHistoricoManutencaoResponseDTO> response = itens.stream()
                .map(MaquinaHistoricoManutencaoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaquinaHistoricoManutencaoResponseDTO> buscarPorId(@PathVariable Integer id) {
        MaquinaHistoricoManutencao entidade = service.buscarPorId(id);
        return ResponseEntity.ok(MaquinaHistoricoManutencaoResponseDTO.fromEntity(entidade));
    }

    @GetMapping(value = "/relatorio/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<byte[]> gerarRelatorioPdf(
            @RequestParam(required = false) Integer codigoMaquinaContrato,
            @RequestParam(required = false) Integer codigoSoftwareInstalado,
            @RequestParam(required = false) Integer codigoTipoManutencao,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String criticidade,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime vencimentoInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime vencimentoFim,
            @RequestParam(required = false) Boolean somenteVencidas,
            @RequestParam(required = false) Integer proximosDias) {
        byte[] pdf = service.gerarRelatorioPdf(
                codigoMaquinaContrato,
                codigoSoftwareInstalado,
                codigoTipoManutencao,
                status,
                criticidade,
                vencimentoInicio,
                vencimentoFim,
                somenteVencidas,
                proximosDias);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-manutencoes.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping
    public ResponseEntity<MaquinaHistoricoManutencaoResponseDTO> criar(
            @Valid @RequestBody MaquinaHistoricoManutencaoCreateDTO dto) {
        MaquinaHistoricoManutencao criado = service.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MaquinaHistoricoManutencaoResponseDTO.fromEntity(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaquinaHistoricoManutencaoResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MaquinaHistoricoManutencaoCreateDTO dto) {
        MaquinaHistoricoManutencao atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(MaquinaHistoricoManutencaoResponseDTO.fromEntity(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/funcionarios")
    public ResponseEntity<List<Integer>> listarFuncionarios(@PathVariable Integer id) {
        return ResponseEntity.ok(service.listarFuncionarios(id));
    }
}
