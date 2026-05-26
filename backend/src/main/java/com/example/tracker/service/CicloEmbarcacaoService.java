package com.example.tracker.service;

import com.example.tracker.dto.ciclo.CicloEmbarcacaoCreateDTO;
import com.example.tracker.dto.ciclo.CicloEmbarcacaoResponseDTO;
import java.util.List;

public interface CicloEmbarcacaoService {

    CicloEmbarcacaoResponseDTO criar(CicloEmbarcacaoCreateDTO dto);

    List<CicloEmbarcacaoResponseDTO> listar();

    CicloEmbarcacaoResponseDTO buscarPorId(Integer id);

    CicloEmbarcacaoResponseDTO atualizar(Integer id, CicloEmbarcacaoCreateDTO dto);

    void deletar(Integer id);
}
