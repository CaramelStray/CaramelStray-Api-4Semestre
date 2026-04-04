package com.example.tracker.service;

import com.example.tracker.dto.TecnicoHabilidade.TecnicoHabilidadeCreateDTO;
import com.example.tracker.dto.TecnicoHabilidade.TecnicoHabilidadeResponseDTO;

public interface TecnicoHabilidadeService {

    TecnicoHabilidadeResponseDTO adicionarHabilidade(TecnicoHabilidadeCreateDTO dto);

    void removerHabilidade(Integer tecnicoId, Integer habilidadeId);
}