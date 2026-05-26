package com.example.tracker.service;

import com.example.tracker.dto.maquinasoftwareinstalado.MaquinaSoftwareInstaladoCreateDTO;
import com.example.tracker.dto.maquinasoftwareinstalado.SistemaLocalizacaoStatusResponseDTO;
import com.example.tracker.entity.CatalogoSoftware;
import com.example.tracker.entity.MaquinaContrato;
import com.example.tracker.entity.MaquinaSoftwareInstalado;
import com.example.tracker.entity.OrdemServico;
import com.example.tracker.repository.CatalogoSoftwareRepository;
import com.example.tracker.repository.MaquinaContratoRepository;
import com.example.tracker.repository.MaquinaSoftwareInstaladoRepository;
import com.example.tracker.repository.OrdemServicoRepository;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MaquinaSoftwareInstaladoServiceImpl implements MaquinaSoftwareInstaladoService {

    private static final Set<String> STATUS_EM_EXECUCAO = Set.of("EM_EXECUCAO");
    private static final Set<String> TIPOS_MANUTENCAO = Set.of("MANUTENCAO_PREVENTIVA", "MANUTENCAO_CORRETIVA");

    private final MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository;
    private final MaquinaContratoRepository maquinaContratoRepository;
    private final CatalogoSoftwareRepository catalogoSoftwareRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    public MaquinaSoftwareInstaladoServiceImpl(
            MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository,
            MaquinaContratoRepository maquinaContratoRepository,
            CatalogoSoftwareRepository catalogoSoftwareRepository,
            OrdemServicoRepository ordemServicoRepository) {
        this.maquinaSoftwareInstaladoRepository = maquinaSoftwareInstaladoRepository;
        this.maquinaContratoRepository = maquinaContratoRepository;
        this.catalogoSoftwareRepository = catalogoSoftwareRepository;
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaSoftwareInstalado> listarTodos() {
        return maquinaSoftwareInstaladoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public MaquinaSoftwareInstalado buscarPorId(Integer id) {
        Integer idNaoNulo = requireId(id, "Id do software instalado e obrigatorio");
        return maquinaSoftwareInstaladoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Software instalado nao encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaSoftwareInstalado> buscarPorMaquinaContrato(Integer codigoMaquinaContrato) {
        Integer codigoMaquinaContratoNaoNulo =
                requireId(codigoMaquinaContrato, "Codigo da maquina do contrato e obrigatorio");
        List<MaquinaSoftwareInstalado> itens = maquinaSoftwareInstaladoRepository.findByMaquinaContratoCodigo(
                Objects.requireNonNull(codigoMaquinaContratoNaoNulo));
        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum software instalado encontrado para a maquina");
        }
        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaSoftwareInstalado> buscarPorSoftware(Integer codigoSoftware) {
        Integer codigoSoftwareNaoNulo = requireId(codigoSoftware, "Codigo do software e obrigatorio");
        List<MaquinaSoftwareInstalado> itens =
                maquinaSoftwareInstaladoRepository.findBySoftwareId(Objects.requireNonNull(codigoSoftwareNaoNulo));
        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma instalacao encontrada para o software");
        }
        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SistemaLocalizacaoStatusResponseDTO> listarSistemasComLocalizacaoStatus() {
        List<MaquinaSoftwareInstalado> sistemas = maquinaSoftwareInstaladoRepository.findAllComLocalizacao();

        List<Integer> codigosSistemas = sistemas.stream()
                .map(MaquinaSoftwareInstalado::getCodigo)
                .toList();

        Map<Integer, List<OrdemServico>> ordensPorSistema = codigosSistemas.isEmpty()
                ? Map.of()
                : ordemServicoRepository.findOrdensAbertasPorSoftwaresInstalados(codigosSistemas).stream()
                        .filter(this::isOrdemManutencao)
                        .filter(os -> os.getSoftwareInstalado() != null)
                        .collect(Collectors.groupingBy(os -> os.getSoftwareInstalado().getCodigo()));

        LocalDateTime agora = LocalDateTime.now();

        return sistemas.stream()
                .map(sistema -> toSistemaLocalizacaoStatusResponseDTO(
                        sistema,
                        ordensPorSistema.getOrDefault(sistema.getCodigo(), List.of()),
                        agora))
                .toList();
    }

    @Override
    @Transactional
    public MaquinaSoftwareInstalado cadastrar(MaquinaSoftwareInstaladoCreateDTO dto) {
        validarEntrada(dto);
        MaquinaSoftwareInstalado entidade = new MaquinaSoftwareInstalado();
        mapearParaEntidade(dto, entidade);
        return maquinaSoftwareInstaladoRepository.save(entidade);
    }

    @Override
    @Transactional
    public MaquinaSoftwareInstalado atualizar(Integer id, MaquinaSoftwareInstaladoCreateDTO dto) {
        Integer idNaoNulo = requireId(id, "Id do software instalado e obrigatorio");
        validarEntrada(dto);

        MaquinaSoftwareInstalado entidade =
                maquinaSoftwareInstaladoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Software instalado nao encontrado"));

        mapearParaEntidade(dto, entidade);
        return maquinaSoftwareInstaladoRepository.save(Objects.requireNonNull(entidade));
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        Integer idNaoNulo = requireId(id, "Id do software instalado e obrigatorio");
        if (!maquinaSoftwareInstaladoRepository.existsById(Objects.requireNonNull(idNaoNulo))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Software instalado nao encontrado");
        }
        maquinaSoftwareInstaladoRepository.deleteById(Objects.requireNonNull(idNaoNulo));
    }

    private void validarEntrada(MaquinaSoftwareInstaladoCreateDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do software instalado sao obrigatorios");
        }

        Integer codigoMaquinaContratoNaoNulo =
                requireId(dto.getCodigoMaquinaContrato(), "Codigo da maquina do contrato e obrigatorio");
        Integer codigoSoftwareNaoNulo = requireId(dto.getCodigoSoftware(), "Codigo do software e obrigatorio");

        Objects.requireNonNull(codigoMaquinaContratoNaoNulo);
        Objects.requireNonNull(codigoSoftwareNaoNulo);
    }

    private void mapearParaEntidade(MaquinaSoftwareInstaladoCreateDTO dto, MaquinaSoftwareInstalado entidade) {
        Integer codigoMaquinaContratoNaoNulo =
                requireId(dto.getCodigoMaquinaContrato(), "Codigo da maquina do contrato e obrigatorio");
        Integer codigoSoftwareNaoNulo = requireId(dto.getCodigoSoftware(), "Codigo do software e obrigatorio");

        MaquinaContrato maquinaContrato = maquinaContratoRepository.findById(Objects.requireNonNull(codigoMaquinaContratoNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada"));
        CatalogoSoftware software = catalogoSoftwareRepository.findById(Objects.requireNonNull(codigoSoftwareNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Software de catalogo nao encontrado"));

        entidade.setMaquinaContrato(maquinaContrato);
        entidade.setSoftware(software);
        entidade.setChaveLicenca(dto.getChaveLicenca());
        entidade.setVersaoInstalada(dto.getVersaoInstalada());
    }

    private SistemaLocalizacaoStatusResponseDTO toSistemaLocalizacaoStatusResponseDTO(
            MaquinaSoftwareInstalado sistema,
            List<OrdemServico> ordensAbertas,
            LocalDateTime agora) {
        SistemaLocalizacaoStatusResponseDTO dto = new SistemaLocalizacaoStatusResponseDTO();
        CatalogoSoftware software = sistema.getSoftware();
        MaquinaContrato maquinaContrato = sistema.getMaquinaContrato();

        dto.setCodigoSistemaInstalado(sistema.getCodigo());

        if (software != null) {
            dto.setCodigoSoftware(software.getId());
            dto.setNome(software.getNome());
            dto.setTipo(software.getTipo());
        }

        if (maquinaContrato != null) {
            dto.setCodigoMaquinaContrato(maquinaContrato.getCodigo());
            dto.setNumeroSerieMaquina(maquinaContrato.getNumeroSerie());
            dto.setLatitude(maquinaContrato.getLatitude());
            dto.setLongitude(maquinaContrato.getLongitude());
        }

        boolean manutencaoEmExecucao = ordensAbertas.stream()
                .anyMatch(ordem -> isOrdemEmExecucaoAgora(ordem, agora));
        boolean manutencaoAtrasada = ordensAbertas.stream()
                .anyMatch(ordem -> ordem.getDataAgendamento() != null && ordem.getDataAgendamento().isBefore(agora));
        boolean manutencaoPendente = ordensAbertas.stream()
                .anyMatch(ordem -> ordem.getDataAgendamento() != null && !ordem.getDataAgendamento().isBefore(agora));

        dto.setStatusOperacional(definirStatusOperacional(software, manutencaoEmExecucao));
        dto.setManutencaoPendente(manutencaoPendente);
        dto.setManutencaoAtrasada(manutencaoAtrasada);

        return dto;
    }

    private String definirStatusOperacional(CatalogoSoftware software, boolean manutencaoEmExecucao) {
        if (software != null && Boolean.FALSE.equals(software.getAtivo())) {
            return "INATIVO";
        }
        if (manutencaoEmExecucao) {
            return "EM_MANUTENCAO";
        }
        return "ATIVO";
    }

    private boolean isOrdemManutencao(OrdemServico ordemServico) {
        String tipo = normalizar(ordemServico.getTipoOrdem());
        return tipo == null || TIPOS_MANUTENCAO.contains(tipo);
    }

    private boolean isOrdemEmExecucaoAgora(OrdemServico ordemServico, LocalDateTime agora) {
        String status = normalizar(ordemServico.getStatus());
        if (STATUS_EM_EXECUCAO.contains(status)) {
            return true;
        }

        LocalDateTime inicio = ordemServico.getDataInicioExecucao();
        if (inicio == null || inicio.isAfter(agora)) {
            return false;
        }

        LocalDateTime fim = ordemServico.getDataFimExecucao();
        return fim == null || !fim.isBefore(agora);
    }

    private String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim().toUpperCase();
    }

    private Integer requireId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return id;
    }
}
