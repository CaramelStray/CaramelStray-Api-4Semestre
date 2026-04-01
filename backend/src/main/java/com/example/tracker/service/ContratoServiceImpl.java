package com.example.tracker.service;

import com.example.tracker.dto.contrato.ContratoCreateDTO;
import com.example.tracker.entity.Contrato;
import com.example.tracker.repository.ContratoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContratoServiceImpl implements ContratoService {

    private final ContratoRepository repository;

    public ContratoServiceImpl(ContratoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrato> listarTodos() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Contrato buscarPorId(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id do contrato é obrigatório");
        }
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrato> buscarPorCliente(Integer codigoCliente) {
        if (codigoCliente == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código do cliente é obrigatório");
        }
        List<Contrato> contratos = repository.findByCodigoCliente(codigoCliente);
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
        return repository.save(contrato);
    }

    @Override
    @Transactional
    public Contrato atualizar(Integer id, ContratoCreateDTO contratoDTO) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id do contrato é obrigatório");
        }
        validarEntrada(contratoDTO);

        Contrato contrato = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado"));

        mapearParaEntidade(contratoDTO, contrato);
        return repository.save(contrato);
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id do contrato é obrigatório");
        }
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado");
        }
        repository.deleteById(id);
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
        contrato.setCodigoCliente(contratoDTO.getCodigoCliente());
        contrato.setDataInicio(contratoDTO.getDataInicio());
        contrato.setDataFim(contratoDTO.getDataFim());
        contrato.setStatus(contratoDTO.getStatus());
        contrato.setPeriodoManutencaoPreventiva(contratoDTO.getPeriodoManutencaoPreventiva());
        contrato.setConexaoInternet(contratoDTO.getConexaoInternet());
        contrato.setVencimentoManutencaoPreventiva(contratoDTO.getVencimentoManutencaoPreventiva());
    }
}
