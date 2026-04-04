package com.example.tracker.dto.software;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogoSoftwareCreateDTO {

    @NotBlank(message = "O nome do software e obrigatorio.")
    private String nomeSoftware;

    @NotBlank(message = "A versao do software e obrigatoria.")
    private String versao;

    @NotBlank(message = "O tipo do software e obrigatorio.")
    private String tipo;

    @NotBlank(message = "O desenvolvedor ou fornecedor e obrigatorio.")
    private String desenvolvedorFornecedor;

    private String urlDocumentacao;
    private String descricaoTecnica;
    private Boolean ativo;

    @Valid
    private List<SoftwareChecklistItemCreateDTO> checklistPadrao;
}
