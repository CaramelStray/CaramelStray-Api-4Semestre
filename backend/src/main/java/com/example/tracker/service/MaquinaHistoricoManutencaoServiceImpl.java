package com.example.tracker.service;

import com.example.tracker.dto.maquinahistoricomanutencao.MaquinaHistoricoManutencaoCreateDTO;
import com.example.tracker.entity.MaquinaContrato;
import com.example.tracker.entity.MaquinaHistoricoManutencao;
import com.example.tracker.entity.MaquinaSoftwareInstalado;
import com.example.tracker.entity.TipoManutencao;
import com.example.tracker.repository.MaquinaContratoRepository;
import com.example.tracker.repository.MaquinaHistoricoManutencaoRepository;
import com.example.tracker.repository.MaquinaSoftwareInstaladoRepository;
import com.example.tracker.repository.TipoManutencaoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MaquinaHistoricoManutencaoServiceImpl implements MaquinaHistoricoManutencaoService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final MaquinaHistoricoManutencaoRepository maquinaHistoricoManutencaoRepository;
    private final MaquinaContratoRepository maquinaContratoRepository;
    private final MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository;
    private final TipoManutencaoRepository tipoManutencaoRepository;
    private final EntityManager entityManager;

    public MaquinaHistoricoManutencaoServiceImpl(
            MaquinaHistoricoManutencaoRepository maquinaHistoricoManutencaoRepository,
            MaquinaContratoRepository maquinaContratoRepository,
            MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository,
            TipoManutencaoRepository tipoManutencaoRepository,
            EntityManager entityManager) {
        this.maquinaHistoricoManutencaoRepository = maquinaHistoricoManutencaoRepository;
        this.maquinaContratoRepository = maquinaContratoRepository;
        this.maquinaSoftwareInstaladoRepository = maquinaSoftwareInstaladoRepository;
        this.tipoManutencaoRepository = tipoManutencaoRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaHistoricoManutencao> listarTodos() {
        return maquinaHistoricoManutencaoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public MaquinaHistoricoManutencao buscarPorId(Integer id) {
        Integer idNaoNulo = requireId(id, "Id do historico de manutencao e obrigatorio");
        return maquinaHistoricoManutencaoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Historico de manutencao nao encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaHistoricoManutencao> buscarPorMaquinaContrato(Integer codigoMaquinaContrato) {
        Integer codigoMaquinaContratoNaoNulo =
                requireId(codigoMaquinaContrato, "Codigo da maquina do contrato e obrigatorio");
        List<MaquinaHistoricoManutencao> itens = maquinaHistoricoManutencaoRepository.findByMaquinaContratoCodigo(
                Objects.requireNonNull(codigoMaquinaContratoNaoNulo));
        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum historico encontrado para a maquina");
        }
        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaHistoricoManutencao> buscarPorSoftwareInstalado(Integer codigoSoftwareInstalado) {
        Integer codigoSoftwareInstaladoNaoNulo =
                requireId(codigoSoftwareInstalado, "Codigo do software instalado e obrigatorio");
        List<MaquinaHistoricoManutencao> itens =
                maquinaHistoricoManutencaoRepository.findBySoftwareInstaladoCodigo(codigoSoftwareInstaladoNaoNulo);
        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum historico encontrado para o software instalado");
        }
        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] gerarRelatorioPdf(
            Integer codigoMaquinaContrato,
            Integer codigoSoftwareInstalado,
            Integer codigoTipoManutencao,
            String status,
            String criticidade,
            LocalDateTime vencimentoInicio,
            LocalDateTime vencimentoFim,
            Boolean somenteVencidas,
            Integer proximosDias) {
        validarFiltrosRelatorio(vencimentoInicio, vencimentoFim, somenteVencidas, proximosDias);

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicioFiltro = vencimentoInicio;
        LocalDateTime fimFiltro = vencimentoFim;

        if (Boolean.TRUE.equals(somenteVencidas) && (fimFiltro == null || fimFiltro.isAfter(agora))) {
            fimFiltro = agora;
        }

        if (proximosDias != null) {
            LocalDateTime limiteProximosDias = agora.plusDays(proximosDias);
            if (inicioFiltro == null || inicioFiltro.isBefore(agora)) {
                inicioFiltro = agora;
            }
            if (fimFiltro == null || fimFiltro.isAfter(limiteProximosDias)) {
                fimFiltro = limiteProximosDias;
            }
        }

        List<MaquinaHistoricoManutencao> manutencoes = filtrarParaRelatorio(
                codigoMaquinaContrato,
                codigoSoftwareInstalado,
                codigoTipoManutencao,
                normalizarFiltro(status),
                normalizarFiltro(criticidade),
                inicioFiltro,
                fimFiltro);

        return MaquinaHistoricoManutencaoPdfBuilder.gerar(
                montarLinhasRelatorio(
                        manutencoes,
                        codigoMaquinaContrato,
                        codigoSoftwareInstalado,
                        codigoTipoManutencao,
                        status,
                        criticidade,
                        inicioFiltro,
                        fimFiltro,
                        Boolean.TRUE.equals(somenteVencidas),
                        proximosDias,
                        agora));
    }

    @Override
    @Transactional
    public MaquinaHistoricoManutencao cadastrar(MaquinaHistoricoManutencaoCreateDTO dto) {
        validarEntrada(dto);
        MaquinaHistoricoManutencao entidade = new MaquinaHistoricoManutencao();
        mapearParaEntidade(dto, entidade);
        return maquinaHistoricoManutencaoRepository.save(entidade);
    }

    @Override
    @Transactional
    public MaquinaHistoricoManutencao atualizar(Integer id, MaquinaHistoricoManutencaoCreateDTO dto) {
        Integer idNaoNulo = requireId(id, "Id do historico de manutencao e obrigatorio");
        validarEntrada(dto);

        MaquinaHistoricoManutencao entidade =
                maquinaHistoricoManutencaoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Historico de manutencao nao encontrado"));

        mapearParaEntidade(dto, entidade);
        return maquinaHistoricoManutencaoRepository.save(Objects.requireNonNull(entidade));
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        Integer idNaoNulo = requireId(id, "Id do historico de manutencao e obrigatorio");
        if (!maquinaHistoricoManutencaoRepository.existsById(Objects.requireNonNull(idNaoNulo))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Historico de manutencao nao encontrado");
        }
        maquinaHistoricoManutencaoRepository.deleteById(Objects.requireNonNull(idNaoNulo));
    }

    private void validarEntrada(MaquinaHistoricoManutencaoCreateDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do historico de manutencao sao obrigatorios");
        }

        Integer codigoMaquinaContratoNaoNulo =
                requireId(dto.getCodigoMaquinaContrato(), "Codigo da maquina do contrato e obrigatorio");
        Integer codigoTipoManutencaoNaoNulo =
                requireId(dto.getCodigoTipoManutencao(), "Codigo do tipo de manutencao e obrigatorio");

        Objects.requireNonNull(codigoMaquinaContratoNaoNulo);
        Objects.requireNonNull(codigoTipoManutencaoNaoNulo);

        Integer codigoSoftwareInstalado = dto.getCodigoSoftwareInstalado();
        if (codigoSoftwareInstalado != null) {
            Objects.requireNonNull(codigoSoftwareInstalado);
        }

        if (dto.getDataInicioExecucao() != null
                && dto.getDataFimExecucao() != null
                && dto.getDataFimExecucao().isBefore(dto.getDataInicioExecucao())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de fim da execucao nao pode ser anterior a data de inicio");
        }
    }

    private void mapearParaEntidade(MaquinaHistoricoManutencaoCreateDTO dto, MaquinaHistoricoManutencao entidade) {
        Integer codigoMaquinaContratoNaoNulo =
                requireId(dto.getCodigoMaquinaContrato(), "Codigo da maquina do contrato e obrigatorio");
        Integer codigoTipoManutencaoNaoNulo =
                requireId(dto.getCodigoTipoManutencao(), "Codigo do tipo de manutencao e obrigatorio");

        MaquinaContrato maquinaContrato = maquinaContratoRepository.findById(Objects.requireNonNull(codigoMaquinaContratoNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada"));
        TipoManutencao tipoManutencao = tipoManutencaoRepository.findById(Objects.requireNonNull(codigoTipoManutencaoNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de manutencao nao encontrado"));

        entidade.setMaquinaContrato(maquinaContrato);
        entidade.setTipoManutencao(tipoManutencao);

        Integer codigoSoftwareInstalado = dto.getCodigoSoftwareInstalado();
        if (codigoSoftwareInstalado != null) {
            MaquinaSoftwareInstalado softwareInstalado =
                    maquinaSoftwareInstaladoRepository.findById(codigoSoftwareInstalado).orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Software instalado nao encontrado"));
            entidade.setSoftwareInstalado(softwareInstalado);
        } else {
            entidade.setSoftwareInstalado(null);
        }

        entidade.setStatus(dto.getStatus());
        entidade.setCriticidade(dto.getCriticidade());
        entidade.setVencimento(dto.getVencimento());
        entidade.setDataAgendamento(dto.getDataAgendamento());
        entidade.setDataInicioExecucao(dto.getDataInicioExecucao());
        entidade.setDataFimExecucao(dto.getDataFimExecucao());
        entidade.setObservacaoGeral(dto.getObservacaoGeral());
    }

    private void validarFiltrosRelatorio(
            LocalDateTime vencimentoInicio,
            LocalDateTime vencimentoFim,
            Boolean somenteVencidas,
            Integer proximosDias) {
        if (vencimentoInicio != null && vencimentoFim != null && vencimentoFim.isBefore(vencimentoInicio)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data final do vencimento nao pode ser anterior a data inicial");
        }

        if (proximosDias != null && proximosDias < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proximos dias deve ser maior ou igual a zero");
        }

        if (Boolean.TRUE.equals(somenteVencidas) && proximosDias != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe somente um filtro entre somenteVencidas e proximosDias");
        }
    }

    private List<String> montarLinhasRelatorio(
            List<MaquinaHistoricoManutencao> manutencoes,
            Integer codigoMaquinaContrato,
            Integer codigoSoftwareInstalado,
            Integer codigoTipoManutencao,
            String status,
            String criticidade,
            LocalDateTime vencimentoInicio,
            LocalDateTime vencimentoFim,
            boolean somenteVencidas,
            Integer proximosDias,
            LocalDateTime agora) {
        long totalVencidas = manutencoes.stream()
                .filter(manutencao -> manutencao.getVencimento() != null
                        && manutencao.getVencimento().isBefore(agora)
                        && !isStatusFinalizado(manutencao.getStatus()))
                .count();
        long totalProximas = manutencoes.stream()
                .filter(manutencao -> manutencao.getVencimento() != null
                        && !manutencao.getVencimento().isBefore(agora)
                        && !manutencao.getVencimento().isAfter(agora.plusDays(30))
                        && !isStatusFinalizado(manutencao.getStatus()))
                .count();
        long totalFinalizadas = manutencoes.stream()
                .filter(manutencao -> isStatusFinalizado(manutencao.getStatus()))
                .count();

        List<String> linhas = new java.util.ArrayList<>();
        linhas.add("Relatorio de manutencoes");
        linhas.add("Gerado em: " + formatarData(agora));
        linhas.add("");
        linhas.add("Filtros");
        linhas.add("Codigo da maquina do contrato: " + valor(codigoMaquinaContrato));
        linhas.add("Codigo do software instalado: " + valor(codigoSoftwareInstalado));
        linhas.add("Codigo do tipo de manutencao: " + valor(codigoTipoManutencao));
        linhas.add("Status: " + valor(status));
        linhas.add("Criticidade: " + valor(criticidade));
        linhas.add("Vencimento inicial: " + formatarData(vencimentoInicio));
        linhas.add("Vencimento final: " + formatarData(vencimentoFim));
        linhas.add("Somente vencidas: " + (somenteVencidas ? "Sim" : "Nao"));
        linhas.add("Proximos dias: " + valor(proximosDias));
        linhas.add("");
        linhas.add("Resumo");
        linhas.add("Total de manutencoes: " + manutencoes.size());
        linhas.add("Vencidas em aberto: " + totalVencidas);
        linhas.add("Proximas em 30 dias: " + totalProximas);
        linhas.add("Finalizadas: " + totalFinalizadas);
        linhas.add("");
        linhas.add("Manutencoes");

        if (manutencoes.isEmpty()) {
            linhas.add("Nenhuma manutencao encontrada para os filtros informados.");
            return linhas;
        }

        for (MaquinaHistoricoManutencao manutencao : manutencoes) {
            linhas.add("");
            linhas.add("Codigo: " + valor(manutencao.getCodigo())
                    + " | Status: " + valor(manutencao.getStatus())
                    + " | Criticidade: " + valor(manutencao.getCriticidade()));
            linhas.add("Vencimento: " + formatarData(manutencao.getVencimento())
                    + " | Agendamento: " + formatarData(manutencao.getDataAgendamento()));
            linhas.add("Inicio execucao: " + formatarData(manutencao.getDataInicioExecucao())
                    + " | Fim execucao: " + formatarData(manutencao.getDataFimExecucao()));
            linhas.add("Maquina: " + descreverMaquina(manutencao));
            linhas.add("Tipo manutencao: " + descreverTipoManutencao(manutencao));
            linhas.add("Software instalado: " + descreverSoftware(manutencao));
            linhas.add("Observacao: " + valor(manutencao.getObservacaoGeral()));
        }

        return linhas;
    }

    private String descreverMaquina(MaquinaHistoricoManutencao manutencao) {
        if (manutencao.getMaquinaContrato() == null) {
            return "-";
        }

        String descricao = manutencao.getMaquinaContrato().getCatalogoMaquina() != null
                ? manutencao.getMaquinaContrato().getCatalogoMaquina().getDescricao()
                : "-";
        return "Codigo " + valor(manutencao.getMaquinaContrato().getCodigo())
                + " | " + valor(descricao)
                + " | Serie " + valor(manutencao.getMaquinaContrato().getNumeroSerie());
    }

    private String descreverTipoManutencao(MaquinaHistoricoManutencao manutencao) {
        if (manutencao.getTipoManutencao() == null) {
            return "-";
        }
        return "Codigo " + valor(manutencao.getTipoManutencao().getCodigo())
                + " | " + valor(manutencao.getTipoManutencao().getDescricao());
    }

    private String descreverSoftware(MaquinaHistoricoManutencao manutencao) {
        if (manutencao.getSoftwareInstalado() == null) {
            return "-";
        }

        String software = manutencao.getSoftwareInstalado().getSoftware() != null
                ? manutencao.getSoftwareInstalado().getSoftware().getNome()
                : "-";
        return "Codigo " + valor(manutencao.getSoftwareInstalado().getCodigo())
                + " | " + valor(software)
                + " | Versao " + valor(manutencao.getSoftwareInstalado().getVersaoInstalada());
    }

    private boolean isStatusFinalizado(String status) {
        String statusNormalizado = normalizarFiltro(status);
        if (statusNormalizado == null) {
            return false;
        }
        return statusNormalizado.contains("conclu") || statusNormalizado.contains("finaliz");
    }

    private String normalizarFiltro(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim().toLowerCase(Locale.ROOT);
    }

    private List<MaquinaHistoricoManutencao> filtrarParaRelatorio(
            Integer codigoMaquinaContrato,
            Integer codigoSoftwareInstalado,
            Integer codigoTipoManutencao,
            String status,
            String criticidade,
            LocalDateTime vencimentoInicio,
            LocalDateTime vencimentoFim) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MaquinaHistoricoManutencao> query = cb.createQuery(MaquinaHistoricoManutencao.class);
        Root<MaquinaHistoricoManutencao> root = query.from(MaquinaHistoricoManutencao.class);

        root.fetch("maquinaContrato", JoinType.LEFT).fetch("catalogoMaquina", JoinType.LEFT);
        root.fetch("softwareInstalado", JoinType.LEFT).fetch("software", JoinType.LEFT);
        root.fetch("tipoManutencao", JoinType.LEFT);

        Join<MaquinaHistoricoManutencao, MaquinaContrato> maquinaContrato =
                root.join("maquinaContrato", JoinType.LEFT);
        Join<MaquinaHistoricoManutencao, MaquinaSoftwareInstalado> softwareInstalado =
                root.join("softwareInstalado", JoinType.LEFT);
        Join<MaquinaHistoricoManutencao, TipoManutencao> tipoManutencao =
                root.join("tipoManutencao", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        if (codigoMaquinaContrato != null) {
            predicates.add(cb.equal(maquinaContrato.get("codigo"), codigoMaquinaContrato));
        }
        if (codigoSoftwareInstalado != null) {
            predicates.add(cb.equal(softwareInstalado.get("codigo"), codigoSoftwareInstalado));
        }
        if (codigoTipoManutencao != null) {
            predicates.add(cb.equal(tipoManutencao.get("codigo"), codigoTipoManutencao));
        }
        if (status != null) {
            predicates.add(cb.equal(cb.lower(root.<String>get("status")), status));
        }
        if (criticidade != null) {
            predicates.add(cb.equal(cb.lower(root.<String>get("criticidade")), criticidade));
        }
        if (vencimentoInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("vencimento"), vencimentoInicio));
        }
        if (vencimentoFim != null) {
            predicates.add(cb.lessThanOrEqualTo(root.<LocalDateTime>get("vencimento"), vencimentoFim));
        }

        query.select(root)
                .distinct(true)
                .where(predicates.toArray(Predicate[]::new))
                .orderBy(cb.asc(root.get("vencimento")), cb.asc(root.get("codigo")));

        return entityManager.createQuery(query).getResultList();
    }

    private String formatarData(LocalDateTime data) {
        if (data == null) {
            return "-";
        }
        return data.format(DATE_TIME_FORMATTER);
    }

    private String valor(Object valor) {
        return valor != null ? String.valueOf(valor) : "-";
    }

    private Integer requireId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return id;
    }
}
