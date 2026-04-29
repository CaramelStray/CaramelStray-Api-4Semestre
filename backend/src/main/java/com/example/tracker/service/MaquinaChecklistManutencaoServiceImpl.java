package com.example.tracker.service;

import com.example.tracker.dto.maquinachecklistmanutencao.MaquinaChecklistManutencaoUpdateDTO;
import com.example.tracker.entity.CatalogoMaquina;
import com.example.tracker.entity.CatalogoMaquinaChecklistPadrao;
import com.example.tracker.entity.MaquinaChecklistManutencao;
import com.example.tracker.entity.MaquinaHistoricoManutencao;
import com.example.tracker.entity.Tecnico;
import com.example.tracker.repository.MaquinaChecklistManutencaoRepository;
import com.example.tracker.repository.MaquinaHistoricoManutencaoRepository;
import com.example.tracker.repository.TecnicoRepository;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MaquinaChecklistManutencaoServiceImpl implements MaquinaChecklistManutencaoService {

    private static final Set<String> STATUS_BLOQUEADOS = Set.of("FINALIZADA", "FINALIZADO", "CANCELADA", "CANCELADO");
    private static final int TAMANHO_MAXIMO_OBSERVACAO = 2000;

    private final MaquinaChecklistManutencaoRepository maquinaChecklistManutencaoRepository;
    private final MaquinaHistoricoManutencaoRepository maquinaHistoricoManutencaoRepository;
    private final TecnicoRepository tecnicoRepository;

    public MaquinaChecklistManutencaoServiceImpl(
            MaquinaChecklistManutencaoRepository maquinaChecklistManutencaoRepository,
            MaquinaHistoricoManutencaoRepository maquinaHistoricoManutencaoRepository,
            TecnicoRepository tecnicoRepository) {
        this.maquinaChecklistManutencaoRepository = maquinaChecklistManutencaoRepository;
        this.maquinaHistoricoManutencaoRepository = maquinaHistoricoManutencaoRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaChecklistManutencao> listarPorHistorico(Integer codigoHistoricoManutencao) {
        Integer codigoHistoricoNaoNulo =
                requireId(codigoHistoricoManutencao, "Codigo do historico de manutencao e obrigatorio");

        MaquinaHistoricoManutencao historico = buscarHistorico(codigoHistoricoNaoNulo);
        validarAcessoAoHistorico(historico);

        return maquinaChecklistManutencaoRepository.findByHistoricoManutencaoCodigoOrderByCodigoAsc(codigoHistoricoNaoNulo);
    }

    @Override
    @Transactional
    public List<MaquinaChecklistManutencao> gerarChecklistPadrao(Integer codigoHistoricoManutencao) {
        Integer codigoHistoricoNaoNulo =
                requireId(codigoHistoricoManutencao, "Codigo do historico de manutencao e obrigatorio");

        MaquinaHistoricoManutencao historico = buscarHistorico(codigoHistoricoNaoNulo);
        validarAcessoAoHistorico(historico);
        validarHistoricoEditavel(historico);

        CatalogoMaquina catalogoMaquina = historico.getMaquinaContrato() != null
                ? historico.getMaquinaContrato().getCatalogoMaquina()
                : null;

        if (catalogoMaquina == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nao foi possivel identificar o catalogo da maquina vinculado ao historico");
        }

        List<CatalogoMaquinaChecklistPadrao> checklistPadrao = catalogoMaquina.getChecklistPadrao();
        if (checklistPadrao == null || checklistPadrao.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A maquina vinculada ao historico nao possui checklist padrao cadastrado");
        }

        for (CatalogoMaquinaChecklistPadrao itemPadrao : checklistPadrao) {
            if (itemPadrao == null || itemPadrao.getTarefa() == null || itemPadrao.getTarefa().getId() == null) {
                continue;
            }

            Integer codigoTarefa = itemPadrao.getTarefa().getId();
            if (maquinaChecklistManutencaoRepository.existsByHistoricoManutencaoCodigoAndTarefaId(
                    codigoHistoricoNaoNulo,
                    codigoTarefa)) {
                continue;
            }

            MaquinaChecklistManutencao item = new MaquinaChecklistManutencao();
            item.setHistoricoManutencao(historico);
            item.setTarefa(itemPadrao.getTarefa());
            item.setRealizado(null);
            item.setObservacaoTarefa(null);
            maquinaChecklistManutencaoRepository.save(item);
        }

        return maquinaChecklistManutencaoRepository.findByHistoricoManutencaoCodigoOrderByCodigoAsc(codigoHistoricoNaoNulo);
    }

    @Override
    @Transactional
    public MaquinaChecklistManutencao atualizarItem(
            Integer codigoHistoricoManutencao,
            Integer codigoChecklist,
            MaquinaChecklistManutencaoUpdateDTO dto) {
        Integer codigoHistoricoNaoNulo =
                requireId(codigoHistoricoManutencao, "Codigo do historico de manutencao e obrigatorio");
        Integer codigoChecklistNaoNulo = requireId(codigoChecklist, "Codigo do item do checklist e obrigatorio");

        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do checklist sao obrigatorios");
        }

        MaquinaChecklistManutencao item = maquinaChecklistManutencaoRepository.findById(codigoChecklistNaoNulo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item do checklist nao encontrado"));

        if (item.getHistoricoManutencao() == null
                || !Objects.equals(item.getHistoricoManutencao().getCodigo(), codigoHistoricoNaoNulo)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O item do checklist informado nao pertence ao historico de manutencao informado");
        }

        MaquinaHistoricoManutencao historico = item.getHistoricoManutencao();
        validarAcessoAoHistorico(historico);
        validarHistoricoEditavel(historico);

        String observacaoTratada = limparObservacao(dto.getObservacaoTarefa());
        if (Boolean.FALSE.equals(dto.getRealizado()) && observacaoTratada == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "E obrigatorio informar observacao quando a tarefa nao for realizada");
        }

        item.setRealizado(dto.getRealizado());
        item.setObservacaoTarefa(observacaoTratada);
        return maquinaChecklistManutencaoRepository.save(item);
    }

    private MaquinaHistoricoManutencao buscarHistorico(Integer codigoHistoricoManutencao) {
        return maquinaHistoricoManutencaoRepository.findById(codigoHistoricoManutencao)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Historico de manutencao nao encontrado"));
    }

    private void validarAcessoAoHistorico(MaquinaHistoricoManutencao historico) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario nao autenticado");
        }

        if (possuiAuthority(authentication, "ROLE_ADMIN")) {
            return;
        }

        if (!possuiAuthority(authentication, "ROLE_TECNICO")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario sem permissao para acessar o checklist");
        }

        String email = authentication.getName();
        Tecnico tecnico = tecnicoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Tecnico autenticado nao encontrado"));

        boolean vinculado = maquinaHistoricoManutencaoRepository.existsTecnicoVinculado(
                historico.getCodigo(),
                tecnico.getId());

        if (!vinculado) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "O tecnico autenticado nao possui acesso a este historico de manutencao");
        }
    }

    private void validarHistoricoEditavel(MaquinaHistoricoManutencao historico) {
        String statusNormalizado = normalizarStatus(historico.getStatus());
        if (statusNormalizado != null && STATUS_BLOQUEADOS.contains(statusNormalizado)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nao e permitido alterar checklist de manutencao finalizada ou cancelada");
        }
    }

    private boolean possuiAuthority(Authentication authentication, String authority) {
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (authority.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private String limparObservacao(String observacao) {
        if (observacao == null) {
            return null;
        }

        String observacaoLimpa = observacao.trim();
        if (observacaoLimpa.isEmpty()) {
            return null;
        }

        if (observacaoLimpa.length() > TAMANHO_MAXIMO_OBSERVACAO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A observacao da tarefa nao pode exceder " + TAMANHO_MAXIMO_OBSERVACAO + " caracteres");
        }

        return observacaoLimpa;
    }

    private String normalizarStatus(String status) {
        if (status == null) {
            return null;
        }

        String statusLimpo = status.trim();
        if (statusLimpo.isEmpty()) {
            return null;
        }

        return statusLimpo.toUpperCase(Locale.ROOT);
    }

    private Integer requireId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return id;
    }
}
