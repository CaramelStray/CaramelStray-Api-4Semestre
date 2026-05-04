package com.example.tracker.service;

import com.example.tracker.dto.viagem.ViagemCreateDTO;
import com.example.tracker.dto.viagem.ViagemParadaCreateDTO;
import com.example.tracker.entity.OrdemServico;
import com.example.tracker.entity.Viagem;
import com.example.tracker.entity.ViagemParada;
import com.example.tracker.repository.ClienteRepository;
import com.example.tracker.repository.OrdemServicoRepository;
import com.example.tracker.repository.TecnicoRepository;
import com.example.tracker.repository.TipoViagemRepository;
import com.example.tracker.repository.ViagemRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ViagemServiceImpl implements ViagemService {

    @Autowired
    private ViagemRepository repository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TipoViagemRepository tipoViagemRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Override
    @Transactional
    public Viagem salvar(ViagemCreateDTO dto) {
        Viagem viagem = new Viagem();
        aplicarCampos(viagem, dto);
        return repository.save(viagem);
    }

    @Override
    @Transactional
    public Viagem atualizar(Integer id, ViagemCreateDTO dto) {
        Viagem viagem = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Viagem nao encontrada"));

        aplicarCampos(viagem, dto);
        viagem.getParadas().clear();
        if (dto.getParadas() != null && !dto.getParadas().isEmpty()) {
            List<ViagemParada> novasParadas = dto.getParadas().stream()
                    .map(paradaDto -> mapParadaDtoToEntity(paradaDto, viagem))
                    .collect(Collectors.toList());
            viagem.getParadas().addAll(novasParadas);
        }

        return repository.save(viagem);
    }

    private void aplicarCampos(Viagem viagem, ViagemCreateDTO dto) {
        viagem.setStatus(dto.getStatus() != null ? dto.getStatus() : "ABERTA");
        viagem.setDataSaidaPrevista(dto.getDataSaidaPrevista());
        viagem.setDataSaidaReal(dto.getDataSaidaReal());
        viagem.setDataRetornoPrevisto(dto.getDataRetornoPrevisto());
        viagem.setDataRetornoReal(dto.getDataRetornoReal());
        viagem.setOrigem(dto.getOrigem());
        viagem.setDestino(dto.getDestino());
        viagem.setKmPrevisto(dto.getKmPrevisto());
        viagem.setKmReal(dto.getKmReal());
        viagem.setObservacao(dto.getObservacao());

        viagem.setCliente(clienteRepository.findById(dto.getCodigoCliente())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado")));

        viagem.setTipoViagem(tipoViagemRepository.findById(dto.getCodigoTipoViagem())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de viagem nao encontrado")));

        if (dto.getCodigoFuncionarioResponsavel() != null) {
            viagem.setFuncionarioResponsavel(tecnicoRepository.findById(dto.getCodigoFuncionarioResponsavel())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Funcionario responsavel nao encontrado")));
        } else {
            viagem.setFuncionarioResponsavel(null);
        }

        viagem.setOrdemServico(buscarOrdemServicoOpcional(dto.getCodigoOrdemServico()));

        if (viagem.getCodigo() == null && dto.getParadas() != null && !dto.getParadas().isEmpty()) {
            List<ViagemParada> paradasEntities = dto.getParadas().stream()
                    .map(paradaDto -> mapParadaDtoToEntity(paradaDto, viagem))
                    .collect(Collectors.toList());
            viagem.setParadas(paradasEntities);
        }
    }

    private OrdemServico buscarOrdemServicoOpcional(Integer codigoOrdemServico) {
        if (codigoOrdemServico == null) {
            return null;
        }

        return ordemServicoRepository.findById(codigoOrdemServico)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));
    }

    private ViagemParada mapParadaDtoToEntity(ViagemParadaCreateDTO dto, Viagem viagem) {
        ViagemParada entity = new ViagemParada();

        entity.setOrdem(dto.getOrdem());
        entity.setDescricaoLocal(dto.getDescricaoLocal());
        entity.setEndereco(dto.getEndereco());
        entity.setCidade(dto.getCidade());
        entity.setEstadoRegiao(dto.getEstadoRegiao());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setDataChegadaPrevista(dto.getDataChegadaPrevista());
        entity.setDataChegadaReal(dto.getDataChegadaReal());
        entity.setDataSaidaPrevista(dto.getDataSaidaPrevista());
        entity.setDataSaidaReal(dto.getDataSaidaReal());
        entity.setObservacao(dto.getObservacao());
        entity.setViagem(viagem);

        return entity;
    }

    @Override
    public Viagem buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Viagem nao encontrada"));
    }

    @Override
    public List<Viagem> listarTodas() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void excluir(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Viagem nao encontrada");
        }
        repository.deleteById(id);
    }

    @Override
    public List<Viagem> buscarPorCliente(Integer idCliente) {
        return repository.findByClienteId(idCliente);
    }

    @Override
    public List<Viagem> buscarPorOrdemServico(Integer codigoOrdemServico) {
        if (codigoOrdemServico == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codigo da ordem de servico e obrigatorio");
        }
        ordemServicoRepository.findById(Objects.requireNonNull(codigoOrdemServico))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));
        return repository.findByOrdemServicoCodigo(codigoOrdemServico);
    }

    @Override
    public List<Viagem> buscarPorStatus(String status) {
        return repository.findByStatus(status);
    }
}