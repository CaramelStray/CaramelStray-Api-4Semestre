package com.example.tracker.enums;

import java.text.Normalizer;
import java.util.Locale;

public enum StatusTecnico {
    DISPONIVEL,
    EM_ATENDIMENTO,
    EM_ROTA,
    EM_VIAGEM,
    INDISPONIVEL;

    public static StatusTecnico from(String valor) {
        String codigo = normalizarCodigo(valor);
        if (codigo == null) {
            return null;
        }
        try {
            return StatusTecnico.valueOf(codigo);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Disponibilidade invalida.");
        }
    }

    public static String codigo(String valor) {
        StatusTecnico status = from(valor);
        return status == null ? null : status.name();
    }

    private static String normalizarCodigo(String valor) {
        if (valor == null) {
            return null;
        }
        String limpo = valor.trim();
        if (limpo.isEmpty()) {
            return null;
        }

        String semAcentos = Normalizer.normalize(limpo, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos
                .trim()
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }
}
