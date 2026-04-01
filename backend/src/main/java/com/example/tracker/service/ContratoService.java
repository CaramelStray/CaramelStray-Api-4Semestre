package com.example.tracker.service;

import com.example.tracker.dto.contrato.ContratoCreateDTO;
import com.example.tracker.entity.Contrato;
import java.util.List;

public interface ContratoService {

    List<Contrato> listarTodos();

    Contrato buscarPorId(Integer id);

    List<Contrato> buscarPorCliente(Integer codigoCliente);

    Contrato cadastrar(ContratoCreateDTO contratoDTO);

    Contrato atualizar(Integer id, ContratoCreateDTO contratoDTO);

    void deletar(Integer id);
}
