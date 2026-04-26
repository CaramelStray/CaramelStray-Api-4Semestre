package com.example.tracker.service;

import com.example.tracker.dto.maquinacontrato.MaquinaContratoCreateDTO;
import com.example.tracker.dto.maquinacontrato.MaquinaContratoResponseDTO;
import java.util.List;

public interface MaquinaContratoService {

    List<MaquinaContratoResponseDTO> listarTodos();

    MaquinaContratoResponseDTO buscarPorId(Integer id);

    List<MaquinaContratoResponseDTO> buscarPorContrato(Integer codigoContrato);

    List<MaquinaContratoResponseDTO> buscarPorCatalogoMaquina(Integer codigoCatalogoMaquina);

    MaquinaContratoResponseDTO cadastrar(MaquinaContratoCreateDTO maquinaContratoDTO);

    MaquinaContratoResponseDTO atualizar(Integer id, MaquinaContratoCreateDTO maquinaContratoDTO);

    void deletar(Integer id);
}