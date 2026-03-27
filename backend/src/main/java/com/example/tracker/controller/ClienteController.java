package com.example.tracker.controller;

import com.example.tracker.dto.cliente.ClienteCreateDTO;
import com.example.tracker.dto.cliente.ClienteResponseDTO;
import com.example.tracker.entity.Cliente;
import com.example.tracker.service.ClienteService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ClienteResponseDTO> criarCliente(
            @Valid @RequestBody ClienteCreateDTO dto,
            Authentication authentication) {
        Cliente clienteCriado = clienteService.criar(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteResponseDTO.fromEntity(clienteCriado));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes() {
        List<ClienteResponseDTO> clientes = clienteService.listarClientes().stream()
                .map(ClienteResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ClienteResponseDTO> buscarClientePorId(@PathVariable Integer id) {
        return clienteService.buscarPorId(id)
                .map(ClienteResponseDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
