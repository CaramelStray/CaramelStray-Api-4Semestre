package com.example.tracker.enums;

import java.text.Normalizer;
import java.util.Locale;

public enum TipoAusenciaTecnico {
    FOLGA,
    AFASTAMENTO,
    FERIAS,
    LICENCA,
    OUTRO;

    public static TipoAusenciaTecnico from(String valor) {
        String codigo = normalizarCodigo(valor);
        if (codigo == null) {
            return null;
        }
        try {
            return TipoAusenciaTecnico.valueOf(codigo);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Tipo de ausencia invalido.");
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
