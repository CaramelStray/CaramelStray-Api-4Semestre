package com.example.tracker.service;

import com.example.tracker.dto.TecnicoHabilidade.TecnicoHabilidadeCreateDTO;
import com.example.tracker.dto.TecnicoHabilidade.TecnicoHabilidadeResponseDTO;
import com.example.tracker.entity.Habilidade;
import com.example.tracker.entity.Tecnico;
import com.example.tracker.entity.TecnicoHabilidade;
import com.example.tracker.entity.TecnicoHabilidadeId;
import com.example.tracker.repository.HabilidadeRepository;
import com.example.tracker.repository.TecnicoHabilidadeRepository;
import com.example.tracker.repository.TecnicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TecnicoHabilidadeServiceImpl implements TecnicoHabilidadeService {

    private final TecnicoHabilidadeRepository repository;
    private final TecnicoRepository tecnicoRepository;
    private final HabilidadeRepository habilidadeRepository;

    public TecnicoHabilidadeServiceImpl(
            TecnicoHabilidadeRepository repository,
            TecnicoRepository tecnicoRepository,
            HabilidadeRepository habilidadeRepository) {
        this.repository = repository;
        this.tecnicoRepository = tecnicoRepository;
        this.habilidadeRepository = habilidadeRepository;
    }

    @Override
    @Transactional
    public TecnicoHabilidadeResponseDTO adicionarHabilidade(TecnicoHabilidadeCreateDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Dados obrigatorios.");
        }

        Tecnico tecnico = tecnicoRepository.findById(dto.getTecnicoId())
                .orElseThrow(() -> new IllegalArgumentException("Tecnico nao encontrado."));

        Habilidade habilidade = habilidadeRepository.findById(dto.getHabilidadeId())
                .orElseThrow(() -> new IllegalArgumentException("Habilidade nao encontrada."));

        TecnicoHabilidadeId id = new TecnicoHabilidadeId();
        id.setTecnicoId(tecnico.getId());
        id.setHabilidadeId(habilidade.getCodigo());

        if (repository.existsById(id)) {
            throw new IllegalArgumentException("Tecnico ja possui essa habilidade.");
        }

        TecnicoHabilidade relacao = new TecnicoHabilidade();
        relacao.setId(id);
        relacao.setTecnico(tecnico);
        relacao.setHabilidade(habilidade);
        relacao.setNivel(dto.getNivel());
        relacao.setDataValidade(dto.getDataValidade());

        return toDTO(repository.save(relacao));
    }

    @Override
    @Transactional
    public void removerHabilidade(Integer tecnicoId, Integer habilidadeId) {

        TecnicoHabilidadeId id = new TecnicoHabilidadeId();
        id.setTecnicoId(tecnicoId);
        id.setHabilidadeId(habilidadeId);

        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Relacionamento nao encontrado.");
        }

        repository.deleteById(id);
    }

    private TecnicoHabilidadeResponseDTO toDTO(TecnicoHabilidade entity) {
        TecnicoHabilidadeResponseDTO dto = new TecnicoHabilidadeResponseDTO();

        dto.setHabilidadeId(entity.getHabilidade().getCodigo());
        dto.setDescricaoHabilidade(entity.getHabilidade().getDescricao());

        dto.setNivel(entity.getNivel());
        dto.setDataValidade(entity.getDataValidade());

        return dto;
    }
}