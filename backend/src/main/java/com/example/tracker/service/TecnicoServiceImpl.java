package com.example.tracker.service;

import com.example.tracker.dto.TecnicoHabilidade.TecnicoHabilidadeResponseDTO;
import com.example.tracker.dto.tecnico.TecnicoCreateDTO;
import com.example.tracker.dto.tecnico.TecnicoResponseDTO;
import com.example.tracker.entity.Perfil;
import com.example.tracker.entity.Tecnico;
import com.example.tracker.entity.Usuario;
import com.example.tracker.enums.StatusTecnico;
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
public class TecnicoServiceImpl implements TecnicoService {

    private static final StatusTecnico DISPONIBILIDADE_PADRAO = StatusTecnico.DISPONIVEL;

    private final TecnicoRepository tecnicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TecnicoResponseDTO criar(TecnicoCreateDTO dto) {
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
        novoTecnico.setCertificacao(limpar(dto.getCertificacao()));
        novoTecnico.setEstado(limpar(dto.getEstado()));
        novoTecnico.setDisponibilidade(normalizarDisponibilidade(dto.getDisponibilidade()));

        Tecnico salvo = tecnicoRepository.save(novoTecnico);
        return toDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TecnicoResponseDTO> listarTecnicos() {
        return tecnicoRepository.findAllComHabilidades()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TecnicoResponseDTO> buscarPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("O id do tecnico e obrigatorio.");
        }

        return tecnicoRepository.findById(id)
                .map(this::toDTO);
    }

    @Override
    @Transactional
    public TecnicoResponseDTO atualizar(Integer id, TecnicoCreateDTO dto) {
        if (id == null) {
            throw new IllegalArgumentException("O id do tecnico e obrigatorio.");
        }

        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tecnico nao encontrado."));

        validarCpfDuplicado(dto, id);

        tecnico.setNome(limpar(dto.getNome()));
        tecnico.setCpf(limpar(dto.getCpf()));
        tecnico.setCargo(limpar(dto.getCargo()));
        tecnico.setTelefone(limpar(dto.getTelefone()));
        tecnico.setLatitude(dto.getLatitude());
        tecnico.setLongitude(dto.getLongitude());
        tecnico.setCertificacao(limpar(dto.getCertificacao()));
        tecnico.setEstado(limpar(dto.getEstado()));
        tecnico.setDisponibilidade(normalizarDisponibilidade(dto.getDisponibilidade()));

        if (tecnico.getUsuario() != null) {
            String novoEmail = normalizar(dto.getEmail());
            if (novoEmail != null && !novoEmail.equals(tecnico.getUsuario().getEmail())) {
                if (usuarioRepository.findByEmail(novoEmail).isPresent()) {
                    throw new IllegalArgumentException("Ja existe usuario cadastrado com este email.");
                }
                tecnico.getUsuario().setEmail(novoEmail);
            }
            if (dto.getSenha() != null && !dto.getSenha().trim().isEmpty()) {
                tecnico.getUsuario().setSenha(passwordEncoder.encode(dto.getSenha()));
            }
            usuarioRepository.save(tecnico.getUsuario());
        }

        Tecnico atualizado = tecnicoRepository.save(tecnico);
        return toDTO(atualizado);
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("O id do tecnico e obrigatorio.");
        }

        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tecnico nao encontrado."));

        tecnicoRepository.delete(Objects.requireNonNull(tecnico));
    }

    private TecnicoResponseDTO toDTO(Tecnico tecnico) {
        TecnicoResponseDTO dto = new TecnicoResponseDTO();

        dto.setId(tecnico.getId());
        if (tecnico.getUsuario() != null) {
            dto.setEmail(tecnico.getUsuario().getEmail());
        }
        dto.setNome(tecnico.getNome());
        dto.setCpf(tecnico.getCpf());
        dto.setCargo(tecnico.getCargo());
        dto.setTelefone(tecnico.getTelefone());
        dto.setCertificacao(tecnico.getCertificacao());
        dto.setEstado(tecnico.getEstado());
        dto.setDisponibilidade(tecnico.getDisponibilidade());
        dto.setLatitude(tecnico.getLatitude());
        dto.setLongitude(tecnico.getLongitude());

        if (tecnico.getHabilidades() != null) {
            dto.setHabilidades(
                    tecnico.getHabilidades().stream().map(rel -> {
                        TecnicoHabilidadeResponseDTO th = new TecnicoHabilidadeResponseDTO();

                        th.setHabilidadeId(rel.getHabilidade().getCodigo());
                        th.setDescricaoHabilidade(rel.getHabilidade().getDescricao());
                        th.setNivel(rel.getNivel());
                        th.setDataValidade(rel.getDataValidade());

                        return th;
                    }).toList()
            );
        }

        return dto;
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
        return valorLimpo.isEmpty() ? null : valorLimpo.toLowerCase();
    }

    private String limpar(String valor) {
        if (valor == null) {
            return null;
        }
        String valorLimpo = valor.trim();
        return valorLimpo.isEmpty() ? null : valorLimpo;
    }

    private String normalizarDisponibilidade(String valor) {
        StatusTecnico disponibilidade = StatusTecnico.from(valor);
        return disponibilidade == null ? DISPONIBILIDADE_PADRAO.name() : disponibilidade.name();
    }
}
