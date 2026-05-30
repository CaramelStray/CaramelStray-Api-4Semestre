package com.example.tracker.service;

import com.example.tracker.dto.dashboard.DashboardCardDTO;
import com.example.tracker.dto.maquinachecklistmanutencao.MaquinaChecklistManutencaoResponseDTO;
import com.example.tracker.dto.ordemservico.TecnicosOrdensResponseDTO;
import com.example.tracker.dto.ordemservico.OrdemServicoCreateDTO;
import com.example.tracker.dto.ordemservico.OrdemServicoDadosBasicosResponseDTO;
import com.example.tracker.dto.ordemservico.OrdemServicoResponseDTO;
import com.example.tracker.entity.Cliente;
import com.example.tracker.entity.Contrato;
import com.example.tracker.entity.MaquinaContrato;
import com.example.tracker.entity.MaquinaHistoricoManutencao;
import com.example.tracker.entity.MaquinaSoftwareInstalado;
import com.example.tracker.entity.OrdemServico;
import com.example.tracker.entity.OrdemServicoChecklistAtivo;
import com.example.tracker.entity.OrdemServicoTecnico;
import com.example.tracker.entity.Tecnico;
import com.example.tracker.enums.StatusTecnico;
import com.example.tracker.repository.ClienteRepository;
import com.example.tracker.repository.ContratoRepository;
import com.example.tracker.repository.MaquinaContratoRepository;
import com.example.tracker.repository.MaquinaChecklistManutencaoRepository;
import com.example.tracker.repository.MaquinaHistoricoManutencaoRepository;
import com.example.tracker.repository.MaquinaSoftwareInstaladoRepository;
import com.example.tracker.repository.OrdemServicoChecklistAtivoRepository;
import com.example.tracker.repository.OrdemServicoRepository;
import com.example.tracker.repository.OrdemServicoTecnicoRepository;
import com.example.tracker.repository.TecnicoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrdemServicoServiceImpl implements OrdemServicoService {

    private static final String STATUS_ABERTA = "ABERTA";
    private static final String STATUS_AGENDADO = "AGENDADO";
    private static final String STATUS_EM_PREPARACAO = "EM_PREPARACAO";
    private static final String STATUS_EM_EXECUCAO = "EM_EXECUCAO";
    private static final String STATUS_FINALIZADA = "FINALIZADA";
    private static final String STATUS_CANCELADA = "CANCELADA";

    private static final String TECNICO_DISPONIVEL = "DISPONIVEL";
    private static final String TECNICO_EM_ATENDIMENTO = "EM_ATENDIMENTO";

    private static final Set<String> STATUS_ORDEM_PERMITIDOS =
            Set.of(STATUS_ABERTA, STATUS_AGENDADO, STATUS_EM_PREPARACAO, STATUS_EM_EXECUCAO, STATUS_FINALIZADA, STATUS_CANCELADA);
    private static final Set<String> CRITICIDADES_PERMITIDAS =
            Set.of("CRITICA", "ALTA", "MEDIA", "BAIXA");
    private static final Set<String> TIPOS_ORDEM_PERMITIDOS =
            Set.of("MANUTENCAO_PREVENTIVA", "MANUTENCAO_CORRETIVA", "INSTALACAO");
    private static final List<String> STATUS_ORDEM_BLOQUEADOS =
            List.of(STATUS_FINALIZADA, STATUS_CANCELADA, "FINALIZADO", "CANCELADO", "CONCLUIDA");
    private static final List<String> STATUS_ORDEM_TECNICO_EM_ATENDIMENTO =
            List.of(STATUS_EM_PREPARACAO, STATUS_EM_EXECUCAO);

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository;
    private final ContratoRepository contratoRepository;
    private final MaquinaContratoRepository maquinaContratoRepository;
    private final OrdemServicoChecklistAtivoService ordemServicoChecklistAtivoService;
    private final MaquinaHistoricoManutencaoRepository maquinaHistoricoManutencaoRepository;
    private final MaquinaChecklistManutencaoService maquinaChecklistManutencaoService;
    private final OrdemServicoChecklistAtivoRepository ordemServicoChecklistAtivoRepository;
    private final MaquinaChecklistManutencaoRepository maquinaChecklistManutencaoRepository;
    private final OrdemServicoTecnicoRepository ordemServicoTecnicoRepository;

    public OrdemServicoServiceImpl(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            TecnicoRepository tecnicoRepository,
            MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository,
            ContratoRepository contratoRepository,
            MaquinaContratoRepository maquinaContratoRepository,
            OrdemServicoChecklistAtivoService ordemServicoChecklistAtivoService,
            MaquinaHistoricoManutencaoRepository maquinaHistoricoManutencaoRepository,
            MaquinaChecklistManutencaoService maquinaChecklistManutencaoService,
            OrdemServicoChecklistAtivoRepository ordemServicoChecklistAtivoRepository,
            MaquinaChecklistManutencaoRepository maquinaChecklistManutencaoRepository,
            OrdemServicoTecnicoRepository ordemServicoTecnicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.maquinaSoftwareInstaladoRepository = maquinaSoftwareInstaladoRepository;
        this.contratoRepository = contratoRepository;
        this.maquinaContratoRepository = maquinaContratoRepository;
        this.ordemServicoChecklistAtivoService = ordemServicoChecklistAtivoService;
        this.maquinaHistoricoManutencaoRepository = maquinaHistoricoManutencaoRepository;
        this.maquinaChecklistManutencaoService = maquinaChecklistManutencaoService;
        this.ordemServicoChecklistAtivoRepository = ordemServicoChecklistAtivoRepository;
        this.maquinaChecklistManutencaoRepository = maquinaChecklistManutencaoRepository;
        this.ordemServicoTecnicoRepository = ordemServicoTecnicoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> listarTodos() {
        return ordemServicoRepository.findAll();
    }

   @Override
@Transactional(readOnly = true)
public List<TecnicosOrdensResponseDTO> buscarMinhasOrdens(String emailUsuario) {

    List<TecnicosOrdensResponseDTO> ordens = tecnicoRepository
            .findByUsuarioEmail(emailUsuario)
            .map(tecnico -> ordemServicoRepository.findByFuncionarioId(tecnico.getId())
                    .stream()
                    .map(TecnicosOrdensResponseDTO::fromEntity)
                    .toList())
            .orElse(List.of());

    Map<LocalDate, Long> quantidadePorDia = ordens.stream()
            .filter(o -> o.getDataAgendamento() != null)
            .collect(Collectors.groupingBy(
                    o -> o.getDataAgendamento().toLocalDate(),
                    Collectors.counting()
            ));

    ordens.forEach(ordem -> {
        if (ordem.getDataAgendamento() == null) {
            ordem.setPossuiConflito(false);
            return;
        }

        ordem.setPossuiConflito(
                quantidadePorDia.get(ordem.getDataAgendamento().toLocalDate()) > 1
        );
    });

    return ordens;
}
    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Data de inicio e data de fim sao obrigatorias");
        }

        if (dataFim.isBefore(dataInicio)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de fim nao pode ser anterior a data de inicio");
        }

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.plusDays(1).atStartOfDay().minusNanos(1);

        return ordemServicoRepository.findByDataAberturaBetween(inicio, fim);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TecnicosOrdensResponseDTO> buscarMinhasOrdens(String emailUsuario) {
        return tecnicoRepository.findByUsuarioEmail(emailUsuario)
                .map(tecnico -> ordemServicoRepository.findByFuncionarioParticipanteId(tecnico.getId())
                        .stream()
                        .map(TecnicosOrdensResponseDTO::fromEntity)
                        .toList())
                .orElse(List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemServico buscarTecnicoOrdem(Integer id, String emailUsuario) {
        Integer idNaoNulo = requireId(id, "Id da ordem de servico e obrigatorio");

        Tecnico tecnico = tecnicoRepository.findByUsuarioEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Tecnico nao encontrado para o usuario autenticado"));

        OrdemServico ordemServico = ordemServicoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));

        if (ordemServico.getFuncionario() == null
                || !Objects.equals(ordemServico.getFuncionario().getId(), tecnico.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acesso negado: esta ordem nao esta atribuida a voce");
        }

        return ordemServico;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServicoDadosBasicosResponseDTO> listarDadosBasicos() {
        return ordemServicoRepository.findAll().stream()
                .map(OrdemServicoDadosBasicosResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemServico buscarPorId(Integer id) {
        Integer idNaoNulo = requireId(id, "Id da ordem de servico e obrigatorio");
        return ordemServicoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscarCompletoPorId(Integer id) {
        Integer idNaoNulo = requireId(id, "Id da ordem de servico e obrigatorio");

        OrdemServico os = ordemServicoRepository.findByIdCompleto(idNaoNulo)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));

        return OrdemServicoResponseDTO.fromEntity(os);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemServicoDadosBasicosResponseDTO buscarDadosBasicosPorId(Integer id) {
        return OrdemServicoDadosBasicosResponseDTO.fromEntity(buscarPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardCardDTO> obterDashboardOrdens() {
        long totalOrdens = ordemServicoRepository.count();
        long aguardando = ordemServicoRepository.countByStatus("AGUARDANDO");
        long emExecucao = ordemServicoRepository.countByStatus("EM_EXECUCAO");
        long finalizadas = ordemServicoRepository.countByStatusIn(List.of("CONCLUIDA", "FINALIZADA"));

        return List.of(
                DashboardCardDTO.of("Total de Ordens", Math.toIntExact(totalOrdens), "Cadastradas no sistema", "text-blue-400", "ClipboardList"),
                DashboardCardDTO.of("Aguardando", Math.toIntExact(aguardando), "Pendentes de execução", "text-amber-400", "Clock"),
                DashboardCardDTO.of("Em Execução", Math.toIntExact(emExecucao), "Em andamento", "text-green-400", "AlertTriangle"),
                DashboardCardDTO.of("Finalizadas", Math.toIntExact(finalizadas), "Ordens fechadas", "text-purple-400", "CheckCircle2")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorCliente(Integer codigoCliente) {
        Integer codigoClienteNaoNulo = requireId(codigoCliente, "Codigo do cliente e obrigatorio");

        List<OrdemServico> itens = ordemServicoRepository.findByClienteId(Objects.requireNonNull(codigoClienteNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nenhuma ordem de servico encontrada para o cliente");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorFuncionario(Integer codigoFuncionario) {
        Integer codigoFuncionarioNaoNulo = requireId(codigoFuncionario, "Codigo do funcionario e obrigatorio");

        List<OrdemServico> itens =
                ordemServicoRepository.findByFuncionarioParticipanteId(Objects.requireNonNull(codigoFuncionarioNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nenhuma ordem de servico encontrada para o funcionario");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorSoftwareInstalado(Integer codigoSoftwareInstalado) {
        Integer codigoSoftwareInstaladoNaoNulo = requireId(codigoSoftwareInstalado,
                "Codigo do software instalado e obrigatorio");

        List<OrdemServico> itens = ordemServicoRepository
                .findBySoftwareInstaladoCodigo(Objects.requireNonNull(codigoSoftwareInstaladoNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nenhuma ordem de servico encontrada para o software instalado");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorContrato(Integer codigoContrato) {
        Integer codigoContratoNaoNulo = requireId(codigoContrato, "Codigo do contrato e obrigatorio");

        List<OrdemServico> itens = ordemServicoRepository
                .findByContratoCodigo(Objects.requireNonNull(codigoContratoNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nenhuma ordem de servico encontrada para o contrato");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorMaquinaContrato(Integer codigoMaquinaContrato) {
        Integer codigoMaquinaContratoNaoNulo = requireId(codigoMaquinaContrato,
                "Codigo da maquina do contrato e obrigatorio");

        List<OrdemServico> itens = ordemServicoRepository
                .findByMaquinaContratoCodigo(Objects.requireNonNull(codigoMaquinaContratoNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nenhuma ordem de servico encontrada para a maquina do contrato");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorStatus(String status) {

        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status e obrigatorio");
        }

        List<OrdemServico> itens = ordemServicoRepository.findByStatus(status.trim().toUpperCase());

        if (itens.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Nenhuma ordem de servico encontrada para o status informado");
        }

        return itens;
    }

    @Override
    @Transactional
    public OrdemServico cadastrar(OrdemServicoCreateDTO dto) {
        RelacoesOrdemServico relacoes = validarEntrada(dto, null);

        OrdemServico entidade = new OrdemServico();
        mapearParaEntidade(dto, entidade, relacoes);

        if (STATUS_FINALIZADA.equals(entidade.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao e permitido criar uma ordem ja finalizada");
        }

        if (entidade.getDataAbertura() == null) {
            entidade.setDataAbertura(LocalDateTime.now());
        }

        OrdemServico ordemServicoSalva = ordemServicoRepository.save(entidade);
        sincronizarTecnicos(ordemServicoSalva, relacoes.tecnicos());
        sincronizarChecklistSeInformado(dto, ordemServicoSalva);
        criarHistoricoManutencaoVinculado(ordemServicoSalva);
        atualizarDisponibilidadeTecnicos(relacoes.tecnicos(), ordemServicoSalva);
        return ordemServicoSalva;
    }

    @Override
    @Transactional
    public OrdemServico atualizar(Integer id, OrdemServicoCreateDTO dto) {
        Integer idNaoNulo = requireId(id, "Id da ordem de servico e obrigatorio");

        OrdemServico entidade = ordemServicoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));

        List<Tecnico> tecnicosAnteriores = listarTecnicosDaOrdem(entidade);
        RelacoesOrdemServico relacoes = validarEntrada(dto, entidade);
        mapearParaEntidade(dto, entidade, relacoes);
        if (STATUS_FINALIZADA.equals(entidade.getStatus())) {
            validarPodeFinalizar(entidade);
        }
        OrdemServico ordemServicoSalva = ordemServicoRepository.save(Objects.requireNonNull(entidade));
        sincronizarTecnicos(ordemServicoSalva, relacoes.tecnicos());
        sincronizarChecklistSeInformado(dto, ordemServicoSalva);
        sincronizarHistorico(ordemServicoSalva);
        atualizarDisponibilidadeTecnicos(unirTecnicos(tecnicosAnteriores, relacoes.tecnicos()), ordemServicoSalva);
        return ordemServicoSalva;
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        Integer idNaoNulo = requireId(id, "Id da ordem de servico e obrigatorio");

        if (!ordemServicoRepository.existsById(Objects.requireNonNull(idNaoNulo))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada");
        }

        ordemServicoRepository.deleteById(Objects.requireNonNull(idNaoNulo));
    }

    @Override
    @Transactional
    public OrdemServico atualizarStatusTecnico(Integer id, String novoStatus, String emailUsuario) {
        Integer idNaoNulo = requireId(id, "Id da ordem de servico e obrigatorio");

        String statusNormalizado = normalizarStatusOrdem(novoStatus, "Status e obrigatorio");
        Set<String> statusPermitidos = Set.of(STATUS_EM_PREPARACAO, STATUS_EM_EXECUCAO, STATUS_FINALIZADA);
        if (!statusPermitidos.contains(statusNormalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Status invalido. Permitidos: " + statusPermitidos);
        }

        Tecnico tecnico = tecnicoRepository.findByUsuarioEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Tecnico nao encontrado para o usuario autenticado"));

        OrdemServico ordemServico = ordemServicoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ordem de servico nao encontrada"));

        if (!tecnicoParticipaDaOrdem(ordemServico, tecnico.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Acesso negado: esta ordem nao esta atribuida a voce");
        }

        validarTransicaoStatus(ordemServico.getStatus(), statusNormalizado);
        if (STATUS_FINALIZADA.equals(statusNormalizado)) {
            validarPodeFinalizar(ordemServico);
        }

        ordemServico.setStatus(statusNormalizado);
        aplicarDatasPorStatus(ordemServico);
        OrdemServico salva = ordemServicoRepository.save(ordemServico);
        sincronizarHistorico(salva);
        atualizarDisponibilidadeTecnicos(listarTecnicosDaOrdem(salva), salva);
        return salva;
    }

    private RelacoesOrdemServico validarEntrada(OrdemServicoCreateDTO dto, OrdemServico ordemAtual) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da ordem de servico sao obrigatorios");
        }

        Integer codigoClienteNaoNulo =
                requireId(dto.getCodigoCliente(), "Codigo do cliente e obrigatorio");
        Integer codigoContratoNaoNulo =
                requireId(dto.getCodigoContrato(), "Codigo do contrato e obrigatorio");
        Integer codigoMaquinaContratoNaoNulo =
                requireId(dto.getCodigoMaquinaContrato(), "Codigo da maquina do contrato e obrigatorio");
        Integer codigoFuncionarioNaoNulo =
                requireId(dto.getCodigoFuncionario(), "Codigo do tecnico e obrigatorio");

        String status = normalizarStatusOrdem(dto.getStatus(), "Status da ordem de servico e obrigatorio");
        String criticidade = normalizarObrigatorio(dto.getCriticidade(), "Criticidade e obrigatoria");
        if (!CRITICIDADES_PERMITIDAS.contains(criticidade)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Criticidade invalida. Permitidas: " + CRITICIDADES_PERMITIDAS);
        }

        String tipoOrdem = normalizarObrigatorio(dto.getTipoOrdem(), "Tipo da ordem e obrigatorio");
        if (!TIPOS_ORDEM_PERMITIDOS.contains(tipoOrdem)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo da ordem invalido. Permitidos: " + TIPOS_ORDEM_PERMITIDOS);
        }

        Cliente cliente = clienteRepository.findById(Objects.requireNonNull(codigoClienteNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado"));

        Contrato contrato = contratoRepository.findById(Objects.requireNonNull(codigoContratoNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato nao encontrado"));

        if (contrato.getCliente() == null
                || !Objects.equals(contrato.getCliente().getId(), cliente.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O contrato informado nao pertence ao cliente informado");
        }

        MaquinaContrato maquinaContrato = maquinaContratoRepository
                .findById(Objects.requireNonNull(codigoMaquinaContratoNaoNulo))
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada"));

        if (maquinaContrato.getContrato() == null
                || !Objects.equals(maquinaContrato.getContrato().getCodigo(), contrato.getCodigo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A maquina do contrato informada nao pertence ao contrato informado");
        }

        Tecnico funcionario = tecnicoRepository.findById(codigoFuncionarioNaoNulo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionario nao encontrado"));
        List<Tecnico> tecnicos = buscarTecnicosDaEquipe(dto, funcionario);
        for (Tecnico tecnico : tecnicos) {
            validarTecnicoDisponivel(tecnico, ordemAtual, status);
        }

        MaquinaSoftwareInstalado softwareInstalado = null;
        Integer codigoSoftwareInstalado = dto.getCodigoSoftwareInstalado();
        if (codigoSoftwareInstalado != null) {
            softwareInstalado = maquinaSoftwareInstaladoRepository.findById(codigoSoftwareInstalado)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Software instalado nao encontrado"));

            if (softwareInstalado.getMaquinaContrato() == null
                    || !Objects.equals(softwareInstalado.getMaquinaContrato().getCodigo(),
                            maquinaContrato.getCodigo())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "O software instalado informado nao pertence a maquina do contrato informada");
            }
        }

        if (dto.getDataAgendamento() != null
                && dto.getDataInicioExecucao() != null
                && dto.getDataInicioExecucao().isBefore(dto.getDataAgendamento())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de inicio da execucao nao pode ser anterior a data de agendamento");
        }

        if (dto.getDataInicioExecucao() != null
                && dto.getDataFimExecucao() != null
                && dto.getDataFimExecucao().isBefore(dto.getDataInicioExecucao())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de fim da execucao nao pode ser anterior a data de inicio");
        }

        if (dto.getDataAbertura() != null
                && dto.getDataAgendamento() != null
                && dto.getDataAgendamento().isBefore(dto.getDataAbertura())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de agendamento nao pode ser anterior a data de abertura");
        }

        Integer codigoOrdemAtual = ordemAtual == null || ordemAtual.getCodigo() == null ? -1 : ordemAtual.getCodigo();
        if (dto.getDataAgendamento() != null
                && !STATUS_FINALIZADA.equals(status)
                && !STATUS_CANCELADA.equals(status)) {
            for (Tecnico tecnico : tecnicos) {
                if (ordemServicoRepository.existsByFuncionarioParticipanteIdAndDataAgendamentoAndCodigoNotAndStatusNotIn(
                            tecnico.getId(),
                            dto.getDataAgendamento(),
                            codigoOrdemAtual,
                            STATUS_ORDEM_BLOQUEADOS)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "O tecnico " + tecnico.getId() + " ja possui ordem de servico agendada para este horario");
                }
            }
        }

        return new RelacoesOrdemServico(cliente, contrato, maquinaContrato, funcionario, tecnicos, softwareInstalado);
    }

    private void mapearParaEntidade(
            OrdemServicoCreateDTO dto,
            OrdemServico entidade,
            RelacoesOrdemServico relacoes) {
        entidade.setCliente(relacoes.cliente());
        entidade.setContrato(relacoes.contrato());
        entidade.setMaquinaContrato(relacoes.maquinaContrato());
        entidade.setFuncionario(relacoes.funcionario());
        entidade.setSoftwareInstalado(relacoes.softwareInstalado());

        entidade.setStatus(normalizarStatusOrdem(dto.getStatus(), "Status da ordem de servico e obrigatorio"));
        entidade.setCriticidade(normalizarObrigatorio(dto.getCriticidade(), "Criticidade e obrigatoria"));
        entidade.setTipoOrdem(normalizarObrigatorio(dto.getTipoOrdem(), "Tipo da ordem e obrigatorio"));
        if (dto.getDataAbertura() != null) {
            entidade.setDataAbertura(dto.getDataAbertura());
        } else if (entidade.getDataAbertura() == null) {
            entidade.setDataAbertura(LocalDateTime.now());
        }
        entidade.setDataAgendamento(dto.getDataAgendamento());
        entidade.setDataInicioExecucao(dto.getDataInicioExecucao());
        entidade.setDataFimExecucao(dto.getDataFimExecucao());
        entidade.setObservacaoGeral(dto.getObservacaoGeral());
        aplicarDatasPorStatus(entidade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaChecklistManutencaoResponseDTO> listarChecklistMaquina(Integer id) {
        requireId(id, "Id da ordem de servico e obrigatorio");
        return maquinaChecklistManutencaoService.listarPorOrdemServico(id);
    }

    private void criarHistoricoManutencaoVinculado(OrdemServico os) {
        MaquinaContrato maquinaContrato = os.getMaquinaContrato();
        if (maquinaContrato == null) {
            return;
        }

        MaquinaHistoricoManutencao historico = new MaquinaHistoricoManutencao();
        historico.setOrdemServico(os);
        historico.setMaquinaContrato(maquinaContrato);
        historico.setSoftwareInstalado(os.getSoftwareInstalado());
        historico.setStatus(os.getStatus());
        historico.setCriticidade(os.getCriticidade());
        historico.setDataAgendamento(os.getDataAgendamento());
        historico.setDataInicioExecucao(os.getDataInicioExecucao());
        historico.setDataFimExecucao(os.getDataFimExecucao());
        historico.setObservacaoGeral(os.getObservacaoGeral());
        MaquinaHistoricoManutencao historicoSalvo = maquinaHistoricoManutencaoRepository.save(historico);

        os.setHistoricoManutencao(historicoSalvo);
    }

    private void sincronizarChecklistSeInformado(OrdemServicoCreateDTO dto, OrdemServico entidade) {
        if (dto.getChecklistAtivos() == null) {
            return;
        }

        List<OrdemServicoChecklistAtivo> checklistAtivos = ordemServicoChecklistAtivoService
                .substituirChecklist(entidade.getCodigo(), dto.getChecklistAtivos());
        entidade.setChecklistAtivos(checklistAtivos);
    }

    private List<Tecnico> buscarTecnicosDaEquipe(OrdemServicoCreateDTO dto, Tecnico funcionarioPrincipal) {
        LinkedHashSet<Integer> codigos = new LinkedHashSet<>();
        if (funcionarioPrincipal != null && funcionarioPrincipal.getId() != null) {
            codigos.add(funcionarioPrincipal.getId());
        }
        if (dto.getCodigosFuncionarios() != null) {
            dto.getCodigosFuncionarios().stream()
                    .filter(Objects::nonNull)
                    .forEach(codigos::add);
        }

        if (codigos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ao menos um tecnico deve ser informado");
        }

        List<Tecnico> tecnicos = new ArrayList<>();
        for (Integer codigoFuncionario : codigos) {
            Tecnico tecnico = tecnicoRepository.findById(codigoFuncionario)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Funcionario nao encontrado: " + codigoFuncionario));
            tecnicos.add(tecnico);
        }
        return tecnicos;
    }

    private void sincronizarTecnicos(OrdemServico ordemServico, List<Tecnico> tecnicos) {
        if (ordemServico == null || ordemServico.getCodigo() == null) {
            return;
        }

        ordemServicoTecnicoRepository.deleteByOrdemServicoCodigo(ordemServico.getCodigo());
        ordemServicoTecnicoRepository.flush();

        List<OrdemServicoTecnico> vinculos = tecnicos.stream()
                .filter(tecnico -> tecnico != null && tecnico.getId() != null)
                .map(tecnico -> new OrdemServicoTecnico(ordemServico, tecnico))
                .toList();
        List<OrdemServicoTecnico> vinculosSalvos = ordemServicoTecnicoRepository.saveAll(vinculos);
        ordemServico.setTecnicos(vinculosSalvos);
    }

    private void validarTecnicoDisponivel(Tecnico funcionario, OrdemServico ordemAtual, String statusOrdem) {
        if (funcionario == null || STATUS_FINALIZADA.equals(statusOrdem) || STATUS_CANCELADA.equals(statusOrdem)) {
            return;
        }

        boolean mesmoTecnico = ordemAtual != null && tecnicoParticipaDaOrdem(ordemAtual, funcionario.getId());
        String disponibilidade = StatusTecnico.codigo(funcionario.getDisponibilidade());
        if (!mesmoTecnico && !TECNICO_DISPONIVEL.equals(disponibilidade)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O tecnico selecionado nao esta disponivel");
        }
    }

    private void validarTransicaoStatus(String statusAtual, String statusNovo) {
        String atual = normalizarStatusOrdem(statusAtual, "Status atual da ordem e obrigatorio");
        if (STATUS_ORDEM_BLOQUEADOS.contains(atual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao e permitido alterar uma ordem finalizada ou cancelada");
        }
        if (atual.equals(statusNovo)) {
            return;
        }

        List<String> fluxo = List.of(STATUS_ABERTA, STATUS_AGENDADO, STATUS_EM_PREPARACAO, STATUS_EM_EXECUCAO, STATUS_FINALIZADA);
        int indiceAtual = fluxo.indexOf(atual);
        int indiceNovo = fluxo.indexOf(statusNovo);
        if (indiceAtual >= 0 && indiceNovo >= 0 && indiceNovo < indiceAtual) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao e permitido regredir o status da ordem");
        }
    }

    private void validarPodeFinalizar(OrdemServico ordemServico) {
        Integer codigo = ordemServico.getCodigo();
        List<OrdemServicoChecklistAtivo> checklistAtivos =
                ordemServicoChecklistAtivoRepository.findByOrdemServicoCodigoOrderByCodigoAsc(codigo);
        if (checklistAtivos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A ordem precisa ter checklist de ativos antes de finalizar");
        }
        if (ordemServicoChecklistAtivoRepository.existsByOrdemServicoCodigoAndLevadoFalse(codigo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos os ativos do checklist precisam ser levados antes de finalizar");
        }
        if (ordemServicoChecklistAtivoRepository.existsByOrdemServicoCodigoAndLevadoTrueAndDevolvidoFalse(codigo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos os ativos levados precisam ser devolvidos antes de finalizar");
        }
        if (maquinaChecklistManutencaoRepository.findByHistoricoManutencaoOrdemServicoCodigoOrderByCodigoAsc(codigo).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A ordem precisa ter checklist de manutencao antes de finalizar");
        }
        if (maquinaChecklistManutencaoRepository.existsByHistoricoManutencaoOrdemServicoCodigoAndRealizadoIsNull(codigo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todos os itens do checklist de manutencao precisam ser preenchidos antes de finalizar");
        }
    }

    private void aplicarDatasPorStatus(OrdemServico ordemServico) {
        String status = normalizarStatusOrdem(ordemServico.getStatus(), "Status da ordem de servico e obrigatorio");
        LocalDateTime agora = LocalDateTime.now();
        if (STATUS_EM_EXECUCAO.equals(status) && ordemServico.getDataInicioExecucao() == null) {
            ordemServico.setDataInicioExecucao(agora);
        }
        if (STATUS_FINALIZADA.equals(status)) {
            if (ordemServico.getDataInicioExecucao() == null) {
                ordemServico.setDataInicioExecucao(agora);
            }
            if (ordemServico.getDataFimExecucao() == null) {
                ordemServico.setDataFimExecucao(agora);
            }
        }
    }

    private void atualizarDisponibilidadeTecnico(Tecnico tecnico, OrdemServico ordemReferencia) {
        if (tecnico == null || tecnico.getId() == null) {
            return;
        }

        Integer codigoIgnorado = ordemReferencia == null || ordemReferencia.getCodigo() == null ? -1 : ordemReferencia.getCodigo();
        boolean temAtendimento = ordemServicoRepository.existsByFuncionarioParticipanteIdAndStatusInAndCodigoNot(
                tecnico.getId(),
                STATUS_ORDEM_TECNICO_EM_ATENDIMENTO,
                codigoIgnorado);

        if (ordemReferencia != null
                && tecnicoParticipaDaOrdem(ordemReferencia, tecnico.getId())) {
            String statusReferencia = normalizarStatusOrdem(ordemReferencia.getStatus(), "Status da ordem de servico e obrigatorio");
            if (STATUS_EM_EXECUCAO.equals(statusReferencia) || STATUS_EM_PREPARACAO.equals(statusReferencia)) {
                temAtendimento = true;
            }
        }

        if (temAtendimento) {
            tecnico.setDisponibilidade(TECNICO_EM_ATENDIMENTO);
        } else {
            tecnico.setDisponibilidade(TECNICO_DISPONIVEL);
        }
        tecnicoRepository.save(tecnico);
    }

    private void atualizarDisponibilidadeTecnicos(List<Tecnico> tecnicos, OrdemServico ordemReferencia) {
        unirTecnicos(tecnicos, List.of()).forEach(tecnico -> atualizarDisponibilidadeTecnico(tecnico, ordemReferencia));
    }

    private List<Tecnico> unirTecnicos(List<Tecnico> primeiraLista, List<Tecnico> segundaLista) {
        LinkedHashSet<Integer> codigos = new LinkedHashSet<>();
        List<Tecnico> tecnicos = new ArrayList<>();
        adicionarTecnicosUnicos(primeiraLista, codigos, tecnicos);
        adicionarTecnicosUnicos(segundaLista, codigos, tecnicos);
        return tecnicos;
    }

    private void adicionarTecnicosUnicos(List<Tecnico> origem, Set<Integer> codigos, List<Tecnico> destino) {
        if (origem == null) {
            return;
        }
        for (Tecnico tecnico : origem) {
            if (tecnico != null && tecnico.getId() != null && codigos.add(tecnico.getId())) {
                destino.add(tecnico);
            }
        }
    }

    private List<Tecnico> listarTecnicosDaOrdem(OrdemServico ordemServico) {
        if (ordemServico == null) {
            return List.of();
        }
        if (ordemServico.getCodigo() != null) {
            List<Tecnico> tecnicos = ordemServicoTecnicoRepository.findTecnicosByOrdemServicoCodigo(ordemServico.getCodigo());
            if (!tecnicos.isEmpty()) {
                return tecnicos;
            }
        }
        return ordemServico.getFuncionario() == null ? List.of() : List.of(ordemServico.getFuncionario());
    }

    private boolean tecnicoParticipaDaOrdem(OrdemServico ordemServico, Integer codigoFuncionario) {
        if (ordemServico == null || codigoFuncionario == null) {
            return false;
        }
        if (ordemServico.getFuncionario() != null
                && Objects.equals(ordemServico.getFuncionario().getId(), codigoFuncionario)) {
            return true;
        }
        if (ordemServico.getCodigo() == null) {
            return false;
        }
        return ordemServicoTecnicoRepository.existsByOrdemServicoCodigoAndTecnicoId(
                ordemServico.getCodigo(),
                codigoFuncionario);
    }

    private void sincronizarHistorico(OrdemServico os) {
        MaquinaHistoricoManutencao historico = os.getHistoricoManutencao();
        if (historico == null && os.getCodigo() != null) {
            historico = maquinaHistoricoManutencaoRepository.findByOrdemServicoCodigo(os.getCodigo())
                    .orElse(null);
        }
        if (historico == null) {
            return;
        }
        historico.setStatus(os.getStatus());
        historico.setCriticidade(os.getCriticidade());
        historico.setDataAgendamento(os.getDataAgendamento());
        historico.setDataInicioExecucao(os.getDataInicioExecucao());
        historico.setDataFimExecucao(os.getDataFimExecucao());
        historico.setObservacaoGeral(os.getObservacaoGeral());
        maquinaHistoricoManutencaoRepository.save(historico);
    }

    private String normalizarStatusOrdem(String valor, String mensagemObrigatorio) {
        String status = normalizarObrigatorio(valor, mensagemObrigatorio);
        if ("AGENDADA".equals(status)) {
            status = STATUS_AGENDADO;
        } else if ("CONCLUIDA".equals(status) || "FINALIZADO".equals(status)) {
            status = STATUS_FINALIZADA;
        } else if ("CANCELADO".equals(status)) {
            status = STATUS_CANCELADA;
        }
        if (!STATUS_ORDEM_PERMITIDOS.contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status da ordem invalido. Permitidos: " + STATUS_ORDEM_PERMITIDOS);
        }
        return status;
    }

    private String normalizarObrigatorio(String valor, String mensagem) {
        String normalizado = normalizarOpcional(valor);
        if (normalizado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return normalizado;
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String limpo = valor.trim();
        if (limpo.isEmpty()) {
            return null;
        }
        return limpo.toUpperCase(Locale.ROOT);
    }

    private Integer requireId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return id;
    }

    private record RelacoesOrdemServico(
            Cliente cliente,
            Contrato contrato,
            MaquinaContrato maquinaContrato,
            Tecnico funcionario,
            List<Tecnico> tecnicos,
            MaquinaSoftwareInstalado softwareInstalado) {
    }
}
