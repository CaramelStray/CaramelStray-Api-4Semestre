package com.example.tracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.tracker.entity.Contrato;
import com.example.tracker.service.ContratoService;

import java.util.List;

@RestController
@RequestMapping("/contratos")
public class ContratoController {

    private final ContratoService service;

    public ContratoController(ContratoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Contrato>> listarTodos() {
        List<Contrato> contratos = service.listarTodos();
        return ResponseEntity.ok(contratos); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contrato> buscarPorId(@PathVariable Integer id) {
        Contrato contrato = service.buscarPorId(id);
        return ResponseEntity.ok(contrato); 
    }

    @GetMapping("/cliente/{codigoCliente}")
    public ResponseEntity<List<Contrato>> buscarPorCliente(@PathVariable Integer codigoCliente) {
        List<Contrato> contratos = service.buscarPorCliente(codigoCliente);
        return ResponseEntity.ok(contratos); 
    }

    @PostMapping
    public ResponseEntity<Contrato> criar(@RequestBody Contrato contrato) {
        Contrato criado = service.salvar(contrato);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contrato> atualizar(@PathVariable Integer id, @RequestBody Contrato contrato) {
        Contrato atualizado = service.atualizar(id, contrato);
        return ResponseEntity.ok(atualizado); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build(); 
    }
}