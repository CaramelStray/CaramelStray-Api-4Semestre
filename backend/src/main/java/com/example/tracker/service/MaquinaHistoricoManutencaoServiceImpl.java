package com.example.tracker.service;

import com.example.tracker.dto.maquinahistoricomanutencao.MaquinaHistoricoManutencaoCreateDTO;
import com.example.tracker.entity.MaquinaHistoricoManutencao;
import com.example.tracker.repository.MaquinaContratoRepository;
import com.example.tracker.repository.MaquinaHistoricoManutencaoRepository;
import com.example.tracker.repository.MaquinaSoftwareInstaladoRepository;
import com.example.tracker.repository.TipoManutencaoRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MaquinaHistoricoManutencaoServiceImpl implements MaquinaHistoricoManutencaoService {

    private final MaquinaHistoricoManutencaoRepository maquinaHistoricoManutencaoRepository;
    private final MaquinaContratoRepository maquinaContratoRepository;
    private final MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository;
    private final TipoManutencaoRepository tipoManutencaoRepository;

    public MaquinaHistoricoManutencaoServiceImpl(
            MaquinaHistoricoManutencaoRepository maquinaHistoricoManutencaoRepository,
            MaquinaContratoRepository maquinaContratoRepository,
            MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository,
            TipoManutencaoRepository tipoManutencaoRepository) {
        this.maquinaHistoricoManutencaoRepository = maquinaHistoricoManutencaoRepository;
        this.maquinaContratoRepository = maquinaContratoRepository;
        this.maquinaSoftwareInstaladoRepository = maquinaSoftwareInstaladoRepository;
        this.tipoManutencaoRepository = tipoManutencaoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaHistoricoManutencao> listarTodos() {
        return maquinaHistoricoManutencaoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public MaquinaHistoricoManutencao buscarPorId(Integer id) {
        Integer idNaoNulo = requireId(id, "Id do historico de manutencao e obrigatorio");
        return maquinaHistoricoManutencaoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Historico de manutencao nao encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaHistoricoManutencao> buscarPorMaquinaContrato(Integer codigoMaquinaContrato) {
        Integer codigoMaquinaContratoNaoNulo =
                requireId(codigoMaquinaContrato, "Codigo da maquina do contrato e obrigatorio");
        List<MaquinaHistoricoManutencao> itens = maquinaHistoricoManutencaoRepository.findByCodigoMaquinaContrato(
                Objects.requireNonNull(codigoMaquinaContratoNaoNulo));
        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum historico encontrado para a maquina");
        }
        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaquinaHistoricoManutencao> buscarPorSoftwareInstalado(Integer codigoSoftwareInstalado) {
        Integer codigoSoftwareInstaladoNaoNulo =
                requireId(codigoSoftwareInstalado, "Codigo do software instalado e obrigatorio");
        List<MaquinaHistoricoManutencao> itens =
                maquinaHistoricoManutencaoRepository.findByCodigoSoftwareInstalado(codigoSoftwareInstaladoNaoNulo);
        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum historico encontrado para o software instalado");
        }
        return itens;
    }

    @Override
    @Transactional
    public MaquinaHistoricoManutencao cadastrar(MaquinaHistoricoManutencaoCreateDTO dto) {
        validarEntrada(dto);
        MaquinaHistoricoManutencao entidade = new MaquinaHistoricoManutencao();
        mapearParaEntidade(dto, entidade);
        return maquinaHistoricoManutencaoRepository.save(entidade);
    }

    @Override
    @Transactional
    public MaquinaHistoricoManutencao atualizar(Integer id, MaquinaHistoricoManutencaoCreateDTO dto) {
        Integer idNaoNulo = requireId(id, "Id do historico de manutencao e obrigatorio");
        validarEntrada(dto);

        MaquinaHistoricoManutencao entidade =
                maquinaHistoricoManutencaoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Historico de manutencao nao encontrado"));

        mapearParaEntidade(dto, entidade);
        return maquinaHistoricoManutencaoRepository.save(Objects.requireNonNull(entidade));
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        Integer idNaoNulo = requireId(id, "Id do historico de manutencao e obrigatorio");
        if (!maquinaHistoricoManutencaoRepository.existsById(Objects.requireNonNull(idNaoNulo))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Historico de manutencao nao encontrado");
        }
        maquinaHistoricoManutencaoRepository.deleteById(Objects.requireNonNull(idNaoNulo));
    }

    private void validarEntrada(MaquinaHistoricoManutencaoCreateDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do historico de manutencao sao obrigatorios");
        }

        Integer codigoMaquinaContratoNaoNulo =
                requireId(dto.getCodigoMaquinaContrato(), "Codigo da maquina do contrato e obrigatorio");
        Integer codigoTipoManutencaoNaoNulo =
                requireId(dto.getCodigoTipoManutencao(), "Codigo do tipo de manutencao e obrigatorio");

        if (!maquinaContratoRepository.existsById(Objects.requireNonNull(codigoMaquinaContratoNaoNulo))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada");
        }

        if (!tipoManutencaoRepository.existsById(Objects.requireNonNull(codigoTipoManutencaoNaoNulo))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de manutencao nao encontrado");
        }

        Integer codigoSoftwareInstalado = dto.getCodigoSoftwareInstalado();
        if (codigoSoftwareInstalado != null
                && !maquinaSoftwareInstaladoRepository.existsById(codigoSoftwareInstalado)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Software instalado nao encontrado");
        }

        if (dto.getDataInicioExecucao() != null
                && dto.getDataFimExecucao() != null
                && dto.getDataFimExecucao().isBefore(dto.getDataInicioExecucao())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de fim da execucao nao pode ser anterior a data de inicio");
        }
    }

    private void mapearParaEntidade(MaquinaHistoricoManutencaoCreateDTO dto, MaquinaHistoricoManutencao entidade) {
        entidade.setCodigoMaquinaContrato(dto.getCodigoMaquinaContrato());
        entidade.setCodigoSoftwareInstalado(dto.getCodigoSoftwareInstalado());
        entidade.setCodigoTipoManutencao(dto.getCodigoTipoManutencao());
        entidade.setStatus(dto.getStatus());
        entidade.setCriticidade(dto.getCriticidade());
        entidade.setVencimento(dto.getVencimento());
        entidade.setDataAgendamento(dto.getDataAgendamento());
        entidade.setDataInicioExecucao(dto.getDataInicioExecucao());
        entidade.setDataFimExecucao(dto.getDataFimExecucao());
        entidade.setObservacaoGeral(dto.getObservacaoGeral());
    }

    private Integer requireId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return id;
    }
}
