package com.example.tracker.service;

import com.example.tracker.dto.maquinacontrato.MaquinaContratoCreateDTO;
import com.example.tracker.entity.MaquinaContrato;
import java.util.List;

public interface MaquinaContratoService {

    List<MaquinaContrato> listarTodos();

    MaquinaContrato buscarPorId(Integer id);

    List<MaquinaContrato> buscarPorContrato(Integer codigoContrato);

    List<MaquinaContrato> buscarPorCatalogoMaquina(Integer codigoCatalogoMaquina);

    MaquinaContrato cadastrar(MaquinaContratoCreateDTO maquinaContratoDTO);

    MaquinaContrato atualizar(Integer id, MaquinaContratoCreateDTO maquinaContratoDTO);

    void deletar(Integer id);
}
