package com.example.tracker.repository;

import com.example.tracker.entity.OrdemServico;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Integer> {

    List<OrdemServico> findByClienteId(Integer codigoCliente);

    List<OrdemServico> findByFuncionarioId(Integer codigoFuncionario);

    List<OrdemServico> findBySoftwareInstaladoCodigo(Integer codigoSoftwareInstalado);

    List<OrdemServico> findByContratoCodigo(Integer codigoContrato);

    List<OrdemServico> findByMaquinaContratoCodigo(Integer codigoMaquinaContrato);
}