package com.example.tracker.service;

import com.example.tracker.dto.software.CatalogoSoftwareCreateDTO;
import com.example.tracker.dto.software.SoftwareChecklistItemCreateDTO;
import com.example.tracker.entity.CatalogoSoftware;
import com.example.tracker.entity.CatalogoSoftwareChecklistPadrao;
import com.example.tracker.entity.CatalogoSoftwareChecklistPadraoId;
import com.example.tracker.entity.CatalogoTarefa;
import com.example.tracker.repository.CatalogoSoftwareRepository;
import com.example.tracker.repository.CatalogoTarefaRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogoSoftwareService {

    private static final String CATEGORIA_TAREFA_SOFTWARE = "SOFTWARE";

    private final CatalogoSoftwareRepository catalogoSoftwareRepository;
    private final CatalogoTarefaRepository catalogoTarefaRepository;

    @Transactional
    public CatalogoSoftware criar(CatalogoSoftwareCreateDTO dto) {
        validarSoftwareParaCriacao(dto);

        CatalogoSoftware novoSoftware = new CatalogoSoftware();
        aplicarCampos(novoSoftware, dto);

        return catalogoSoftwareRepository.save(novoSoftware);
    }

    @Transactional
    public Optional<CatalogoSoftware> atualizar(Integer id, CatalogoSoftwareCreateDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("O id do software e obrigatorio.");
        }

        CatalogoSoftware software = catalogoSoftwareRepository.findById(id).orElse(null);
        if (software == null) {
            return Optional.empty();
        }

        validarSoftwareParaAtualizacao(id, dto);
        aplicarCampos(software, dto);

        return Optional.of(catalogoSoftwareRepository.save(software));
    }

    @Transactional
    public boolean remover(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("O id do software e obrigatorio.");
        }

        if (!catalogoSoftwareRepository.existsById(id)) {
            return false;
        }

        catalogoSoftwareRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<CatalogoSoftware> listarSoftwares(
            String termo,
            String tipo,
            Boolean ativo,
            Integer page,
            Integer size) {
        Specification<CatalogoSoftware> spec = Specification.where(comFiltrosOpcionais(termo, tipo, ativo));

        if (page != null || size != null) {
            int pagina = page == null ? 0 : page;
            int limite = size == null ? 10 : size;

            if (pagina < 0) {
                throw new IllegalArgumentException("O parametro page deve ser maior ou igual a zero.");
            }
            if (limite <= 0) {
                throw new IllegalArgumentException("O parametro size deve ser maior que zero.");
            }

            Pageable pageable = PageRequest.of(pagina, limite, Sort.by(Sort.Direction.ASC, "id"));
            return catalogoSoftwareRepository.findAll(spec, pageable).getContent();
        }

        return catalogoSoftwareRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "id"));
    }

    @Transactional(readOnly = true)
    public Optional<CatalogoSoftware> buscarPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("O id do software e obrigatorio.");
        }
        return catalogoSoftwareRepository.findById(id);
    }

    private void validarSoftwareParaCriacao(CatalogoSoftwareCreateDTO dto) {
        validarDtoObrigatorio(dto);
        validarDuplicidade(null, dto);
        validarChecklist(dto.getChecklistPadrao());
    }

    private void validarSoftwareParaAtualizacao(Integer id, CatalogoSoftwareCreateDTO dto) {
        validarDtoObrigatorio(dto);
        validarDuplicidade(id, dto);
        validarChecklist(dto.getChecklistPadrao());
    }

    private void validarDtoObrigatorio(CatalogoSoftwareCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Os dados do software sao obrigatorios.");
        }

        if (limparString(dto.getNomeSoftware()) == null) {
            throw new IllegalArgumentException("O nome do software e obrigatorio.");
        }

        if (limparString(dto.getVersao()) == null) {
            throw new IllegalArgumentException("A versao do software e obrigatoria.");
        }

        if (limparString(dto.getTipo()) == null) {
            throw new IllegalArgumentException("O tipo do software e obrigatorio.");
        }

        if (limparString(dto.getDesenvolvedorFornecedor()) == null) {
            throw new IllegalArgumentException("O desenvolvedor ou fornecedor e obrigatorio.");
        }
    }

    private void validarDuplicidade(Integer id, CatalogoSoftwareCreateDTO dto) {
        String nomeNormalizado = limparString(dto.getNomeSoftware());
        String versaoNormalizada = limparString(dto.getVersao());

        Optional<CatalogoSoftware> softwareExistente =
                catalogoSoftwareRepository.findByNomeIgnoreCaseAndVersaoIgnoreCase(
                        nomeNormalizado, versaoNormalizada);

        if (softwareExistente.isPresent()
                && (id == null || !softwareExistente.get().getId().equals(id))) {
            throw new IllegalArgumentException("Ja existe software cadastrado com este nome e versao.");
        }
    }

    private void validarChecklist(List<SoftwareChecklistItemCreateDTO> checklistPadrao) {
        if (checklistPadrao == null || checklistPadrao.isEmpty()) {
            return;
        }

        Set<String> tarefasInformadas = new HashSet<>();

        for (SoftwareChecklistItemCreateDTO item : checklistPadrao) {
            if (item == null || limparString(item.getDescricao()) == null) {
                throw new IllegalArgumentException("Todas as tarefas do checklist devem possuir descricao.");
            }

            String chave = limparString(item.getDescricao()).toLowerCase();
            if (!tarefasInformadas.add(chave)) {
                throw new IllegalArgumentException(
                        "Nao e permitido informar tarefas duplicadas no checklist padrao.");
            }
        }
    }

    private void aplicarCampos(CatalogoSoftware software, CatalogoSoftwareCreateDTO dto) {
        software.setNome(limparString(dto.getNomeSoftware()));
        software.setVersao(limparString(dto.getVersao()));
        software.setTipo(limparString(dto.getTipo()));
        software.setDesenvolvedorFornecedor(limparString(dto.getDesenvolvedorFornecedor()));
        software.setUrlDocumentacao(limparString(dto.getUrlDocumentacao()));
        software.setDescricaoTecnica(limparString(dto.getDescricaoTecnica()));

        if (dto.getAtivo() != null) {
            software.setAtivo(dto.getAtivo());
        } else if (software.getId() == null) {
            software.setAtivo(true);
        }

        software.limparChecklistPadrao();

        if (dto.getChecklistPadrao() == null || dto.getChecklistPadrao().isEmpty()) {
            return;
        }

        for (SoftwareChecklistItemCreateDTO itemDto : dto.getChecklistPadrao()) {
            CatalogoTarefa tarefa = buscarOuCriarTarefa(limparString(itemDto.getDescricao()));

            CatalogoSoftwareChecklistPadrao item = new CatalogoSoftwareChecklistPadrao();
            item.setId(new CatalogoSoftwareChecklistPadraoId());
            item.setTarefa(tarefa);
            item.setObrigatorio(Boolean.TRUE.equals(itemDto.getObrigatorio()));

            software.adicionarChecklistPadraoItem(item);
        }
    }

    private CatalogoTarefa buscarOuCriarTarefa(String descricao) {
        return catalogoTarefaRepository
                .findByDescricaoIgnoreCaseAndCategoriaIgnoreCase(descricao, CATEGORIA_TAREFA_SOFTWARE)
                .orElseGet(() -> {
                    CatalogoTarefa novaTarefa = new CatalogoTarefa();
                    novaTarefa.setDescricao(descricao);
                    novaTarefa.setCategoria(CATEGORIA_TAREFA_SOFTWARE);
                    return catalogoTarefaRepository.save(novaTarefa);
                });
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

    private Specification<CatalogoSoftware> comFiltrosOpcionais(String termo, String tipo, Boolean ativo) {
        String termoLimpo = limparString(termo);
        String tipoLimpo = limparString(tipo);

        Specification<CatalogoSoftware> spec = Specification.where(null);

        if (termoLimpo != null) {
            String termoNormalizado = "%" + termoLimpo.toLowerCase() + "%";
            spec = spec.and((root, query, builder) -> builder.or(
                    builder.like(builder.lower(root.get("nome")), termoNormalizado),
                    builder.like(builder.lower(root.get("versao")), termoNormalizado),
                    builder.like(builder.lower(root.get("desenvolvedorFornecedor")), termoNormalizado)));
        }

        if (tipoLimpo != null) {
            spec = spec.and((root, query, builder) ->
                    builder.equal(builder.lower(root.get("tipo")), tipoLimpo.toLowerCase()));
        }

        if (ativo != null) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get("ativo"), ativo));
        }

        return spec;
    }
}
