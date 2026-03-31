package com.example.tracker.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.tracker.entity.Contrato;
import com.example.tracker.repository.ContratoRepository;

import java.util.List;

@Service
public class ContratoService {

    private final ContratoRepository repository;

    public ContratoService(ContratoRepository repository) {
        this.repository = repository;
    }

    public List<Contrato> listarTodos() {
        return repository.findAll();
    }

    public Contrato buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado"));
    }

    public List<Contrato> buscarPorCliente(Integer codigoCliente) {
        List<Contrato> contratos = repository.findByCodigoCliente(codigoCliente);
        if (contratos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum contrato encontrado para o cliente");
        }
        return contratos;
    }

    public Contrato salvar(Contrato contrato) {
        if (contrato.getDataFim() != null && contrato.getDataFim().isBefore(contrato.getDataInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Data de fim não pode ser antes da data de início");
        }
        return repository.save(contrato);
    }

    public Contrato atualizar(Integer id, Contrato contratoAtualizado) {
        Contrato contrato = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado"));

        if (contratoAtualizado.getDataFim() != null &&
                contratoAtualizado.getDataFim().isBefore(contratoAtualizado.getDataInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Data de fim não pode ser antes da data de início");
        }

        contrato.setCodigoCliente(contratoAtualizado.getCodigoCliente());
        contrato.setDataInicio(contratoAtualizado.getDataInicio());
        contrato.setDataFim(contratoAtualizado.getDataFim());
        contrato.setStatus(contratoAtualizado.getStatus());
        contrato.setPeriodoManutencaoPreventiva(contratoAtualizado.getPeriodoManutencaoPreventiva());
        contrato.setConexaoInternet(contratoAtualizado.getConexaoInternet());
        contrato.setVencimentoManutencaoPreventiva(contratoAtualizado.getVencimentoManutencaoPreventiva());

        return repository.save(contrato);
    }

    public void deletar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado");
        }
        repository.deleteById(id);
    }
}