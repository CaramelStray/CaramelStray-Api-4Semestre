package com.example.tracker.repository;

import com.example.tracker.entity.MaquinaHistoricoManutencao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaquinaHistoricoManutencaoRepository extends JpaRepository<MaquinaHistoricoManutencao, Integer> {

    List<MaquinaHistoricoManutencao> findByMaquinaContratoCodigo(Integer codigoMaquinaContrato);

    List<MaquinaHistoricoManutencao> findBySoftwareInstaladoCodigo(Integer codigoSoftwareInstalado);

    @Query(
            value = """
                    select case when count(*) > 0 then true else false end
                    from tb_srv_maquina_funcionario_manutencao
                    where codigo_historico_manutencao = :codigoHistorico
                      and codigo_funcionario = :codigoFuncionario
                    """,
            nativeQuery = true)
    boolean existsTecnicoVinculado(
            @Param("codigoHistorico") Integer codigoHistorico,
            @Param("codigoFuncionario") Integer codigoFuncionario);
}
