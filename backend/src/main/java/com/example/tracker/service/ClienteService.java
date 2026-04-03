package com.example.tracker.service;

import com.example.tracker.dto.cliente.ClienteCreateDTO;
import com.example.tracker.entity.Cliente;
import com.example.tracker.repository.ClienteRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente criar(ClienteCreateDTO dto) {
        validarClienteParaCriacao(dto);

        Cliente novoCliente = new Cliente();
        aplicarCampos(novoCliente, dto);
        novoCliente.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());

        return clienteRepository.save(novoCliente);
    }

    @Transactional
    public Optional<Cliente> atualizar(Integer id, ClienteCreateDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("O id do cliente e obrigatorio.");
        }

        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null) {
            return Optional.empty();
        }

        validarClienteParaAtualizacao(id, dto);
        aplicarCampos(cliente, dto);

        if (dto.getAtivo() != null) {
            cliente.setAtivo(dto.getAtivo());
        }

        if (dto.getInternacional() != null) {
            cliente.setInternacional(dto.getInternacional());
        }

        return Optional.ofNullable(clienteRepository.save(cliente));
    }

    @Transactional
    public boolean remover(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("O id do cliente e obrigatorio.");
        }
        if (!clienteRepository.existsById(id)) {
            return false;
        }
        clienteRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarClientes(String pais, String classificacaoDistancia, Integer page, Integer size) {
        Specification<Cliente> spec = Specification.where(comFiltrosOpcionais(pais, classificacaoDistancia));

        if (page != null || size != null) {
            int pagina = page == null ? 0 : page;
            int limite = size == null ? 10 : size;

            if (pagina < 0) {
                throw new IllegalArgumentException("O parametro page deve ser maior ou igual a zero.");
            }
            if (limite <= 0) {
                throw new IllegalArgumentException("O parametro size deve ser maior que zero.");
            }

            Pageable pageable = PageRequest.of(pagina, limite, Sort.by(Sort.Direction.ASC, "id"));
            return clienteRepository.findAll(spec, pageable).getContent();
        }

        return clienteRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "id"));
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("O id do cliente e obrigatorio.");
        }
        return clienteRepository.findById(id);
    }

    private void validarClienteParaCriacao(ClienteCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Os dados do cliente sao obrigatorios.");
        }

        String nomeNormalizado = limparString(dto.getNomeEmpresa());
        if (nomeNormalizado == null) {
            throw new IllegalArgumentException("O nome do cliente e obrigatorio.");
        }

        String emailNormalizado = normalizarString(dto.getEmailContato());
        if (emailNormalizado != null && clienteRepository.findByEmailContato(emailNormalizado).isPresent()) {
            throw new IllegalArgumentException("Ja existe cliente cadastrado com este email.");
        }

        String documentoNormalizado = limparString(dto.getDocumento());
        if (documentoNormalizado != null && clienteRepository.findByDocumento(documentoNormalizado).isPresent()) {
            throw new IllegalArgumentException("Ja existe cliente cadastrado com este documento.");
        }
    }

    private void validarClienteParaAtualizacao(Integer id, ClienteCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Os dados do cliente sao obrigatorios.");
        }

        String nomeNormalizado = limparString(dto.getNomeEmpresa());
        if (nomeNormalizado == null) {
            throw new IllegalArgumentException("O nome do cliente e obrigatorio.");
        }

        String emailNormalizado = normalizarString(dto.getEmailContato());
        if (emailNormalizado != null) {
            Optional<Cliente> clienteComEmail = clienteRepository.findByEmailContato(emailNormalizado);
            if (clienteComEmail.isPresent() && !clienteComEmail.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ja existe cliente cadastrado com este email.");
            }
        }

        String documentoNormalizado = limparString(dto.getDocumento());
        if (documentoNormalizado != null) {
            Optional<Cliente> clienteComDocumento = clienteRepository.findByDocumento(documentoNormalizado);
            if (clienteComDocumento.isPresent() && !clienteComDocumento.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ja existe cliente cadastrado com este documento.");
            }
        }
    }

    private void aplicarCampos(Cliente cliente, ClienteCreateDTO dto) {
        cliente.setNomeEmpresa(limparString(dto.getNomeEmpresa()));
        cliente.setDocumento(limparString(dto.getDocumento()));
        cliente.setEmailContato(normalizarString(dto.getEmailContato()));
        cliente.setTelefoneContato(limparString(dto.getTelefoneContato()));
        cliente.setNomeResponsavel(limparString(dto.getNomeResponsavel()));
        cliente.setPais(limparString(dto.getPais()));
        cliente.setEstadoRegiao(limparString(dto.getEstadoRegiao()));
        cliente.setCidade(limparString(dto.getCidade()));
        cliente.setClassificacaoDistancia(limparString(dto.getClassificacaoDistancia()));
        cliente.setFusoHorario(limparString(dto.getFusoHorario()));
        cliente.setObservacao(limparString(dto.getObservacao()));
        cliente.setRua(limparString(dto.getRua()));
        cliente.setNumero(limparString(dto.getNumero()));
        if (dto.getInternacional() != null) {
            cliente.setInternacional(dto.getInternacional());
        }
    }


    private String normalizarString(String valor) {
        if (valor == null) {
            return null;
        }

        String valorLimpo = valor.trim();
        if (valorLimpo.isEmpty()) {
            return null;
        }

        return valorLimpo.toLowerCase();
    }

    private String limparString(String valor) {
        if (valor == null) {
            return null;
        }

        String valorLimpo = valor.trim();
        if (valorLimpo.isEmpty()) {
            return null;
        }

        return valorLimpo;
    }

    private Specification<Cliente> comFiltrosOpcionais(String pais, String classificacaoDistancia) {
        String paisLimpo = limparString(pais);
        String classificacaoLimpa = limparString(classificacaoDistancia);

        Specification<Cliente> spec = Specification.where(null);

        if (paisLimpo != null) {
            spec = spec.and((root, query, builder) ->
                    builder.equal(builder.lower(root.get("pais")), paisLimpo.toLowerCase()));
        }

        if (classificacaoLimpa != null) {
            spec = spec.and((root, query, builder) ->
                    builder.equal(builder.lower(root.get("classificacaoDistancia")), classificacaoLimpa.toLowerCase()));
        }

        return spec;
    }
}