package com.example.tracker.enums;

import java.text.Normalizer;
import java.util.Locale;

public enum StatusAusenciaTecnico {
    ATIVA,
    CANCELADA;

    public static StatusAusenciaTecnico from(String valor) {
        String codigo = normalizarCodigo(valor);
        if (codigo == null) {
            return null;
        }
        try {
            return StatusAusenciaTecnico.valueOf(codigo);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Status de ausencia invalido.");
        }
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
