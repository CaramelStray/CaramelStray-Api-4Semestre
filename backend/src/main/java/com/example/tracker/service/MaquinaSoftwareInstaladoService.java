package com.example.tracker.service;

import com.example.tracker.dto.maquinasoftwareinstalado.MaquinaSoftwareInstaladoCreateDTO;
import com.example.tracker.entity.MaquinaSoftwareInstalado;
import java.util.List;

public interface MaquinaSoftwareInstaladoService {

    List<MaquinaSoftwareInstalado> listarTodos();

    MaquinaSoftwareInstalado buscarPorId(Integer id);

    List<MaquinaSoftwareInstalado> buscarPorMaquinaContrato(Integer codigoMaquinaContrato);

    List<MaquinaSoftwareInstalado> buscarPorSoftware(Integer codigoSoftware);

    MaquinaSoftwareInstalado cadastrar(MaquinaSoftwareInstaladoCreateDTO dto);

    MaquinaSoftwareInstalado atualizar(Integer id, MaquinaSoftwareInstaladoCreateDTO dto);

    void deletar(Integer id);
}
