package com.example.tracker.service;

import com.example.tracker.dto.ciclo.CicloEmbarcacaoCreateDTO;
import com.example.tracker.dto.ciclo.CicloEmbarcacaoResponseDTO;
import com.example.tracker.entity.CicloEmbarcacao;
import com.example.tracker.repository.CicloEmbarcacaoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CicloEmbarcacaoServiceImpl implements CicloEmbarcacaoService {

    private final CicloEmbarcacaoRepository cicloEmbarcacaoRepository;

    @Override
    @Transactional
    public CicloEmbarcacaoResponseDTO criar(CicloEmbarcacaoCreateDTO dto) {
        CicloEmbarcacao ciclo = new CicloEmbarcacao();
        mapearParaEntidade(dto, ciclo);
        return CicloEmbarcacaoResponseDTO.fromEntity(cicloEmbarcacaoRepository.save(ciclo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CicloEmbarcacaoResponseDTO> listar() {
        return cicloEmbarcacaoRepository.findAll().stream()
                .map(CicloEmbarcacaoResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CicloEmbarcacaoResponseDTO buscarPorId(Integer id) {
        return CicloEmbarcacaoResponseDTO.fromEntity(buscarEntidade(id));
    }

    @Override
    @Transactional
    public CicloEmbarcacaoResponseDTO atualizar(Integer id, CicloEmbarcacaoCreateDTO dto) {
        CicloEmbarcacao ciclo = buscarEntidade(id);
        mapearParaEntidade(dto, ciclo);
        return CicloEmbarcacaoResponseDTO.fromEntity(cicloEmbarcacaoRepository.save(ciclo));
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        if (!cicloEmbarcacaoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciclo de embarcacao nao encontrado");
        }
        cicloEmbarcacaoRepository.deleteById(id);
    }

    private CicloEmbarcacao buscarEntidade(Integer id) {
        return cicloEmbarcacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciclo de embarcacao nao encontrado"));
    }

    private void mapearParaEntidade(CicloEmbarcacaoCreateDTO dto, CicloEmbarcacao ciclo) {
        if (dto.getDataSaida() != null && dto.getDataSaida().isBefore(dto.getDataChegada())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de saida nao pode ser anterior a data de chegada");
        }

        ciclo.setEmbarcacao(dto.getEmbarcacao());
        ciclo.setDataChegada(dto.getDataChegada());
        ciclo.setDataSaida(dto.getDataSaida());
        ciclo.setLocal(dto.getLocal());
    }
}
