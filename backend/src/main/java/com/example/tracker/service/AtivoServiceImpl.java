package com.example.tracker.service;

import com.example.tracker.dto.ativo.AtivoCreateDTO;
import com.example.tracker.dto.ativo.AtivoResponseDTO;
import com.example.tracker.entity.Ativo;
import com.example.tracker.entity.CatalogoAtivo;
import com.example.tracker.entity.MaquinaContrato;
import com.example.tracker.entity.Tecnico;
import com.example.tracker.repository.AtivoRepository;
import com.example.tracker.repository.CatalogoAtivoRepository;
import com.example.tracker.repository.MaquinaContratoRepository;
import com.example.tracker.repository.TecnicoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AtivoServiceImpl implements AtivoService {

    private final AtivoRepository ativoRepository;
    private final CatalogoAtivoRepository catalogoAtivoRepository;
    private final TecnicoRepository tecnicoRepository;
    private final MaquinaContratoRepository maquinaContratoRepository;

    @Override
    @Transactional
    public AtivoResponseDTO criar(AtivoCreateDTO dto) {
        Ativo ativo = new Ativo();
        mapearParaEntidade(dto, ativo);
        return AtivoResponseDTO.fromEntity(ativoRepository.save(ativo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtivoResponseDTO> listar() {
        return ativoRepository.findAll().stream()
                .map(AtivoResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AtivoResponseDTO buscarPorId(Integer id) {
        return AtivoResponseDTO.fromEntity(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtivoResponseDTO> buscarPorCatalogoAtivo(Integer codigoCatalogoAtivo) {
        return ativoRepository.findByCatalogoAtivoCodigo(codigoCatalogoAtivo).stream()
                .map(AtivoResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtivoResponseDTO> buscarPorFuncionario(Integer codigoFuncionario) {
        return ativoRepository.findByFuncionarioResponsavelId(codigoFuncionario).stream()
                .map(AtivoResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtivoResponseDTO> buscarPorMaquinaContrato(Integer codigoMaquinaContrato) {
        if (codigoMaquinaContrato == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codigo da maquina do contrato e obrigatorio");
        }

        if (!maquinaContratoRepository.existsById(codigoMaquinaContrato)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada");
        }

        return ativoRepository.findByMaquinaContratoCodigo(codigoMaquinaContrato).stream()
                .map(AtivoResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtivoResponseDTO> buscarPorStatus(String status) {
        return ativoRepository.findByStatus(status).stream()
                .map(AtivoResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public AtivoResponseDTO atualizar(Integer id, AtivoCreateDTO dto) {
        Ativo ativo = buscarEntidade(id);
        mapearParaEntidade(dto, ativo);
        return AtivoResponseDTO.fromEntity(ativoRepository.save(ativo));
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        if (!ativoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ativo não encontrado");
        }
        ativoRepository.deleteById(id);
    }

    private Ativo buscarEntidade(Integer id) {
        return ativoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ativo não encontrado"));
    }

    private void mapearParaEntidade(AtivoCreateDTO dto, Ativo ativo) {
        CatalogoAtivo catalogoAtivo = catalogoAtivoRepository.findById(dto.getCodigoCatalogoAtivo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catálogo de ativo não encontrado"));

        ativo.setCatalogoAtivo(catalogoAtivo);
        ativo.setNumeroSerie(dto.getNumeroSerie());
        ativo.setLote(dto.getLote());
        ativo.setDescricao(dto.getDescricao());
        ativo.setStatus(dto.getStatus());

        if (dto.getCodigoFuncionarioResponsavel() != null) {
            Tecnico tecnico = tecnicoRepository.findById(dto.getCodigoFuncionarioResponsavel())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Técnico não encontrado"));
            ativo.setFuncionarioResponsavel(tecnico);
        } else {
            ativo.setFuncionarioResponsavel(null);
        }

        if (dto.getCodigoMaquinaContrato() != null) {
            MaquinaContrato maquinaContrato = maquinaContratoRepository.findById(dto.getCodigoMaquinaContrato())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Maquina do contrato nao encontrada"));
            ativo.setMaquinaContrato(maquinaContrato);
        } else {
            ativo.setMaquinaContrato(null);
        }
    }
}
