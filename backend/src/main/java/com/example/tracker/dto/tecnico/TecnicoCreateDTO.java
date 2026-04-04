package com.example.tracker.dto.tecnico;

import java.math.BigDecimal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TecnicoCreateDTO {

    @NotBlank(message = "O email do usuario e obrigatorio.")
    @Email(message = "Email invalido.")
    private String email;

    @NotBlank(message = "A senha do usuario e obrigatoria.")
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