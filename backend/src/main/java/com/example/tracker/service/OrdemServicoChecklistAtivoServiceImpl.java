package com.example.tracker.service;

import com.example.tracker.dto.ordemservico.OrdemServicoChecklistAtivoCheckinDTO;
import com.example.tracker.dto.ordemservico.OrdemServicoChecklistAtivoCreateDTO;
import com.example.tracker.entity.Ativo;
import com.example.tracker.entity.CatalogoAtivo;
import com.example.tracker.entity.OrdemServico;
import com.example.tracker.entity.OrdemServicoChecklistAtivo;
import com.example.tracker.entity.Tecnico;
import com.example.tracker.repository.AtivoRepository;
import com.example.tracker.repository.OrdemServicoChecklistAtivoRepository;
import com.example.tracker.repository.OrdemServicoRepository;
import com.example.tracker.repository.TecnicoRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrdemServicoChecklistAtivoServiceImpl implements OrdemServicoChecklistAtivoService {

    private static final Set<String> STATUS_INTERVENCAO_PERMITIDOS =
            Set.of("PENDENTE", "UTILIZADO", "INSTALADO", "NAO_UTILIZADO", "RETORNADO", "DANIFICADO", "PERDIDO");
    private static final Set<String> TIPOS_ATIVO_INTERVENCAO_PERMITIDOS =
            Set.of("COMPONENTE", "PERIFERICO");
    private static final Set<String> STATUS_ORDEM_BLOQUEADOS =
            Set.of("FINALIZADA", "FINALIZADO", "CANCELADA", "CANCELADO");
    private static final int TAMANHO_MAXIMO_OBSERVACAO_INTERVENCAO = 2000;

    private final OrdemServicoChecklistAtivoRepository checklistRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final AtivoRepository ativoRepository;
    private final TecnicoRepository tecnicoRepository;

    public OrdemServicoChecklistAtivoServiceImpl(
            OrdemServicoChecklistAtivoRepository checklistRepository,
            OrdemServicoRepository ordemServicoRepository,
            AtivoRepository ativoRepository,
            TecnicoRepository tecnicoRepository) {
        this.checklistRepository = checklistRepository;
        this.ordemServicoRepository = ordemServicoRepository;
        this.ativoRepository = ativoRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServicoChecklistAtivo> listarPorOrdemServico(Integer codigoOrdemServico) {
        Integer codigoOrdemServicoNaoNulo =
                requireId(codigoOrdemServico, "Codigo da ordem de servico e obrigatorio");
        buscarOrdemServico(codigoOrdemServicoNaoNulo);
        return checklistRepository.findByOrdemServicoCodigoOrderByCodigoAsc(codigoOrdemServicoNaoNulo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServicoChecklistAtivo> listarItensIntervencaoTecnico(
            Integer codigoOrdemServico,
            String emailUsuario) {
        Integer codigoOrdemServicoNaoNulo =
                requireId(codigoOrdemServico, "Codigo da ordem de servico e obrigatorio");

        OrdemServico ordemServico = buscarOrdemServico(codigoOrdemServicoNaoNulo);
        validarTecnicoPodeAlterarOrdem(ordemServico, emailUsuario);

        return checklistRepository.findByOrdemServicoCodigoOrderByCodigoAsc(codigoOrdemServicoNaoNulo).stream()
                .filter(this::isItemElegivelParaIntervencao)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemServicoChecklistAtivo buscarItem(Integer codigoOrdemServico, Integer codigoItem) {
        Integer codigoOrdemServicoNaoNulo =
                requireId(codigoOrdemServico, "Codigo da ordem de servico e obrigatorio");
        Integer codigoItemNaoNulo = requireId(codigoItem, "Codigo do item do checklist e obrigatorio");

        buscarOrdemServico(codigoOrdemServicoNaoNulo);
        return checklistRepository
                .findByCodigoAndOrdemServicoCodigo(codigoItemNaoNulo, codigoOrdemServicoNaoNulo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item do checklist de ativos nao encontrado"));
    }

    @Override
    @Transactional
    public OrdemServicoChecklistAtivo adicionar(Integer codigoOrdemServico, OrdemServicoChecklistAtivoCreateDTO dto) {
        Integer codigoOrdemServicoNaoNulo =
                requireId(codigoOrdemServico, "Codigo da ordem de servico e obrigatorio");
        OrdemServico ordemServico = buscarOrdemServico(codigoOrdemServicoNaoNulo);
        ItemValidado itemValidado = validarItem(ordemServico, dto, null);

        validarDuplicidade(codigoOrdemServicoNaoNulo, itemValidado.ativo().getCodigo(), null);

        OrdemServicoChecklistAtivo item = new OrdemServicoChecklistAtivo();
        aplicarCampos(item, ordemServico, itemValidado, dto);
        return checklistRepository.save(item);
    }

    @Override
    @Transactional
    public List<OrdemServicoChecklistAtivo> substituirChecklist(
            Integer codigoOrdemServico,
            List<OrdemServicoChecklistAtivoCreateDTO> itens) {
        Integer codigoOrdemServicoNaoNulo =
                requireId(codigoOrdemServico, "Codigo da ordem de servico e obrigatorio");
        OrdemServico ordemServico = buscarOrdemServico(codigoOrdemServicoNaoNulo);

        if (itens == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Checklist de ativos e obrigatorio");
        }

        Set<Integer> ativosInformados = new HashSet<>();
        for (OrdemServicoChecklistAtivoCreateDTO item : itens) {
            Integer codigoAtivo = requireId(
                    item == null ? null : item.getCodigoAtivo(),
                    "Codigo do ativo e obrigatorio");
            if (!ativosInformados.add(codigoAtivo)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Nao e permitido informar ativos duplicados no checklist");
            }
        }

        checklistRepository.deleteByOrdemServicoCodigo(codigoOrdemServicoNaoNulo);
        checklistRepository.flush();

        List<OrdemServicoChecklistAtivo> novosItens = itens.stream()
                .map(dto -> {
                    ItemValidado itemValidado = validarItem(ordemServico, dto, null);
                    OrdemServicoChecklistAtivo item = new OrdemServicoChecklistAtivo();
                    aplicarCampos(item, ordemServico, itemValidado, dto);
                    return item;
                })
                .toList();

        return checklistRepository.saveAll(novosItens);
    }

    @Override
    @Transactional
    public OrdemServicoChecklistAtivo atualizar(
            Integer codigoOrdemServico,
            Integer codigoItem,
            OrdemServicoChecklistAtivoCreateDTO dto) {
        Integer codigoOrdemServicoNaoNulo =
                requireId(codigoOrdemServico, "Codigo da ordem de servico e obrigatorio");
        Integer codigoItemNaoNulo = requireId(codigoItem, "Codigo do item do checklist e obrigatorio");
        OrdemServico ordemServico = buscarOrdemServico(codigoOrdemServicoNaoNulo);

        OrdemServicoChecklistAtivo item = checklistRepository
                .findByCodigoAndOrdemServicoCodigo(codigoItemNaoNulo, codigoOrdemServicoNaoNulo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item do checklist de ativos nao encontrado"));

        ItemValidado itemValidado = validarItem(ordemServico, dto, codigoItemNaoNulo);
        validarDuplicidade(codigoOrdemServicoNaoNulo, itemValidado.ativo().getCodigo(), codigoItemNaoNulo);

        aplicarCampos(item, ordemServico, itemValidado, dto);
        return checklistRepository.save(item);
    }

    @Override
    @Transactional
    public OrdemServicoChecklistAtivo registrarCheckinTecnico(
            Integer codigoOrdemServico,
            Integer codigoItem,
            OrdemServicoChecklistAtivoCheckinDTO dto,
            String emailUsuario) {
        Integer codigoOrdemServicoNaoNulo =
                requireId(codigoOrdemServico, "Codigo da ordem de servico e obrigatorio");
        Integer codigoItemNaoNulo = requireId(codigoItem, "Codigo do item do checklist e obrigatorio");

        if (dto == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dados do check-in do item do checklist sao obrigatorios");
        }

        OrdemServico ordemServico = buscarOrdemServico(codigoOrdemServicoNaoNulo);
        validarTecnicoPodeAlterarOrdem(ordemServico, emailUsuario);
        validarOrdemEditavel(ordemServico);

        OrdemServicoChecklistAtivo item = checklistRepository
                .findByCodigoAndOrdemServicoCodigo(codigoItemNaoNulo, codigoOrdemServicoNaoNulo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item do checklist de ativos nao encontrado"));

        validarItemElegivelParaIntervencao(item);

        String statusIntervencao = normalizarStatusIntervencao(dto.getStatusIntervencao());
        item.setStatusIntervencao(statusIntervencao);
        item.setDataIntervencao(dto.getDataIntervencao() != null ? dto.getDataIntervencao() : LocalDateTime.now());
        item.setObservacaoIntervencao(limparObservacaoIntervencao(dto.getObservacaoIntervencao()));

        return checklistRepository.save(item);
    }

    @Override
    @Transactional
    public OrdemServicoChecklistAtivo marcarLevado(Integer codigoOrdemServico, Integer codigoItem, String emailUsuario) {
        Integer codigoOs = requireId(codigoOrdemServico, "Codigo da ordem de servico e obrigatorio");
        Integer codigoIt = requireId(codigoItem, "Codigo do item e obrigatorio");
        OrdemServico ordemServico = buscarOrdemServico(codigoOs);
        validarTecnicoPodeAlterarOrdem(ordemServico, emailUsuario);
        validarOrdemEditavel(ordemServico);
        OrdemServicoChecklistAtivo item = checklistRepository
                .findByCodigoAndOrdemServicoCodigo(codigoIt, codigoOs)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item do checklist nao encontrado"));
        item.setLevado(true);
        return checklistRepository.save(item);
    }

    @Override
    @Transactional
    public OrdemServicoChecklistAtivo desmarcarLevado(Integer codigoOrdemServico, Integer codigoItem, String emailUsuario) {
        Integer codigoOs = requireId(codigoOrdemServico, "Codigo da ordem de servico e obrigatorio");
        Integer codigoIt = requireId(codigoItem, "Codigo do item e obrigatorio");
        OrdemServico ordemServico = buscarOrdemServico(codigoOs);
        validarTecnicoPodeAlterarOrdem(ordemServico, emailUsuario);
        validarOrdemEditavel(ordemServico);
        OrdemServicoChecklistAtivo item = checklistRepository
                .findByCodigoAndOrdemServicoCodigo(codigoIt, codigoOs)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item do checklist nao encontrado"));
        item.setLevado(false);
        item.setDevolvido(false); // se desmarcar levado, devolvido tambem deve ser desfeito
        return checklistRepository.save(item);
    }

    @Override
    @Transactional
    public OrdemServicoChecklistAtivo marcarDevolvido(Integer codigoOrdemServico, Integer codigoItem, String emailUsuario) {
        Integer codigoOs = requireId(codigoOrdemServico, "Codigo da ordem de servico e obrigatorio");
        Integer codigoIt = requireId(codigoItem, "Codigo do item e obrigatorio");
        OrdemServico ordemServico = buscarOrdemServico(codigoOs);
        validarTecnicoPodeAlterarOrdem(ordemServico, emailUsuario);
        validarOrdemEditavel(ordemServico);
        OrdemServicoChecklistAtivo item = checklistRepository
                .findByCodigoAndOrdemServicoCodigo(codigoIt, codigoOs)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item do checklist nao encontrado"));
        if (!Boolean.TRUE.equals(item.getLevado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ativo precisa ser levado antes de ser devolvido");
        }
        item.setDevolvido(true);
        return checklistRepository.save(item);
    }

    @Override
    @Transactional
    public OrdemServicoChecklistAtivo desmarcarDevolvido(Integer codigoOrdemServico, Integer codigoItem, String emailUsuario) {
        Integer codigoOs = requireId(codigoOrdemServico, "Codigo da ordem de servico e obrigatorio");
        Integer codigoIt = requireId(codigoItem, "Codigo do item e obrigatorio");
        OrdemServico ordemServico = buscarOrdemServico(codigoOs);
        validarTecnicoPodeAlterarOrdem(ordemServico, emailUsuario);
        validarOrdemEditavel(ordemServico);
        OrdemServicoChecklistAtivo item = checklistRepository
                .findByCodigoAndOrdemServicoCodigo(codigoIt, codigoOs)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item do checklist nao encontrado"));
        item.setDevolvido(false);
        return checklistRepository.save(item);
    }

    @Override
    @Transactional
    public void remover(Integer codigoOrdemServico, Integer codigoItem) {
        OrdemServicoChecklistAtivo item = buscarItem(codigoOrdemServico, codigoItem);
        checklistRepository.delete(item);
    }

    private OrdemServico buscarOrdemServico(Integer codigoOrdemServico) {
        return ordemServicoRepository.findById(Objects.requireNonNull(codigoOrdemServico))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));
    }

    private ItemValidado validarItem(
            OrdemServico ordemServico,
            OrdemServicoChecklistAtivoCreateDTO dto,
            Integer codigoItemAtual) {
        if (dto == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dados do item do checklist de ativos sao obrigatorios");
        }

        Integer codigoAtivoNaoNulo = requireId(dto.getCodigoAtivo(), "Codigo do ativo e obrigatorio");
        Ativo ativo = ativoRepository.findById(Objects.requireNonNull(codigoAtivoNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ativo nao encontrado"));

        validarAtivoPertenceAMaquinaDaOrdem(ordemServico, ativo);

        Tecnico funcionario = buscarFuncionarioDoItem(ordemServico, dto.getCodigoFuncionario());

        boolean levado = Boolean.TRUE.equals(dto.getLevado());
        boolean devolvido = Boolean.TRUE.equals(dto.getDevolvido());
        if (devolvido && !levado) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ativo nao pode ser marcado como devolvido antes de ser levado");
        }

        String descricaoAtivo = limparString(dto.getDescricaoAtivo());
        if (descricaoAtivo == null) {
            descricaoAtivo = montarDescricaoAtivo(ativo);
        }

        if (descricaoAtivo != null && descricaoAtivo.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A descricao do ativo deve possuir no maximo 255 caracteres");
        }

        return new ItemValidado(ativo, funcionario, descricaoAtivo);
    }

    private void validarAtivoPertenceAMaquinaDaOrdem(OrdemServico ordemServico, Ativo ativo) {
        if (ativo.getMaquinaContrato() == null) {
            return;
        }

        if (ordemServico.getMaquinaContrato() == null
                || !Objects.equals(
                        ativo.getMaquinaContrato().getCodigo(),
                        ordemServico.getMaquinaContrato().getCodigo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ativo informado nao pertence a maquina da ordem de servico");
        }
    }

    private Tecnico buscarFuncionarioDoItem(OrdemServico ordemServico, Integer codigoFuncionario) {
        Tecnico funcionarioOrdem = ordemServico.getFuncionario();
        if (codigoFuncionario == null) {
            return funcionarioOrdem;
        }

        Tecnico funcionario = tecnicoRepository.findById(codigoFuncionario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionario nao encontrado"));

        if (funcionarioOrdem != null && !Objects.equals(funcionarioOrdem.getId(), funcionario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O funcionario informado nao pertence a ordem de servico");
        }

        return funcionario;
    }

    private void validarDuplicidade(Integer codigoOrdemServico, Integer codigoAtivo, Integer codigoItemAtual) {
        checklistRepository
                .findByOrdemServicoCodigoAndAtivoCodigo(codigoOrdemServico, codigoAtivo)
                .filter(itemExistente -> !Objects.equals(itemExistente.getCodigo(), codigoItemAtual))
                .ifPresent(itemExistente -> {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Ativo ja informado no checklist desta ordem de servico");
                });
    }

    private void aplicarCampos(
            OrdemServicoChecklistAtivo item,
            OrdemServico ordemServico,
            ItemValidado itemValidado,
            OrdemServicoChecklistAtivoCreateDTO dto) {
        item.setOrdemServico(ordemServico);
        item.setAtivo(itemValidado.ativo());
        item.setFuncionario(itemValidado.funcionario());
        item.setDescricaoAtivo(itemValidado.descricaoAtivo());
        item.setLevado(Boolean.TRUE.equals(dto.getLevado()));
        item.setDevolvido(Boolean.TRUE.equals(dto.getDevolvido()));
        item.setObservacao(limparString(dto.getObservacao()));
        item.setStatusIntervencao(normalizarStatusIntervencaoOpcional(dto.getStatusIntervencao()));
        item.setDataIntervencao(dto.getDataIntervencao());
        item.setObservacaoIntervencao(limparObservacaoIntervencao(dto.getObservacaoIntervencao()));
    }

    private String montarDescricaoAtivo(Ativo ativo) {
        String descricao = limparString(ativo.getDescricao());
        if (descricao != null) {
            return descricao;
        }

        CatalogoAtivo catalogo = ativo.getCatalogoAtivo();
        if (catalogo != null) {
            descricao = limparString(catalogo.getDescricaoProduto());
            if (descricao != null) {
                return descricao;
            }
        }

        return "Ativo " + ativo.getCodigo();
    }

    private String limparString(String valor) {
        if (valor == null) {
            return null;
        }
        String valorLimpo = valor.trim();
        if (valorLimpo.isEmpty()) {
            return null;
        }
        return valorLimpo;
    }

    private void validarTecnicoPodeAlterarOrdem(OrdemServico ordemServico, String emailUsuario) {
        if (emailUsuario == null || emailUsuario.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario nao autenticado");
        }

        Tecnico tecnico = tecnicoRepository.findByUsuarioEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Tecnico nao encontrado para o usuario autenticado"));

        if (ordemServico.getFuncionario() == null
                || !Objects.equals(ordemServico.getFuncionario().getId(), tecnico.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acesso negado: esta ordem nao esta atribuida a voce");
        }
    }

    private void validarOrdemEditavel(OrdemServico ordemServico) {
        String status = normalizar(ordemServico.getStatus());
        if (status != null && STATUS_ORDEM_BLOQUEADOS.contains(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nao e permitido registrar check-in em ordem finalizada ou cancelada");
        }
    }

    private void validarItemElegivelParaIntervencao(OrdemServicoChecklistAtivo item) {
        if (!isItemElegivelParaIntervencao(item)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Check-in de intervencao permitido apenas para componentes ou perifericos");
        }
    }

    private boolean isItemElegivelParaIntervencao(OrdemServicoChecklistAtivo item) {
        if (item == null || item.getAtivo() == null || item.getAtivo().getCatalogoAtivo() == null) {
            return false;
        }

        String tipoAtivo = normalizar(item.getAtivo().getCatalogoAtivo().getTipo());
        return tipoAtivo != null && TIPOS_ATIVO_INTERVENCAO_PERMITIDOS.contains(tipoAtivo);
    }

    private String normalizarStatusIntervencao(String statusIntervencao) {
        String statusNormalizado = normalizar(statusIntervencao);
        if (statusNormalizado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status da intervencao e obrigatorio");
        }

        if (!STATUS_INTERVENCAO_PERMITIDOS.contains(statusNormalizado)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status da intervencao invalido");
        }

        return statusNormalizado;
    }

    private String normalizarStatusIntervencaoOpcional(String statusIntervencao) {
        String statusNormalizado = normalizar(statusIntervencao);
        if (statusNormalizado == null) {
            return null;
        }

        if (!STATUS_INTERVENCAO_PERMITIDOS.contains(statusNormalizado)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status da intervencao invalido");
        }

        return statusNormalizado;
    }

    private String limparObservacaoIntervencao(String observacaoIntervencao) {
        String observacaoTratada = limparString(observacaoIntervencao);
        if (observacaoTratada != null && observacaoTratada.length() > TAMANHO_MAXIMO_OBSERVACAO_INTERVENCAO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A observacao da intervencao deve possuir no maximo "
                            + TAMANHO_MAXIMO_OBSERVACAO_INTERVENCAO
                            + " caracteres");
        }
        return observacaoTratada;
    }

    private String normalizar(String valor) {
        String valorLimpo = limparString(valor);
        return valorLimpo == null ? null : valorLimpo.toUpperCase(Locale.ROOT);
    }

    private Integer requireId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return id;
    }

    private record ItemValidado(
            Ativo ativo,
            Tecnico funcionario,
            String descricaoAtivo) {
    }
}
