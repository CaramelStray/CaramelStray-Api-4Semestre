package com.example.tracker.service;

import com.example.tracker.dto.tecnicoausencia.TecnicoAusenciaCreateDTO;
import com.example.tracker.entity.TecnicoAusencia;
import java.time.LocalDate;
import java.util.List;

public interface TecnicoAusenciaService {

    TecnicoAusencia criar(Integer codigoTecnico, TecnicoAusenciaCreateDTO dto);

    TecnicoAusencia atualizar(Integer codigoAusencia, TecnicoAusenciaCreateDTO dto);

    TecnicoAusencia buscarPorId(Integer codigoAusencia);

    List<TecnicoAusencia> listar(Integer codigoTecnico, LocalDate dataInicio, LocalDate dataFim, String status);

    TecnicoAusencia cancelar(Integer codigoAusencia);

    void deletar(Integer codigoAusencia);
}
