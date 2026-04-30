package com.example.tracker.service;

import com.example.tracker.dto.viagem.ViagemCreateDTO;
import com.example.tracker.entity.Viagem;
import java.util.List;

public interface ViagemService {
    Viagem salvar(ViagemCreateDTO dto);
    Viagem buscarPorId(Integer id);
    List<Viagem> listarTodas();
    void excluir(Integer id);
    List<Viagem> buscarPorCliente(Integer idCliente);
    List<Viagem> buscarPorOrdemServico(Integer codigoOrdemServico);
    List<Viagem> buscarPorStatus(String status);
    Viagem atualizar(Integer id, ViagemCreateDTO dto);
}