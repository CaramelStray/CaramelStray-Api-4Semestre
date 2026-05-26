package com.example.tracker.service;

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
import com.example.tracker.entity.Tecnico;
import com.example.tracker.repository.ClienteRepository;
import com.example.tracker.repository.ContratoRepository;
import com.example.tracker.repository.MaquinaContratoRepository;
import com.example.tracker.repository.MaquinaHistoricoManutencaoRepository;
import com.example.tracker.repository.MaquinaSoftwareInstaladoRepository;
import com.example.tracker.repository.OrdemServicoRepository;
import com.example.tracker.repository.TecnicoRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrdemServicoServiceImpl implements OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository;
    private final ContratoRepository contratoRepository;
    private final MaquinaContratoRepository maquinaContratoRepository;
    private final OrdemServicoChecklistAtivoService ordemServicoChecklistAtivoService;
    private final MaquinaHistoricoManutencaoRepository maquinaHistoricoManutencaoRepository;
    private final MaquinaChecklistManutencaoService maquinaChecklistManutencaoService;

    public OrdemServicoServiceImpl(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            TecnicoRepository tecnicoRepository,
            MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository,
            ContratoRepository contratoRepository,
            MaquinaContratoRepository maquinaContratoRepository,
            OrdemServicoChecklistAtivoService ordemServicoChecklistAtivoService,
            MaquinaHistoricoManutencaoRepository maquinaHistoricoManutencaoRepository,
            MaquinaChecklistManutencaoService maquinaChecklistManutencaoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.maquinaSoftwareInstaladoRepository = maquinaSoftwareInstaladoRepository;
        this.contratoRepository = contratoRepository;
        this.maquinaContratoRepository = maquinaContratoRepository;
        this.ordemServicoChecklistAtivoService = ordemServicoChecklistAtivoService;
        this.maquinaHistoricoManutencaoRepository = maquinaHistoricoManutencaoRepository;
        this.maquinaChecklistManutencaoService = maquinaChecklistManutencaoService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> listarTodos() {
        return ordemServicoRepository.findAll();
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
                .map(tecnico -> ordemServicoRepository.findByFuncionarioId(tecnico.getId())
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Tecnico nao encontrado para o usuario autenticado"));

        OrdemServico ordemServico = ordemServicoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));

        if (ordemServico.getFuncionario() == null
                || !Objects.equals(ordemServico.getFuncionario().getId(), tecnico.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado: esta ordem nao esta atribuida a voce");
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));
    }


    @Override
    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscarCompletoPorId(Integer id) {
        Integer idNaoNulo = requireId(id, "Id da ordem de servico e obrigatorio");

        OrdemServico os = ordemServicoRepository.findByIdCompleto(idNaoNulo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));

        return OrdemServicoResponseDTO.fromEntity(os);
    }


    @Override
    @Transactional(readOnly = true)
    public OrdemServicoDadosBasicosResponseDTO buscarDadosBasicosPorId(Integer id) {
        return OrdemServicoDadosBasicosResponseDTO.fromEntity(buscarPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorCliente(Integer codigoCliente) {
        Integer codigoClienteNaoNulo =
                requireId(codigoCliente, "Codigo do cliente e obrigatorio");

        List<OrdemServico> itens =
                ordemServicoRepository.findByClienteId(Objects.requireNonNull(codigoClienteNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma ordem de servico encontrada para o cliente");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorFuncionario(Integer codigoFuncionario) {
        Integer codigoFuncionarioNaoNulo =
                requireId(codigoFuncionario, "Codigo do funcionario e obrigatorio");

        List<OrdemServico> itens =
                ordemServicoRepository.findByFuncionarioId(Objects.requireNonNull(codigoFuncionarioNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma ordem de servico encontrada para o funcionario");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorSoftwareInstalado(Integer codigoSoftwareInstalado) {
        Integer codigoSoftwareInstaladoNaoNulo =
                requireId(codigoSoftwareInstalado, "Codigo do software instalado e obrigatorio");

        List<OrdemServico> itens =
                ordemServicoRepository.findBySoftwareInstaladoCodigo(Objects.requireNonNull(codigoSoftwareInstaladoNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma ordem de servico encontrada para o software instalado");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorContrato(Integer codigoContrato) {
        Integer codigoContratoNaoNulo =
                requireId(codigoContrato, "Codigo do contrato e obrigatorio");

        List<OrdemServico> itens =
                ordemServicoRepository.findByContratoCodigo(Objects.requireNonNull(codigoContratoNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma ordem de servico encontrada para o contrato");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorMaquinaContrato(Integer codigoMaquinaContrato) {
        Integer codigoMaquinaContratoNaoNulo =
                requireId(codigoMaquinaContrato, "Codigo da maquina do contrato e obrigatorio");

        List<OrdemServico> itens =
                ordemServicoRepository.findByMaquinaContratoCodigo(Objects.requireNonNull(codigoMaquinaContratoNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma ordem de servico encontrada para a maquina do contrato");
        }

        return itens;
    }

    @Override
    @Transactional
    public OrdemServico cadastrar(OrdemServicoCreateDTO dto) {
        RelacoesOrdemServico relacoes = validarEntrada(dto);

        OrdemServico entidade = new OrdemServico();
        mapearParaEntidade(dto, entidade, relacoes);

        if (entidade.getDataAbertura() == null) {
            entidade.setDataAbertura(LocalDateTime.now());
        }

        OrdemServico ordemServicoSalva = ordemServicoRepository.save(entidade);
        sincronizarChecklistSeInformado(dto, ordemServicoSalva);
        criarHistoricoManutencaoVinculado(ordemServicoSalva);
        return ordemServicoSalva;
    }

    @Override
    @Transactional
    public OrdemServico atualizar(Integer id, OrdemServicoCreateDTO dto) {
        Integer idNaoNulo = requireId(id, "Id da ordem de servico e obrigatorio");
        RelacoesOrdemServico relacoes = validarEntrada(dto);

        OrdemServico entidade = ordemServicoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));

        mapearParaEntidade(dto, entidade, relacoes);
        OrdemServico ordemServicoSalva = ordemServicoRepository.save(Objects.requireNonNull(entidade));
        sincronizarChecklistSeInformado(dto, ordemServicoSalva);
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

        if (novoStatus == null || novoStatus.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status e obrigatorio");
        }

        String statusNormalizado = novoStatus.trim().toUpperCase();
        java.util.Set<String> statusPermitidos = java.util.Set.of("EM_PREPARACAO", "EM_EXECUCAO", "FINALIZADA");
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

        if (ordemServico.getFuncionario() == null
                || !Objects.equals(ordemServico.getFuncionario().getId(), tecnico.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acesso negado: esta ordem nao esta atribuida a voce");
        }

        ordemServico.setStatus(statusNormalizado);
        return ordemServicoRepository.save(ordemServico);
    }

    private RelacoesOrdemServico validarEntrada(OrdemServicoCreateDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da ordem de servico sao obrigatorios");
        }

        Integer codigoClienteNaoNulo =
                requireId(dto.getCodigoCliente(), "Codigo do cliente e obrigatorio");
        Integer codigoContratoNaoNulo =
                requireId(dto.getCodigoContrato(), "Codigo do contrato e obrigatorio");
        Integer codigoMaquinaContratoNaoNulo =
                requireId(dto.getCodigoMaquinaContrato(), "Codigo da maquina do contrato e obrigatorio");

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

        MaquinaContrato maquinaContrato = maquinaContratoRepository.findById(Objects.requireNonNull(codigoMaquinaContratoNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada"));

        if (maquinaContrato.getContrato() == null
                || !Objects.equals(maquinaContrato.getContrato().getCodigo(), contrato.getCodigo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A maquina do contrato informada nao pertence ao contrato informado");
        }

        Tecnico funcionario = null;
        Integer codigoFuncionario = dto.getCodigoFuncionario();
        if (codigoFuncionario != null) {
            funcionario = tecnicoRepository.findById(codigoFuncionario)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionario nao encontrado"));
        }

        MaquinaSoftwareInstalado softwareInstalado = null;
        Integer codigoSoftwareInstalado = dto.getCodigoSoftwareInstalado();
        if (codigoSoftwareInstalado != null) {
            softwareInstalado = maquinaSoftwareInstaladoRepository.findById(codigoSoftwareInstalado)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Software instalado nao encontrado"));

            if (softwareInstalado.getMaquinaContrato() == null
                    || !Objects.equals(softwareInstalado.getMaquinaContrato().getCodigo(), maquinaContrato.getCodigo())) {
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

        return new RelacoesOrdemServico(cliente, contrato, maquinaContrato, funcionario, softwareInstalado);
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

        entidade.setStatus(dto.getStatus());
        entidade.setCriticidade(dto.getCriticidade());
        entidade.setTipoOrdem(dto.getTipoOrdem());
        entidade.setDataAbertura(dto.getDataAbertura());
        entidade.setDataAgendamento(dto.getDataAgendamento());
        entidade.setDataInicioExecucao(dto.getDataInicioExecucao());
        entidade.setDataFimExecucao(dto.getDataFimExecucao());
        entidade.setObservacaoGeral(dto.getObservacaoGeral());
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
        MaquinaHistoricoManutencao historicoSalvo = maquinaHistoricoManutencaoRepository.save(historico);

        os.setHistoricoManutencao(historicoSalvo);
    }

    private void sincronizarChecklistSeInformado(OrdemServicoCreateDTO dto, OrdemServico entidade) {
        if (dto.getChecklistAtivos() == null) {
            return;
        }

        List<OrdemServicoChecklistAtivo> checklistAtivos =
                ordemServicoChecklistAtivoService.substituirChecklist(entidade.getCodigo(), dto.getChecklistAtivos());
        entidade.setChecklistAtivos(checklistAtivos);
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
            MaquinaSoftwareInstalado softwareInstalado) {
    }
}
