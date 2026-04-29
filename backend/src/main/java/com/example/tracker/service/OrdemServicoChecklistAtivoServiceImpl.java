package com.example.tracker.service;

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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrdemServicoChecklistAtivoServiceImpl implements OrdemServicoChecklistAtivoService {

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
