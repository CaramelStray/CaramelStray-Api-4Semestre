package com.example.tracker.service;

import com.example.tracker.dto.tecnico.TecnicoCreateDTO;
import com.example.tracker.dto.tecnico.TecnicoResponseDTO;
import java.util.List;
import java.util.Optional;

public interface TecnicoService {

    TecnicoResponseDTO criar(TecnicoCreateDTO dto);

    List<TecnicoResponseDTO> listarTecnicos();

    List<TecnicoResponseDTO> listarSelecionaveis();

    Optional<TecnicoResponseDTO> buscarPorId(Integer id);

    TecnicoResponseDTO atualizar(Integer id, TecnicoCreateDTO dto);

    void deletar(Integer id);
}