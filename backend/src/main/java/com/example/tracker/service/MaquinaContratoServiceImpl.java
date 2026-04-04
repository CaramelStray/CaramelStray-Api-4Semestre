package com.example.tracker.service;

import com.example.tracker.dto.maquinacontrato.MaquinaContratoCreateDTO;
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
    public List<MaquinaContrato> listarTodos() {
        return maquinaContratoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public MaquinaContrato buscarPorId(Integer id) {
        validarId(id, "Id da maquina do contrato e obrigatorio");
        return maquinaContratoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaContrato> buscarPorContrato(Integer codigoContrato) {
        validarId(codigoContrato, "Codigo do contrato e obrigatorio");
        List<MaquinaContrato> maquinas = maquinaContratoRepository.findByCodigoContrato(codigoContrato);
        if (maquinas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma maquina encontrada para o contrato");
        }
        return maquinas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaContrato> buscarPorCatalogoMaquina(Integer codigoCatalogoMaquina) {
        validarId(codigoCatalogoMaquina, "Codigo do catalogo de maquina e obrigatorio");
        List<MaquinaContrato> maquinas = maquinaContratoRepository.findByCodigoCatalogoMaquina(codigoCatalogoMaquina);
        if (maquinas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma maquina encontrada para o catalogo informado");
        }
        return maquinas;
    }

    @Override
    @Transactional
    public MaquinaContrato cadastrar(MaquinaContratoCreateDTO maquinaContratoDTO) {
        validarEntrada(maquinaContratoDTO);
        MaquinaContrato maquinaContrato = new MaquinaContrato();
        mapearParaEntidade(maquinaContratoDTO, maquinaContrato);
        return maquinaContratoRepository.save(maquinaContrato);
    }

    @Override
    @Transactional
    public MaquinaContrato atualizar(Integer id, MaquinaContratoCreateDTO maquinaContratoDTO) {
        validarId(id, "Id da maquina do contrato e obrigatorio");
        validarEntrada(maquinaContratoDTO);
        MaquinaContrato maquinaContrato = maquinaContratoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada"));

        mapearParaEntidade(maquinaContratoDTO, maquinaContrato);
        return maquinaContratoRepository.save(maquinaContrato);
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        validarId(id, "Id da maquina do contrato e obrigatorio");
        if (!maquinaContratoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada");
        }
        maquinaContratoRepository.deleteById(id);
    }

    private void validarEntrada(MaquinaContratoCreateDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da maquina do contrato sao obrigatorios");
        }

        validarId(dto.getCodigoContrato(), "Codigo do contrato e obrigatorio");
        validarId(dto.getCodigoCatalogoMaquina(), "Codigo do catalogo de maquina e obrigatorio");

        if (!contratoRepository.existsById(dto.getCodigoContrato())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato nao encontrado");
        }

        if (!catalogoMaquinaRepository.existsById(dto.getCodigoCatalogoMaquina())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalogo de maquina nao encontrado");
        }

        validarLatitudeLongitude(dto.getLatitude(), dto.getLongitude());
    }

    private void validarLatitudeLongitude(BigDecimal latitude, BigDecimal longitude) {
        if (latitude != null && (latitude.compareTo(BigDecimal.valueOf(-90)) < 0
                || latitude.compareTo(BigDecimal.valueOf(90)) > 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Latitude deve estar entre -90 e 90");
        }

        if (longitude != null && (longitude.compareTo(BigDecimal.valueOf(-180)) < 0
                || longitude.compareTo(BigDecimal.valueOf(180)) > 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Longitude deve estar entre -180 e 180");
        }
    }

    private void mapearParaEntidade(MaquinaContratoCreateDTO dto, MaquinaContrato entidade) {
        entidade.setCodigoContrato(dto.getCodigoContrato());
        entidade.setCodigoCatalogoMaquina(dto.getCodigoCatalogoMaquina());
        entidade.setNumeroSerie(dto.getNumeroSerie());
        entidade.setDataInstalacao(dto.getDataInstalacao());
        entidade.setManutencaoFeita(dto.getManutencaoFeita());
        entidade.setTrabalhoEmAltura(dto.getTrabalhoEmAltura() != null ? dto.getTrabalhoEmAltura() : Boolean.FALSE);
        entidade.setLatitude(dto.getLatitude());
        entidade.setLongitude(dto.getLongitude());
    }

    private void validarId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
    }
}
