package com.example.tracker.controller;

import com.example.tracker.dto.ordemservico.OrdemServicoCreateDTO;
import com.example.tracker.dto.ordemservico.OrdemServicoChecklistAtivoCreateDTO;
import com.example.tracker.dto.ordemservico.OrdemServicoChecklistAtivoResponseDTO;
import com.example.tracker.dto.ordemservico.OrdemServicoDadosBasicosResponseDTO;
import com.example.tracker.dto.ordemservico.OrdemServicoResponseDTO;
import com.example.tracker.entity.OrdemServico;
import com.example.tracker.entity.OrdemServicoChecklistAtivo;
import com.example.tracker.service.OrdemServicoChecklistAtivoService;
import com.example.tracker.service.OrdemServicoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordens-servico")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;
    private final OrdemServicoChecklistAtivoService ordemServicoChecklistAtivoService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrdemServicoResponseDTO> criarOrdemServico(
            @Valid @RequestBody OrdemServicoCreateDTO dto) {
        OrdemServico ordemServicoCriada = ordemServicoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrdemServicoResponseDTO.fromEntity(ordemServicoCriada));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<OrdemServicoResponseDTO>> listarOrdensServico() {
        List<OrdemServicoResponseDTO> ordens = ordemServicoService.listarTodos().stream()
                .map(OrdemServicoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ordens);
    }

    @GetMapping("/dados-basicos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<OrdemServicoDadosBasicosResponseDTO>> listarDadosBasicosOrdensServico() {
        return ResponseEntity.ok(ordemServicoService.listarDadosBasicos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(@PathVariable Integer id) {
        OrdemServico ordemServico = ordemServicoService.buscarPorId(id);
        return ResponseEntity.ok(OrdemServicoResponseDTO.fromEntity(ordemServico));
    }

    @GetMapping("/{id}/dados-basicos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrdemServicoDadosBasicosResponseDTO> buscarDadosBasicosPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ordemServicoService.buscarDadosBasicosPorId(id));
    }

    @GetMapping("/cliente/{codigoCliente}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<OrdemServicoResponseDTO>> buscarPorCliente(@PathVariable Integer codigoCliente) {
        List<OrdemServicoResponseDTO> ordens = ordemServicoService.buscarPorCliente(codigoCliente).stream()
                .map(OrdemServicoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ordens);
    }

    @GetMapping("/funcionario/{codigoFuncionario}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<OrdemServicoResponseDTO>> buscarPorFuncionario(@PathVariable Integer codigoFuncionario) {
        List<OrdemServicoResponseDTO> ordens = ordemServicoService.buscarPorFuncionario(codigoFuncionario).stream()
                .map(OrdemServicoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ordens);
    }

    @GetMapping("/software-instalado/{codigoSoftwareInstalado}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<OrdemServicoResponseDTO>> buscarPorSoftwareInstalado(
            @PathVariable Integer codigoSoftwareInstalado) {
        List<OrdemServicoResponseDTO> ordens = ordemServicoService.buscarPorSoftwareInstalado(codigoSoftwareInstalado).stream()
                .map(OrdemServicoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ordens);
    }

    @GetMapping("/contrato/{codigoContrato}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<OrdemServicoResponseDTO>> buscarPorContrato(@PathVariable Integer codigoContrato) {
        List<OrdemServicoResponseDTO> ordens = ordemServicoService.buscarPorContrato(codigoContrato).stream()
                .map(OrdemServicoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ordens);
    }

    @GetMapping("/maquina-contrato/{codigoMaquinaContrato}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<OrdemServicoResponseDTO>> buscarPorMaquinaContrato(
            @PathVariable Integer codigoMaquinaContrato) {
        List<OrdemServicoResponseDTO> ordens = ordemServicoService.buscarPorMaquinaContrato(codigoMaquinaContrato).stream()
                .map(OrdemServicoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ordens);
    }

    @GetMapping("/{id}/checklist-ativos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<OrdemServicoChecklistAtivoResponseDTO>> listarChecklistAtivos(
            @PathVariable Integer id) {
        List<OrdemServicoChecklistAtivoResponseDTO> itens = ordemServicoChecklistAtivoService
                .listarPorOrdemServico(id).stream()
                .map(OrdemServicoChecklistAtivoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(itens);
    }

    @GetMapping("/{id}/checklist-ativos/{codigoItem}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrdemServicoChecklistAtivoResponseDTO> buscarChecklistAtivoPorId(
            @PathVariable Integer id,
            @PathVariable Integer codigoItem) {
        OrdemServicoChecklistAtivo item = ordemServicoChecklistAtivoService.buscarItem(id, codigoItem);
        return ResponseEntity.ok(OrdemServicoChecklistAtivoResponseDTO.fromEntity(item));
    }

    @PostMapping("/{id}/checklist-ativos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrdemServicoChecklistAtivoResponseDTO> adicionarChecklistAtivo(
            @PathVariable Integer id,
            @Valid @RequestBody OrdemServicoChecklistAtivoCreateDTO dto) {
        OrdemServicoChecklistAtivo item = ordemServicoChecklistAtivoService.adicionar(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrdemServicoChecklistAtivoResponseDTO.fromEntity(item));
    }

    @PutMapping("/{id}/checklist-ativos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<OrdemServicoChecklistAtivoResponseDTO>> substituirChecklistAtivos(
            @PathVariable Integer id,
            @Valid @RequestBody List<OrdemServicoChecklistAtivoCreateDTO> itens) {
        List<OrdemServicoChecklistAtivoResponseDTO> response = ordemServicoChecklistAtivoService
                .substituirChecklist(id, itens).stream()
                .map(OrdemServicoChecklistAtivoResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/checklist-ativos/{codigoItem}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrdemServicoChecklistAtivoResponseDTO> atualizarChecklistAtivo(
            @PathVariable Integer id,
            @PathVariable Integer codigoItem,
            @Valid @RequestBody OrdemServicoChecklistAtivoCreateDTO dto) {
        OrdemServicoChecklistAtivo item = ordemServicoChecklistAtivoService.atualizar(id, codigoItem, dto);
        return ResponseEntity.ok(OrdemServicoChecklistAtivoResponseDTO.fromEntity(item));
    }

    @DeleteMapping("/{id}/checklist-ativos/{codigoItem}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> removerChecklistAtivo(
            @PathVariable Integer id,
            @PathVariable Integer codigoItem) {
        ordemServicoChecklistAtivoService.remover(id, codigoItem);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrdemServicoResponseDTO> atualizarOrdemServico(
            @PathVariable Integer id,
            @Valid @RequestBody OrdemServicoCreateDTO dto) {
        OrdemServico ordemServicoAtualizada = ordemServicoService.atualizar(id, dto);
        return ResponseEntity.ok(OrdemServicoResponseDTO.fromEntity(ordemServicoAtualizada));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deletarOrdemServico(@PathVariable Integer id) {
        ordemServicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
