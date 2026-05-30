package com.example.tracker.repository;

import com.example.tracker.entity.OrdemServicoTecnico;
import com.example.tracker.entity.OrdemServicoTecnicoId;
import com.example.tracker.entity.Tecnico;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrdemServicoTecnicoRepository extends JpaRepository<OrdemServicoTecnico, OrdemServicoTecnicoId> {

    boolean existsByOrdemServicoCodigoAndTecnicoId(Integer codigoOrdemServico, Integer codigoFuncionario);

    @Query("""
        select ost.tecnico
        from OrdemServicoTecnico ost
        where ost.ordemServico.codigo = :codigoOrdemServico
    """)
    List<Tecnico> findTecnicosByOrdemServicoCodigo(@Param("codigoOrdemServico") Integer codigoOrdemServico);

    @Modifying
    void deleteByOrdemServicoCodigo(Integer codigoOrdemServico);
}
