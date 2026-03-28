package com.example.tracker.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteCreateDTO {

    @NotBlank(message = "O nome da empresa e obrigatorio.")
    private String nomeEmpresa;
    private String documento;

    @Email(message = "Informe um email de contato valido.")
    private String emailContato;
    private String telefoneContato;
    private String nomeResponsavel;
    private String pais;
    private String estadoRegiao;
    private String cidade;
    private String classificacaoDistancia;
    private String fusoHorario;
    private Boolean ativo;
}
