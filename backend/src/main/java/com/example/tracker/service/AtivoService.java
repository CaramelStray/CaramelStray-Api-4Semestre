package com.example.tracker.service;

import com.example.tracker.dto.ativo.AtivoCreateDTO;
import com.example.tracker.dto.ativo.AtivoResponseDTO;
import java.util.List;

public interface AtivoService {

    AtivoResponseDTO criar(AtivoCreateDTO dto);

    List<AtivoResponseDTO> listar();

    AtivoResponseDTO buscarPorId(Integer id);

    List<AtivoResponseDTO> buscarPorCatalogoAtivo(Integer codigoCatalogoAtivo);

    List<AtivoResponseDTO> buscarPorFuncionario(Integer codigoFuncionario);

    List<AtivoResponseDTO> buscarPorStatus(String status);

    AtivoResponseDTO atualizar(Integer id, AtivoCreateDTO dto);

    void deletar(Integer id);
}
