package com.example.tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

import com.example.tracker.dto.tecnico.TecnicoResponseDTO;
import com.example.tracker.entity.Tecnico;
import com.example.tracker.enums.StatusAusenciaTecnico;
import com.example.tracker.repository.MaquinaHistoricoManutencaoRepository;
import com.example.tracker.repository.OrdemServicoRepository;
import com.example.tracker.repository.PerfilRepository;
import com.example.tracker.repository.TecnicoAusenciaRepository;
import com.example.tracker.repository.TecnicoRepository;
import com.example.tracker.repository.UsuarioRepository;
import com.example.tracker.repository.ViagemRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class TecnicoServiceImplTest {

    private static final String STATUS_DISPONIVEL = "DISPONIVEL";
    private static final String STATUS_EM_ATENDIMENTO = "EM_ATENDIMENTO";
    private static final String STATUS_EM_ROTA = "EM_ROTA";
    private static final String STATUS_INDISPONIVEL = "INDISPONIVEL";

    @Mock
    private TecnicoRepository tecnicoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private MaquinaHistoricoManutencaoRepository maquinaHistoricoManutencaoRepository;

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private TecnicoAusenciaRepository tecnicoAusenciaRepository;

    @InjectMocks
    private TecnicoServiceImpl tecnicoService;

    @Test
    void listarTecnicosRetornaEmRotaQuandoExisteViagemAtiva() {
        Tecnico tecnico = tecnico(1, STATUS_DISPONIVEL);
        given(tecnicoRepository.findAllComHabilidades()).willReturn(List.of(tecnico));
        given(viagemRepository.existsRotaAtivaPorTecnico(1)).willReturn(true);

        List<TecnicoResponseDTO> resposta = tecnicoService.listarTecnicos();

        assertThat(resposta).hasSize(1);
        assertThat(resposta.get(0).getDisponibilidade()).isEqualTo(STATUS_EM_ROTA);
        verifyNoInteractions(ordemServicoRepository, maquinaHistoricoManutencaoRepository);
    }

    @Test
    void listarTecnicosRetornaEmAtendimentoQuandoExisteOrdemAtiva() {
        Tecnico tecnico = tecnico(1, STATUS_DISPONIVEL);
        given(tecnicoRepository.findAllComHabilidades()).willReturn(List.of(tecnico));
        given(viagemRepository.existsRotaAtivaPorTecnico(1)).willReturn(false);
        given(ordemServicoRepository.existsManutencaoAtivaPorTecnico(1)).willReturn(true);

        List<TecnicoResponseDTO> resposta = tecnicoService.listarTecnicos();

        assertThat(resposta).hasSize(1);
        assertThat(resposta.get(0).getDisponibilidade()).isEqualTo(STATUS_EM_ATENDIMENTO);
        verifyNoInteractions(maquinaHistoricoManutencaoRepository);
    }

    @Test
    void listarTecnicosRetornaEmAtendimentoQuandoExisteHistoricoAtivo() {
        Tecnico tecnico = tecnico(1, STATUS_DISPONIVEL);
        given(tecnicoRepository.findAllComHabilidades()).willReturn(List.of(tecnico));
        given(viagemRepository.existsRotaAtivaPorTecnico(1)).willReturn(false);
        given(ordemServicoRepository.existsManutencaoAtivaPorTecnico(1)).willReturn(false);
        given(maquinaHistoricoManutencaoRepository.existsHistoricoAtivoPorTecnico(1)).willReturn(true);

        List<TecnicoResponseDTO> resposta = tecnicoService.listarTecnicos();

        assertThat(resposta).hasSize(1);
        assertThat(resposta.get(0).getDisponibilidade()).isEqualTo(STATUS_EM_ATENDIMENTO);
    }

    @Test
    void listarTecnicosRetornaDisponivelQuandoNaoExisteAtividadeAtiva() {
        Tecnico tecnico = tecnico(1, STATUS_EM_ROTA);
        given(tecnicoRepository.findAllComHabilidades()).willReturn(List.of(tecnico));
        given(viagemRepository.existsRotaAtivaPorTecnico(1)).willReturn(false);
        given(ordemServicoRepository.existsManutencaoAtivaPorTecnico(1)).willReturn(false);
        given(maquinaHistoricoManutencaoRepository.existsHistoricoAtivoPorTecnico(1)).willReturn(false);
        given(tecnicoAusenciaRepository.existsAusenciaAtivaNaData(1, LocalDate.now(), StatusAusenciaTecnico.ATIVA))
                .willReturn(false);

        List<TecnicoResponseDTO> resposta = tecnicoService.listarTecnicos();

        assertThat(resposta).hasSize(1);
        assertThat(resposta.get(0).getDisponibilidade()).isEqualTo(STATUS_DISPONIVEL);
    }

    @Test
    void listarTecnicosRetornaIndisponivelQuandoExisteAusenciaAtivaHoje() {
        Tecnico tecnico = tecnico(1, STATUS_DISPONIVEL);
        given(tecnicoRepository.findAllComHabilidades()).willReturn(List.of(tecnico));
        given(viagemRepository.existsRotaAtivaPorTecnico(1)).willReturn(false);
        given(ordemServicoRepository.existsManutencaoAtivaPorTecnico(1)).willReturn(false);
        given(maquinaHistoricoManutencaoRepository.existsHistoricoAtivoPorTecnico(1)).willReturn(false);
        given(tecnicoAusenciaRepository.existsAusenciaAtivaNaData(1, LocalDate.now(), StatusAusenciaTecnico.ATIVA))
                .willReturn(true);

        List<TecnicoResponseDTO> resposta = tecnicoService.listarTecnicos();

        assertThat(resposta).hasSize(1);
        assertThat(resposta.get(0).getDisponibilidade()).isEqualTo(STATUS_INDISPONIVEL);
    }

    @Test
    void listarTecnicosPreservaStatusManualQuandoNaoExisteAtividadeAtiva() {
        Tecnico tecnico = tecnico(1, STATUS_INDISPONIVEL);
        given(tecnicoRepository.findAllComHabilidades()).willReturn(List.of(tecnico));
        given(viagemRepository.existsRotaAtivaPorTecnico(1)).willReturn(false);
        given(ordemServicoRepository.existsManutencaoAtivaPorTecnico(1)).willReturn(false);
        given(maquinaHistoricoManutencaoRepository.existsHistoricoAtivoPorTecnico(1)).willReturn(false);
        given(tecnicoAusenciaRepository.existsAusenciaAtivaNaData(1, LocalDate.now(), StatusAusenciaTecnico.ATIVA))
                .willReturn(false);

        List<TecnicoResponseDTO> resposta = tecnicoService.listarTecnicos();

        assertThat(resposta).hasSize(1);
        assertThat(resposta.get(0).getDisponibilidade()).isEqualTo(STATUS_INDISPONIVEL);
    }

    private Tecnico tecnico(Integer id, String disponibilidade) {
        Tecnico tecnico = new Tecnico();
        tecnico.setId(id);
        tecnico.setNome("Tecnico");
        tecnico.setDisponibilidade(disponibilidade);
        return tecnico;
    }
}
