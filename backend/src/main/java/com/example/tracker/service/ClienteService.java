package com.example.tracker.service;

import com.example.tracker.dto.cliente.ClienteCreateDTO;
import com.example.tracker.entity.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {

    Cliente criar(ClienteCreateDTO dto);

    Optional<Cliente> atualizar(Integer id, ClienteCreateDTO dto);

    boolean remover(Integer id);

    List<Cliente> listarClientes(String pais, String classificacaoDistancia, Integer page, Integer size);

    Optional<Cliente> buscarPorId(Integer id);
}