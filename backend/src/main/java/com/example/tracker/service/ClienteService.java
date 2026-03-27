package com.example.tracker.service;

import com.example.tracker.dto.cliente.ClienteCreateDTO;
import com.example.tracker.entity.Cliente;
import com.example.tracker.entity.Usuario;
import com.example.tracker.repository.ClienteRepository;
import com.example.tracker.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Cliente criar(ClienteCreateDTO dto, String emailUsuarioLogado) {
        validarCliente(dto);

        Usuario usuario = buscarUsuarioResponsavel(emailUsuarioLogado);
        Cliente novoCliente = new Cliente();
        novoCliente.setUsuario(usuario);
        novoCliente.setNomeEmpresa(limpar(dto.getNomeEmpresa()));
        novoCliente.setDocumento(limpar(dto.getDocumento()));
        novoCliente.setEmailContato(normalizar(dto.getEmailContato()));
        novoCliente.setTelefoneContato(limpar(dto.getTelefoneContato()));
        novoCliente.setNomeResponsavel(limpar(dto.getNomeResponsavel()));
        novoCliente.setPais(limpar(dto.getPais()));
        novoCliente.setEstadoRegiao(limpar(dto.getEstadoRegiao()));
        novoCliente.setCidade(limpar(dto.getCidade()));
        novoCliente.setClassificacaoDistancia(limpar(dto.getClassificacaoDistancia()));
        novoCliente.setFusoHorario(limpar(dto.getFusoHorario()));
        novoCliente.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());

        return clienteRepository.save(novoCliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("O id do cliente e obrigatorio.");
        }
        return clienteRepository.findById(id);
    }

    private void validarCliente(ClienteCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Os dados do cliente sao obrigatorios.");
        }

        String nomeNormalizado = limpar(dto.getNomeEmpresa());
        if (nomeNormalizado == null) {
            throw new IllegalArgumentException("O nome do cliente e obrigatorio.");
        }

        String emailNormalizado = normalizar(dto.getEmailContato());
        if (emailNormalizado != null && clienteRepository.findByEmailContato(emailNormalizado).isPresent()) {
            throw new IllegalArgumentException("Ja existe cliente cadastrado com este email.");
        }

        String documentoNormalizado = limpar(dto.getDocumento());
        if (documentoNormalizado != null && clienteRepository.findByDocumento(documentoNormalizado).isPresent()) {
            throw new IllegalArgumentException("Ja existe cliente cadastrado com este documento.");
        }

    }

    private Usuario buscarUsuarioResponsavel(String emailUsuarioLogado) {
        String emailNormalizado = normalizar(emailUsuarioLogado);
        if (emailNormalizado == null) {
            throw new IllegalStateException("Nao foi possivel identificar o usuario autenticado.");
        }
        return usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado nao encontrado."));
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return null;
        }

        String valorLimpo = valor.trim();
        if (valorLimpo.isEmpty()) {
            return null;
        }

        return valorLimpo.toLowerCase();
    }

    private String limpar(String valor) {
        if (valor == null) {
            return null;
        }

        String valorLimpo = valor.trim();
        if (valorLimpo.isEmpty()) {
            return null;
        }

        return valorLimpo;
    }
}
