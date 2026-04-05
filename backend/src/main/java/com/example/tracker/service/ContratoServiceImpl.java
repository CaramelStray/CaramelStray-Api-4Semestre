package com.example.tracker.service;

import com.example.tracker.dto.contrato.ContratoCreateDTO;
import com.example.tracker.entity.Cliente;
import com.example.tracker.entity.Contrato;
import com.example.tracker.repository.ClienteRepository;
import com.example.tracker.repository.ContratoRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContratoServiceImpl implements ContratoService {

    private final ContratoRepository repository;
    private final ClienteRepository clienteRepository;

    public ContratoServiceImpl(ContratoRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrato> listarTodos() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Contrato buscarPorId(Integer id) {
        Integer idNaoNulo = requireId(id, "Id do contrato é obrigatório");
        return repository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrato> buscarPorCliente(Integer codigoCliente) {
        Integer codigoClienteNaoNulo = requireId(codigoCliente, "Código do cliente é obrigatório");
        List<Contrato> contratos = repository.findByClienteId(Objects.requireNonNull(codigoClienteNaoNulo));
        if (contratos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum contrato encontrado para o cliente");
        }
        return contratos;
    }

    @Override
    @Transactional
    public Contrato cadastrar(ContratoCreateDTO contratoDTO) {
        validarEntrada(contratoDTO);

        Contrato contrato = new Contrato();
        mapearParaEntidade(contratoDTO, contrato);

        contrato = repository.save(contrato);

        String descricao = String.format(
                "MER-%d-%04d",
                contrato.getDataInicio().getYear(),
                contrato.getCodigo());

        contrato.setDescricao(descricao);

        return repository.save(contrato);
    }

    @Override
    @Transactional
    public Contrato atualizar(Integer id, ContratoCreateDTO contratoDTO) {
        Integer idNaoNulo = requireId(id, "Id do contrato é obrigatório");
        validarEntrada(contratoDTO);

        Contrato contrato = repository.findById(Objects.requireNonNull(idNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado"));

        mapearParaEntidade(contratoDTO, contrato);
        return repository.save(Objects.requireNonNull(contrato));
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        Integer idNaoNulo = requireId(id, "Id do contrato é obrigatório");
        if (!repository.existsById(Objects.requireNonNull(idNaoNulo))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado");
        }
        repository.deleteById(Objects.requireNonNull(idNaoNulo));
    }

    private void validarEntrada(ContratoCreateDTO contratoDTO) {
        if (contratoDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do contrato são obrigatórios");
        }
        if (contratoDTO.getDataFim() != null && contratoDTO.getDataFim().isBefore(contratoDTO.getDataInicio())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Data de fim não pode ser antes da data de início");
        }
    }

    private void mapearParaEntidade(ContratoCreateDTO contratoDTO, Contrato contrato) {
        Integer codigoClienteNaoNulo = requireId(
                contratoDTO.getCodigoCliente(), "O codigo do cliente e obrigatorio.");
        Cliente cliente = clienteRepository.findById(Objects.requireNonNull(codigoClienteNaoNulo))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado com id: " + codigoClienteNaoNulo));
        contrato.setCliente(cliente);
        contrato.setDataInicio(contratoDTO.getDataInicio());
        contrato.setDataFim(contratoDTO.getDataFim());
        contrato.setStatus(contratoDTO.getStatus());
        contrato.setPeriodoManutencaoPreventiva(contratoDTO.getPeriodoManutencaoPreventiva());
        contrato.setConexaoInternet(contratoDTO.getConexaoInternet());
        contrato.setVencimentoManutencaoPreventiva(contratoDTO.getVencimentoManutencaoPreventiva());
    }

    private Integer requireId(Integer id, String mensagem) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
        return id;
    }
}
