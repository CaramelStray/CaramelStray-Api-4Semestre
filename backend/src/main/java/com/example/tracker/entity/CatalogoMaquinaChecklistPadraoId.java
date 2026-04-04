package com.example.tracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CatalogoMaquinaChecklistPadraoId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "codigo_catalogo_maquina")
    private Integer codigoCatalogoMaquina;

    @Column(name = "codigo_tarefa")
    private Integer codigoTarefa;
}
