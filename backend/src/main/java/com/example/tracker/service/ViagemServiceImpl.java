package com.example.tracker.service;

import com.example.tracker.dto.viagem.ViagemCreateDTO;
import com.example.tracker.dto.viagem.ViagemParadaCreateDTO;
import com.example.tracker.entity.Viagem;
import com.example.tracker.entity.ViagemParada;
import com.example.tracker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + dto.getCodigoCliente())));

        viagem.setTipoViagem(tipoViagemRepository.findById(dto.getCodigoTipoViagem())
                .orElseThrow(
                        () -> new RuntimeException("Tipo de Viagem não encontrado: " + dto.getCodigoTipoViagem())));

        if (dto.getCodigoFuncionarioResponsavel() != null) {
            viagem.setFuncionarioResponsavel(tecnicoRepository.findById(dto.getCodigoFuncionarioResponsavel())
                    .orElseThrow(() -> new RuntimeException(
                            "Técnico não encontrado: " + dto.getCodigoFuncionarioResponsavel())));
        }

        if (dto.getCodigoOrdemServico() != null) {
            viagem.setOrdemServico(ordemServicoRepository.findById(dto.getCodigoOrdemServico()).orElse(null));
        }

        if (dto.getParadas() != null && !dto.getParadas().isEmpty()) {
            List<ViagemParada> paradasEntities = dto.getParadas().stream()
                    .map(paradaDto -> mapParadaDtoToEntity(paradaDto, viagem))
                    .collect(Collectors.toList());

            viagem.setParadas(paradasEntities);
        }

        return repository.save(viagem);
    }

    @Override
    @Transactional
    public Viagem atualizar(Integer id, ViagemCreateDTO dto) {
        Viagem viagem = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada para atualização: " + id));

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
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado")));

        viagem.setTipoViagem(tipoViagemRepository.findById(dto.getCodigoTipoViagem())
                .orElseThrow(() -> new RuntimeException("Tipo de Viagem não encontrado")));

        if (dto.getCodigoFuncionarioResponsavel() != null) {
            viagem.setFuncionarioResponsavel(tecnicoRepository.findById(dto.getCodigoFuncionarioResponsavel())
                    .orElseThrow(() -> new RuntimeException("Técnico não encontrado")));
        } else {
            viagem.setFuncionarioResponsavel(null);
        }

        viagem.getParadas().clear();
        if (dto.getParadas() != null && !dto.getParadas().isEmpty()) {
            List<ViagemParada> novasParadas = dto.getParadas().stream()
                    .map(paradaDto -> mapParadaDtoToEntity(paradaDto, viagem))
                    .collect(Collectors.toList());
            viagem.getParadas().addAll(novasParadas);
        }

        return repository.save(viagem);
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
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada id: " + id));
    }

    @Override
    public List<Viagem> listarTodas() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void excluir(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<Viagem> buscarPorCliente(Integer idCliente) {
        return repository.findByClienteId(idCliente);
    }

    @Override
    public List<Viagem> buscarPorStatus(String status) {
        return repository.findByStatus(status);
    }
}