package com.example.tracker.service;

import com.example.tracker.dto.tecnico.TecnicoCreateDTO;
import com.example.tracker.entity.Perfil;
import com.example.tracker.entity.Tecnico;
import com.example.tracker.entity.Usuario;
import com.example.tracker.repository.PerfilRepository;
import com.example.tracker.repository.TecnicoRepository;
import com.example.tracker.repository.UsuarioRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Tecnico criar(TecnicoCreateDTO dto) {
        validarTecnico(dto);

        Perfil perfilTecnico = perfilRepository.findByNome("ROLE_TECNICO")
                .orElseThrow(() -> new IllegalArgumentException("Perfil ROLE_TECNICO nao encontrado."));

        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(normalizar(dto.getEmail()));
        novoUsuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        novoUsuario.setAtivo(true);
        novoUsuario.setPerfis(Set.of(perfilTecnico));

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        Tecnico novoTecnico = new Tecnico();
        novoTecnico.setUsuario(usuarioSalvo);
        novoTecnico.setNome(limpar(dto.getNome()));
        novoTecnico.setCpf(limpar(dto.getCpf()));
        novoTecnico.setCargo(limpar(dto.getCargo()));
        novoTecnico.setTelefone(limpar(dto.getTelefone()));
        novoTecnico.setLatitude(dto.getLatitude());
        novoTecnico.setLongitude(dto.getLongitude());

        return tecnicoRepository.save(novoTecnico);
    }

    @Transactional(readOnly = true)
    public List<Tecnico> listarTecnicos() {
        return tecnicoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Tecnico> buscarPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("O id do tecnico e obrigatorio.");
        }
        return tecnicoRepository.findById(id);
    }

    @Transactional
    public Tecnico atualizar(Integer id, TecnicoCreateDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("O id do tecnico e obrigatorio.");
        }

        if (dto == null) {
            throw new IllegalArgumentException("Os dados do tecnico sao obrigatorios.");
        }

        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tecnico nao encontrado."));

        validarCpfDuplicado(dto, id);

        String nomeNormalizado = limpar(dto.getNome());
        if (nomeNormalizado == null) {
            throw new IllegalArgumentException("O nome do tecnico e obrigatorio.");
        }

        tecnico.setNome(nomeNormalizado);
        tecnico.setCpf(limpar(dto.getCpf()));
        tecnico.setCargo(limpar(dto.getCargo()));
        tecnico.setTelefone(limpar(dto.getTelefone()));
        tecnico.setLatitude(dto.getLatitude());
        tecnico.setLongitude(dto.getLongitude());

        return tecnicoRepository.save(tecnico);
    }

    @Transactional
    public void deletar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("O id do tecnico e obrigatorio.");
        }

        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tecnico nao encontrado."));

        tecnicoRepository.delete(Objects.requireNonNull(tecnico));
    }

    private void validarTecnico(TecnicoCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Os dados do tecnico sao obrigatorios.");
        }

        String emailNormalizado = normalizar(dto.getEmail());
        if (emailNormalizado == null) {
            throw new IllegalArgumentException("O email do usuario e obrigatorio.");
        }

        if (usuarioRepository.findByEmail(emailNormalizado).isPresent()) {
            throw new IllegalArgumentException("Ja existe usuario cadastrado com este email.");
        }

        String senhaLimpa = limpar(dto.getSenha());
        if (senhaLimpa == null) {
            throw new IllegalArgumentException("A senha do usuario e obrigatoria.");
        }

        String nomeNormalizado = limpar(dto.getNome());
        if (nomeNormalizado == null) {
            throw new IllegalArgumentException("O nome do tecnico e obrigatorio.");
        }

        String cpfNormalizado = limpar(dto.getCpf());
        if (cpfNormalizado != null && tecnicoRepository.findByCpf(cpfNormalizado).isPresent()) {
            throw new IllegalArgumentException("Ja existe tecnico cadastrado com este CPF.");
        }
    }

    private void validarCpfDuplicado(TecnicoCreateDTO dto, Integer idAtual) {
        String cpfNormalizado = limpar(dto.getCpf());

        if (cpfNormalizado == null) {
            return;
        }

        Optional<Tecnico> tecnicoExistente = tecnicoRepository.findByCpf(cpfNormalizado);

        if (tecnicoExistente.isPresent() && !tecnicoExistente.get().getId().equals(idAtual)) {
            throw new IllegalArgumentException("Ja existe tecnico cadastrado com este CPF.");
        }
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
