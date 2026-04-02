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
public class CatalogoSoftwareChecklistPadraoId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "codigo_catalogo_software")
    private Integer codigoCatalogoSoftware;

    @Column(name = "codigo_catalogo_tarefa")
    private Integer codigoCatalogoTarefa;
}
