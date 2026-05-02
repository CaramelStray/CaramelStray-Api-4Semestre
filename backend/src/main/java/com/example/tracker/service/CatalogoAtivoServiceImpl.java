package com.example.tracker.service;

import com.example.tracker.dto.catalogoativo.CatalogoAtivoCreateDTO;
import com.example.tracker.dto.catalogoativo.CatalogoAtivoResponseDTO;
import com.example.tracker.entity.CatalogoAtivo;
import com.example.tracker.repository.CatalogoAtivoRepository;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CatalogoAtivoServiceImpl implements CatalogoAtivoService {

    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of("FERRAMENTA", "EQUIPAMENTO", "COMPONENTE", "PERIFERICO", "EPI", "CONSUMIVEL");

    private final CatalogoAtivoRepository catalogoAtivoRepository;

    @Override
    @Transactional
    public CatalogoAtivoResponseDTO criar(CatalogoAtivoCreateDTO dto) {
        CatalogoAtivo entity = new CatalogoAtivo();
        mapearParaEntidade(dto, entity);
        return CatalogoAtivoResponseDTO.fromEntity(catalogoAtivoRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogoAtivoResponseDTO> listar() {
        return catalogoAtivoRepository.findAll().stream()
                .map(CatalogoAtivoResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogoAtivoResponseDTO buscarPorId(Integer id) {
        return CatalogoAtivoResponseDTO.fromEntity(buscarEntidade(id));
    }

    @Override
    @Transactional
    public CatalogoAtivoResponseDTO atualizar(Integer id, CatalogoAtivoCreateDTO dto) {
        CatalogoAtivo entity = buscarEntidade(id);
        mapearParaEntidade(dto, entity);
        return CatalogoAtivoResponseDTO.fromEntity(catalogoAtivoRepository.save(entity));
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        if (!catalogoAtivoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catálogo de ativo não encontrado");
        }
        catalogoAtivoRepository.deleteById(id);
    }

    private CatalogoAtivo buscarEntidade(Integer id) {
        return catalogoAtivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Catálogo de ativo não encontrado"));
    }

    private void mapearParaEntidade(CatalogoAtivoCreateDTO dto, CatalogoAtivo entity) {
        entity.setDescricaoProduto(limparString(dto.getDescricaoProduto()));
        entity.setModelo(limparString(dto.getModelo()));
        entity.setMarca(limparString(dto.getMarca()));
        entity.setDescricao(limparString(dto.getDescricao()));
        entity.setEspecificacao(limparString(dto.getEspecificacao()));
        entity.setTipo(normalizarTipo(dto.getTipo()));
    }

    private String normalizarTipo(String tipo) {
        String tipoNormalizado = limparString(tipo);
        if (tipoNormalizado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tipo do ativo é obrigatório");
        }

        tipoNormalizado = tipoNormalizado.toUpperCase(Locale.ROOT);
        if (!TIPOS_PERMITIDOS.contains(tipoNormalizado)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo do ativo inválido. Tipos permitidos: FERRAMENTA, EQUIPAMENTO, COMPONENTE, PERIFERICO, EPI, CONSUMIVEL");
        }

        return tipoNormalizado;
    }

    private String limparString(String valor) {
        if (valor == null) {
            return null;
        }

        String valorLimpo = valor.trim();
        return valorLimpo.isEmpty() ? null : valorLimpo;
    }
}
