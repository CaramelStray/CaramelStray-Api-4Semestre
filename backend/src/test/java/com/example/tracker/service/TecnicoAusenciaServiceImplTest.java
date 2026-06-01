package com.example.tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.tracker.dto.tecnicoausencia.TecnicoAusenciaCreateDTO;
import com.example.tracker.entity.Tecnico;
import com.example.tracker.entity.TecnicoAusencia;
import com.example.tracker.enums.StatusAusenciaTecnico;
import com.example.tracker.enums.TipoAusenciaTecnico;
import com.example.tracker.repository.TecnicoAusenciaRepository;
import com.example.tracker.repository.TecnicoRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TecnicoAusenciaServiceImplTest {

    @Mock
    private TecnicoAusenciaRepository tecnicoAusenciaRepository;

    @Mock
    private TecnicoRepository tecnicoRepository;

    @InjectMocks
    private TecnicoAusenciaServiceImpl service;

    @Test
    void criarRegistraAusenciaAtivaParaTecnico() {
        Tecnico tecnico = tecnico(1);
        TecnicoAusenciaCreateDTO dto = dto("folga", LocalDate.of(2026, 5, 30), LocalDate.of(2026, 5, 31));

        given(tecnicoRepository.findById(1)).willReturn(Optional.of(tecnico));
        given(tecnicoAusenciaRepository.existsSobreposicaoAtiva(
                eq(1),
                eq(dto.getDataInicio()),
                eq(dto.getDataFim()),
                eq(null),
                eq(StatusAusenciaTecnico.ATIVA))).willReturn(false);
        given(tecnicoAusenciaRepository.save(any(TecnicoAusencia.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        TecnicoAusencia ausencia = service.criar(1, dto);

        assertThat(ausencia.getTecnico()).isEqualTo(tecnico);
        assertThat(ausencia.getTipo()).isEqualTo(TipoAusenciaTecnico.FOLGA);
        assertThat(ausencia.getStatus()).isEqualTo(StatusAusenciaTecnico.ATIVA);
        verify(tecnicoAusenciaRepository).save(ausencia);
    }

    @Test
    void criarBloqueiaAusenciaAtivaSobreposta() {
        Tecnico tecnico = tecnico(1);
        TecnicoAusenciaCreateDTO dto = dto("afastamento", LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 10));

        given(tecnicoRepository.findById(1)).willReturn(Optional.of(tecnico));
        given(tecnicoAusenciaRepository.existsSobreposicaoAtiva(
                eq(1),
                eq(dto.getDataInicio()),
                eq(dto.getDataFim()),
                eq(null),
                eq(StatusAusenciaTecnico.ATIVA))).willReturn(true);

        assertThatThrownBy(() -> service.criar(1, dto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    private Tecnico tecnico(Integer id) {
        Tecnico tecnico = new Tecnico();
        tecnico.setId(id);
        tecnico.setNome("Tecnico");
        return tecnico;
    }

    private TecnicoAusenciaCreateDTO dto(String tipo, LocalDate dataInicio, LocalDate dataFim) {
        TecnicoAusenciaCreateDTO dto = new TecnicoAusenciaCreateDTO();
        dto.setTipo(tipo);
        dto.setDataInicio(dataInicio);
        dto.setDataFim(dataFim);
        return dto;
    }
}
