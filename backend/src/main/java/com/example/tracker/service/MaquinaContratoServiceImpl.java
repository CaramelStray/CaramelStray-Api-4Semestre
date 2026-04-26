package com.example.tracker.service;

import com.example.tracker.dto.maquinacontrato.MaquinaContratoCreateDTO;
import com.example.tracker.dto.maquinacontrato.MaquinaContratoResponseDTO;
import com.example.tracker.entity.CatalogoMaquina;
import com.example.tracker.entity.Contrato;
import com.example.tracker.entity.MaquinaContrato;
import com.example.tracker.repository.CatalogoMaquinaRepository;
import com.example.tracker.repository.ContratoRepository;
import com.example.tracker.repository.MaquinaContratoRepository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MaquinaContratoServiceImpl implements MaquinaContratoService {

    private final MaquinaContratoRepository maquinaContratoRepository;
    private final ContratoRepository contratoRepository;
    private final CatalogoMaquinaRepository catalogoMaquinaRepository;

    public MaquinaContratoServiceImpl(
            MaquinaContratoRepository maquinaContratoRepository,
            ContratoRepository contratoRepository,
            CatalogoMaquinaRepository catalogoMaquinaRepository) {
        this.maquinaContratoRepository = maquinaContratoRepository;
        this.contratoRepository = contratoRepository;
        this.catalogoMaquinaRepository = catalogoMaquinaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaContratoResponseDTO> listarTodos() {
        return maquinaContratoRepository.findAllWithSoftwares()
                .stream()
                .map(MaquinaContratoResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MaquinaContratoResponseDTO buscarPorId(Integer id) {
        Integer idNaoNulo = requireId(id, "Id da maquina do contrato e obrigatorio");

        return maquinaContratoRepository.findByIdWithSoftwares(idNaoNulo)
                .map(MaquinaContratoResponseDTO::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaContratoResponseDTO> buscarPorContrato(Integer codigoContrato) {

        Integer codigoContratoNaoNulo =
                requireId(codigoContrato, "Codigo do contrato e obrigatorio");

        List<MaquinaContrato> maquinas =
                maquinaContratoRepository.findByContratoCodigoWithSoftwares(codigoContratoNaoNulo);

        if (maquinas.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Nenhuma maquina encontrada para o contrato");
        }

        return maquinas.stream()
                .map(MaquinaContratoResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaContratoResponseDTO> buscarPorCatalogoMaquina(Integer codigoCatalogoMaquina) {

        Integer codigoCatalogoNaoNulo =
                requireId(codigoCatalogoMaquina, "Codigo do catalogo de maquina e obrigatorio");

        List<MaquinaContrato> maquinas =
                maquinaContratoRepository.findByCatalogoMaquinaCodigo(codigoCatalogoNaoNulo);

        if (maquinas.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Nenhuma maquina encontrada para o catalogo informado");
        }

        return maquinas.stream()
                .map(MaquinaContratoResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public MaquinaContratoResponseDTO cadastrar(MaquinaContratoCreateDTO maquinaContratoDTO) {

        RelacoesMaquinaContrato relacoes = validarEntrada(maquinaContratoDTO);

        MaquinaContrato maquinaContrato = new MaquinaContrato();

        mapearParaEntidade(
                maquinaContratoDTO,
                maquinaContrato,
                relacoes.contrato,
                relacoes.catalogoMaquina);

        return MaquinaContratoResponseDTO.fromEntity(
                maquinaContratoRepository.save(maquinaContrato));
    }

    @Override
    @Transactional
    public MaquinaContratoResponseDTO atualizar(Integer id, MaquinaContratoCreateDTO maquinaContratoDTO) {

        Integer idNaoNulo = requireId(id, "Id da maquina do contrato e obrigatorio");

        RelacoesMaquinaContrato relacoes = validarEntrada(maquinaContratoDTO);

        MaquinaContrato maquinaContrato = maquinaContratoRepository.findById(idNaoNulo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada"));

        mapearParaEntidade(
                maquinaContratoDTO,
                maquinaContrato,
                relacoes.contrato,
                relacoes.catalogoMaquina);

        return MaquinaContratoResponseDTO.fromEntity(
                maquinaContratoRepository.save(maquinaContrato));
    }

    @Override
    @Transactional
    public void deletar(Integer id) {

        Integer idNaoNulo = requireId(id, "Id da maquina do contrato e obrigatorio");

        if (!maquinaContratoRepository.existsById(idNaoNulo)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada");
        }

        maquinaContratoRepository.deleteById(idNaoNulo);
    }

    private RelacoesMaquinaContrato validarEntrada(MaquinaContratoCreateDTO dto) {

        if (dto == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dados da maquina do contrato sao obrigatorios");
        }

        Integer codigoContratoNaoNulo =
                requireId(dto.getCodigoContrato(), "Codigo do contrato e obrigatorio");

        Integer codigoCatalogoNaoNulo =
                requireId(dto.getCodigoCatalogoMaquina(),
                        "Codigo do catalogo de maquina e obrigatorio");

        Contrato contrato = contratoRepository.findById(codigoContratoNaoNulo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Contrato nao encontrado"));

        CatalogoMaquina catalogoMaquina =
                catalogoMaquinaRepository.findById(codigoCatalogoNaoNulo)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Catalogo de maquina nao encontrado"));

        validarLatitudeLongitude(dto.getLatitude(), dto.getLongitude());

        return new RelacoesMaquinaContrato(contrato, catalogoMaquina);
    }

    private void validarLatitudeLongitude(BigDecimal latitude, BigDecimal longitude) {

        if (latitude != null && (latitude.compareTo(BigDecimal.valueOf(-90)) < 0
                || latitude.compareTo(BigDecimal.valueOf(90)) > 0)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Latitude deve estar entre -90 e 90");
        }

        if (longitude != null && (longitude.compareTo(BigDecimal.valueOf(-180)) < 0
                || longitude.compareTo(BigDecimal.valueOf(180)) > 0)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Longitude deve estar entre -180 e 180");
        }
    }

    private void mapearParaEntidade(
            MaquinaContratoCreateDTO dto,
            MaquinaContrato entidade,
            Contrato contrato,
            CatalogoMaquina catalogoMaquina) {

        entidade.setContrato(contrato);
        entidade.setCatalogoMaquina(catalogoMaquina);
        entidade.setNumeroSerie(dto.getNumeroSerie());
        entidade.setDataInstalacao(dto.getDataInstalacao());
        entidade.setManutencaoFeita(dto.getManutencaoFeita());
        entidade.setTrabalhoEmAltura(
                dto.getTrabalhoEmAltura() != null
                        ? dto.getTrabalhoEmAltura()
                        : Boolean.FALSE);
        entidade.setLatitude(dto.getLatitude());
        entidade.setLongitude(dto.getLongitude());
    }

    private record RelacoesMaquinaContrato(
            Contrato contrato,
            CatalogoMaquina catalogoMaquina) {}

    private Integer requireId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return id;
    }
}