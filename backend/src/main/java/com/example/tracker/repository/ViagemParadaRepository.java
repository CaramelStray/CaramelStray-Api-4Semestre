package com.example.tracker.repository;

import com.example.tracker.entity.ViagemParada;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViagemParadaRepository extends JpaRepository<ViagemParada, Integer> {

    List<ViagemParada> findByViagemCodigoOrderByOrdemAsc(Integer codigoViagem);
}
