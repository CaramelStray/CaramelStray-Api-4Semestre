package com.example.tracker.service;

import com.example.tracker.dto.maquinasoftwareinstalado.MaquinaSoftwareInstaladoCreateDTO;
import com.example.tracker.entity.CatalogoSoftware;
import com.example.tracker.entity.MaquinaContrato;
import com.example.tracker.entity.MaquinaSoftwareInstalado;
import com.example.tracker.repository.CatalogoSoftwareRepository;
import com.example.tracker.repository.MaquinaContratoRepository;
import com.example.tracker.repository.MaquinaSoftwareInstaladoRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MaquinaSoftwareInstaladoServiceImpl implements MaquinaSoftwareInstaladoService {

    private final MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository;
    private final MaquinaContratoRepository maquinaContratoRepository;
    private final CatalogoSoftwareRepository catalogoSoftwareRepository;

    public MaquinaSoftwareInstaladoServiceImpl(
            MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository,
            MaquinaContratoRepository maquinaContratoRepository,
            CatalogoSoftwareRepository catalogoSoftwareRepository) {
        this.maquinaSoftwareInstaladoRepository = maquinaSoftwareInstaladoRepository;
        this.maquinaContratoRepository = maquinaContratoRepository;
        this.catalogoSoftwareRepository = catalogoSoftwareRepository;
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

    private Integer requireId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return id;
    }
}
