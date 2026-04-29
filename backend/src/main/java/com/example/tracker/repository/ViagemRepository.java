package com.example.tracker.repository;

import com.example.tracker.entity.Viagem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViagemRepository extends JpaRepository<Viagem, Integer> {

    List<Viagem> findByClienteId(Integer codigoCliente);

    List<Viagem> findByFuncionarioResponsavelId(Integer codigoFuncionarioResponsavel);

    List<Viagem> findByOrdemServicoCodigo(Integer codigoOrdemServico);

    List<Viagem> findByTipoViagemCodigo(Integer codigoTipoViagem);

    List<Viagem> findByStatus(String status);
}
