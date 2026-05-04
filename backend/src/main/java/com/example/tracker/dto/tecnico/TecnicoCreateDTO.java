package com.example.tracker.dto.tecnico;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TecnicoCreateDTO {

    private String email;

    private String senha;

    @NotBlank(message = "O nome do tecnico e obrigatorio.")
    private String nome;

    private String cpf;
    private String cargo;
    private String telefone;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String certificacao;
    private String estado;
    private String disponibilidade;
}