package com.example.tracker.service;

import com.example.tracker.dto.catalogoativo.CatalogoAtivoCreateDTO;
import com.example.tracker.dto.catalogoativo.CatalogoAtivoResponseDTO;
import java.util.List;

public interface CatalogoAtivoService {

    CatalogoAtivoResponseDTO criar(CatalogoAtivoCreateDTO dto);

    List<CatalogoAtivoResponseDTO> listar();

    CatalogoAtivoResponseDTO buscarPorId(Integer id);

    CatalogoAtivoResponseDTO atualizar(Integer id, CatalogoAtivoCreateDTO dto);

    void deletar(Integer id);
}
