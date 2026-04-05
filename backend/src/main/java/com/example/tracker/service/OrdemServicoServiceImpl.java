package com.example.tracker.service;

import com.example.tracker.dto.ordemservico.OrdemServicoCreateDTO;
import com.example.tracker.entity.Cliente;
import com.example.tracker.entity.Contrato;
import com.example.tracker.entity.MaquinaContrato;
import com.example.tracker.entity.MaquinaSoftwareInstalado;
import com.example.tracker.entity.OrdemServico;
import com.example.tracker.entity.Tecnico;
import com.example.tracker.repository.ClienteRepository;
import com.example.tracker.repository.ContratoRepository;
import com.example.tracker.repository.MaquinaContratoRepository;
import com.example.tracker.repository.MaquinaSoftwareInstaladoRepository;
import com.example.tracker.repository.OrdemServicoRepository;
import com.example.tracker.repository.TecnicoRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

@Service
public class OrdemServicoServiceImpl implements OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository;
    private final ContratoRepository contratoRepository;
    private final MaquinaContratoRepository maquinaContratoRepository;

    public OrdemServicoServiceImpl(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            TecnicoRepository tecnicoRepository,
            MaquinaSoftwareInstaladoRepository maquinaSoftwareInstaladoRepository,
            ContratoRepository contratoRepository,
            MaquinaContratoRepository maquinaContratoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.maquinaSoftwareInstaladoRepository = maquinaSoftwareInstaladoRepository;
        this.contratoRepository = contratoRepository;
        this.maquinaContratoRepository = maquinaContratoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> listarTodos() {
        return ordemServicoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemServico buscarPorId(Integer id) {
        Integer idNaoNulo = requireId(id, "Id da ordem de servico e obrigatorio");
        return ordemServicoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorCliente(Integer codigoCliente) {
        Integer codigoClienteNaoNulo =
                requireId(codigoCliente, "Codigo do cliente e obrigatorio");

        List<OrdemServico> itens =
                ordemServicoRepository.findByClienteCodigo(Objects.requireNonNull(codigoClienteNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma ordem de servico encontrada para o cliente");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorFuncionario(Integer codigoFuncionario) {
        Integer codigoFuncionarioNaoNulo =
                requireId(codigoFuncionario, "Codigo do funcionario e obrigatorio");

        List<OrdemServico> itens =
                ordemServicoRepository.findByFuncionarioCodigo(Objects.requireNonNull(codigoFuncionarioNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma ordem de servico encontrada para o funcionario");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorSoftwareInstalado(Integer codigoSoftwareInstalado) {
        Integer codigoSoftwareInstaladoNaoNulo =
                requireId(codigoSoftwareInstalado, "Codigo do software instalado e obrigatorio");

        List<OrdemServico> itens =
                ordemServicoRepository.findBySoftwareInstaladoCodigo(Objects.requireNonNull(codigoSoftwareInstaladoNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma ordem de servico encontrada para o software instalado");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorContrato(Integer codigoContrato) {
        Integer codigoContratoNaoNulo =
                requireId(codigoContrato, "Codigo do contrato e obrigatorio");

        List<OrdemServico> itens =
                ordemServicoRepository.findByContratoCodigo(Objects.requireNonNull(codigoContratoNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma ordem de servico encontrada para o contrato");
        }

        return itens;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorMaquinaContrato(Integer codigoMaquinaContrato) {
        Integer codigoMaquinaContratoNaoNulo =
                requireId(codigoMaquinaContrato, "Codigo da maquina do contrato e obrigatorio");

        List<OrdemServico> itens =
                ordemServicoRepository.findByMaquinaContratoCodigo(Objects.requireNonNull(codigoMaquinaContratoNaoNulo));

        if (itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma ordem de servico encontrada para a maquina do contrato");
        }

        return itens;
    }

    @Override
@Transactional
public OrdemServico cadastrar(OrdemServicoCreateDTO dto) {
    validarEntrada(dto);

    OrdemServico entidade = new OrdemServico();
    mapearParaEntidade(dto, entidade);

    if (entidade.getDataAbertura() == null) {
        entidade.setDataAbertura(LocalDateTime.now());
    }

    return ordemServicoRepository.save(entidade);
}
    @Override
    @Transactional
    public OrdemServico atualizar(Integer id, OrdemServicoCreateDTO dto) {
        Integer idNaoNulo = requireId(id, "Id da ordem de servico e obrigatorio");
        validarEntrada(dto);

        OrdemServico entidade = ordemServicoRepository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada"));

        mapearParaEntidade(dto, entidade);
        return ordemServicoRepository.save(Objects.requireNonNull(entidade));
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        Integer idNaoNulo = requireId(id, "Id da ordem de servico e obrigatorio");

        if (!ordemServicoRepository.existsById(Objects.requireNonNull(idNaoNulo))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de servico nao encontrada");
        }

        ordemServicoRepository.deleteById(Objects.requireNonNull(idNaoNulo));
    }

    private void validarEntrada(OrdemServicoCreateDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da ordem de servico sao obrigatorios");
        }

        Integer codigoClienteNaoNulo =
                requireId(dto.getCodigoCliente(), "Codigo do cliente e obrigatorio");
        Integer codigoContratoNaoNulo =
                requireId(dto.getCodigoContrato(), "Codigo do contrato e obrigatorio");
        Integer codigoMaquinaContratoNaoNulo =
                requireId(dto.getCodigoMaquinaContrato(), "Codigo da maquina do contrato e obrigatorio");

        Objects.requireNonNull(codigoClienteNaoNulo);
        Objects.requireNonNull(codigoContratoNaoNulo);
        Objects.requireNonNull(codigoMaquinaContratoNaoNulo);

        Integer codigoFuncionario = dto.getCodigoFuncionario();
        if (codigoFuncionario != null) {
            Objects.requireNonNull(codigoFuncionario);
        }

        Integer codigoSoftwareInstalado = dto.getCodigoSoftwareInstalado();
        if (codigoSoftwareInstalado != null) {
            Objects.requireNonNull(codigoSoftwareInstalado);
        }

        if (dto.getDataAgendamento() != null
                && dto.getDataInicioExecucao() != null
                && dto.getDataInicioExecucao().isBefore(dto.getDataAgendamento())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de inicio da execucao nao pode ser anterior a data de agendamento");
        }

        if (dto.getDataInicioExecucao() != null
                && dto.getDataFimExecucao() != null
                && dto.getDataFimExecucao().isBefore(dto.getDataInicioExecucao())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de fim da execucao nao pode ser anterior a data de inicio");
        }
    }

    private void mapearParaEntidade(OrdemServicoCreateDTO dto, OrdemServico entidade) {
        Integer codigoClienteNaoNulo =
                requireId(dto.getCodigoCliente(), "Codigo do cliente e obrigatorio");
        Integer codigoContratoNaoNulo =
                requireId(dto.getCodigoContrato(), "Codigo do contrato e obrigatorio");
        Integer codigoMaquinaContratoNaoNulo =
                requireId(dto.getCodigoMaquinaContrato(), "Codigo da maquina do contrato e obrigatorio");

        Cliente cliente = clienteRepository.findById(Objects.requireNonNull(codigoClienteNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente nao encontrado"));

        Contrato contrato = contratoRepository.findById(Objects.requireNonNull(codigoContratoNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato nao encontrado"));

        MaquinaContrato maquinaContrato = maquinaContratoRepository.findById(Objects.requireNonNull(codigoMaquinaContratoNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Maquina do contrato nao encontrada"));

        entidade.setCliente(cliente);
        entidade.setContrato(contrato);
        entidade.setMaquinaContrato(maquinaContrato);

        Integer codigoFuncionario = dto.getCodigoFuncionario();
        if (codigoFuncionario != null) {
            Tecnico funcionario = tecnicoRepository.findById(codigoFuncionario)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionario nao encontrado"));
            entidade.setFuncionario(funcionario);
        } else {
            entidade.setFuncionario(null);
        }

        Integer codigoSoftwareInstalado = dto.getCodigoSoftwareInstalado();
        if (codigoSoftwareInstalado != null) {
            MaquinaSoftwareInstalado softwareInstalado = maquinaSoftwareInstaladoRepository.findById(codigoSoftwareInstalado)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Software instalado nao encontrado"));
            entidade.setSoftwareInstalado(softwareInstalado);
        } else {
            entidade.setSoftwareInstalado(null);
        }

        entidade.setStatus(dto.getStatus());
        entidade.setCriticidade(dto.getCriticidade());
        entidade.setDataAbertura(dto.getDataAbertura());
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