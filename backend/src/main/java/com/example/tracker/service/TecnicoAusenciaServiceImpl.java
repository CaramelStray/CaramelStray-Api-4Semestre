package com.example.tracker.service;

import com.example.tracker.dto.tecnicoausencia.TecnicoAusenciaCreateDTO;
import com.example.tracker.entity.Tecnico;
import com.example.tracker.entity.TecnicoAusencia;
import com.example.tracker.enums.StatusAusenciaTecnico;
import com.example.tracker.enums.TipoAusenciaTecnico;
import com.example.tracker.repository.TecnicoAusenciaRepository;
import com.example.tracker.repository.TecnicoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TecnicoAusenciaServiceImpl implements TecnicoAusenciaService {

    private final TecnicoAusenciaRepository tecnicoAusenciaRepository;
    private final TecnicoRepository tecnicoRepository;

    @Override
    @Transactional
    public TecnicoAusencia criar(Integer codigoTecnico, TecnicoAusenciaCreateDTO dto) {
        Tecnico tecnico = buscarTecnico(codigoTecnico);
        validarDatas(dto);

        TecnicoAusencia ausencia = new TecnicoAusencia();
        ausencia.setTecnico(tecnico);
        aplicarCampos(ausencia, dto, true);
        validarSobreposicao(ausencia, null);

        return tecnicoAusenciaRepository.save(ausencia);
    }

    @Override
    @Transactional
    public TecnicoAusencia atualizar(Integer codigoAusencia, TecnicoAusenciaCreateDTO dto) {
        TecnicoAusencia ausencia = buscarPorId(codigoAusencia);
        validarDatas(dto);

        aplicarCampos(ausencia, dto, false);
        validarSobreposicao(ausencia, codigoAusencia);

        return tecnicoAusenciaRepository.save(ausencia);
    }

    @Override
    @Transactional(readOnly = true)
    public TecnicoAusencia buscarPorId(Integer codigoAusencia) {
        Integer codigoNaoNulo = requireId(codigoAusencia, "Codigo da ausencia e obrigatorio");
        return tecnicoAusenciaRepository.findById(Objects.requireNonNull(codigoNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ausencia do tecnico nao encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TecnicoAusencia> listar(Integer codigoTecnico, LocalDate dataInicio, LocalDate dataFim, String status) {
        if (dataInicio != null && dataFim != null && dataFim.isBefore(dataInicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de fim nao pode ser anterior a data de inicio");
        }

        StatusAusenciaTecnico statusFiltro = status == null || status.isBlank()
                ? null
                : normalizarStatus(status);

        if (codigoTecnico != null && !tecnicoRepository.existsById(codigoTecnico)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tecnico nao encontrado");
        }

        return tecnicoAusenciaRepository.filtrar(codigoTecnico, dataInicio, dataFim, statusFiltro);
    }

    @Override
    @Transactional
    public TecnicoAusencia cancelar(Integer codigoAusencia) {
        TecnicoAusencia ausencia = buscarPorId(codigoAusencia);
        ausencia.setStatus(StatusAusenciaTecnico.CANCELADA);
        return tecnicoAusenciaRepository.save(ausencia);
    }

    @Override
    @Transactional
    public void deletar(Integer codigoAusencia) {
        TecnicoAusencia ausencia = buscarPorId(codigoAusencia);
        tecnicoAusenciaRepository.delete(ausencia);
    }

    private void aplicarCampos(TecnicoAusencia ausencia, TecnicoAusenciaCreateDTO dto, boolean criar) {
        ausencia.setTipo(normalizarTipo(dto.getTipo()));
        ausencia.setDataInicio(dto.getDataInicio());
        ausencia.setDataFim(dto.getDataFim());
        ausencia.setMotivo(limpar(dto.getMotivo()));
        ausencia.setObservacao(limpar(dto.getObservacao()));

        if (criar) {
            ausencia.setStatus(dto.getStatus() == null ? StatusAusenciaTecnico.ATIVA : normalizarStatus(dto.getStatus()));
        } else if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            ausencia.setStatus(normalizarStatus(dto.getStatus()));
        }
    }

    private void validarDatas(TecnicoAusenciaCreateDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da ausencia sao obrigatorios");
        }
        if (dto.getDataInicio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de inicio e obrigatoria");
        }
        if (dto.getDataFim() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de fim e obrigatoria");
        }
        if (dto.getDataFim().isBefore(dto.getDataInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de fim nao pode ser anterior a data de inicio");
        }
    }

    private void validarSobreposicao(TecnicoAusencia ausencia, Integer codigoIgnorado) {
        if (ausencia.getStatus() != StatusAusenciaTecnico.ATIVA) {
            return;
        }

        boolean existeSobreposicao = tecnicoAusenciaRepository.existsSobreposicaoAtiva(
                ausencia.getTecnico().getId(),
                ausencia.getDataInicio(),
                ausencia.getDataFim(),
                codigoIgnorado,
                StatusAusenciaTecnico.ATIVA);

        if (existeSobreposicao) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ja existe ausencia ativa para o tecnico nesse periodo");
        }
    }

    private Tecnico buscarTecnico(Integer codigoTecnico) {
        Integer codigoNaoNulo = requireId(codigoTecnico, "Codigo do tecnico e obrigatorio");
        return tecnicoRepository.findById(Objects.requireNonNull(codigoNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tecnico nao encontrado"));
    }

    private TipoAusenciaTecnico normalizarTipo(String valor) {
        try {
            TipoAusenciaTecnico tipo = TipoAusenciaTecnico.from(valor);
            if (tipo == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de ausencia e obrigatorio");
            }
            return tipo;
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    private StatusAusenciaTecnico normalizarStatus(String valor) {
        try {
            StatusAusenciaTecnico status = StatusAusenciaTecnico.from(valor);
            if (status == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status de ausencia e obrigatorio");
            }
            return status;
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    private Integer requireId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return id;
    }

    private String limpar(String valor) {
        if (valor == null) {
            return null;
        }
        String valorLimpo = valor.trim();
        return valorLimpo.isEmpty() ? null : valorLimpo;
    }
}
