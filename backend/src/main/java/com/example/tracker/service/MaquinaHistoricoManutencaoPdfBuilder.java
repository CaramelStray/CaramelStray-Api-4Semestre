package com.example.tracker.service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

final class MaquinaHistoricoManutencaoPdfBuilder {

    private static final int MAX_CHARS_PER_LINE = 95;
    private static final int MAX_LINES_PER_PAGE = 52;

    private MaquinaHistoricoManutencaoPdfBuilder() {
    }

    static byte[] gerar(List<String> linhas) {
        List<List<String>> paginas = paginar(linhas);
        int quantidadePaginas = paginas.size();
        int codigoFonte = 3 + (quantidadePaginas * 2);

        List<String> objetos = new ArrayList<>();
        objetos.add("<< /Type /Catalog /Pages 2 0 R >>");
        objetos.add(montarObjetoPaginas(quantidadePaginas));

        for (int indice = 0; indice < quantidadePaginas; indice++) {
            int codigoPagina = 3 + (indice * 2);
            int codigoConteudo = codigoPagina + 1;
            String conteudo = montarConteudo(paginas.get(indice));
            int tamanhoConteudo = conteudo.getBytes(StandardCharsets.ISO_8859_1).length;

            objetos.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] "
                    + "/Resources << /Font << /F1 " + codigoFonte + " 0 R >> >> "
                    + "/Contents " + codigoConteudo + " 0 R >>");
            objetos.add("<< /Length " + tamanhoConteudo + " >>\nstream\n" + conteudo + "endstream");
        }

        objetos.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");
        return escreverPdf(objetos);
    }

    private static List<List<String>> paginar(List<String> linhas) {
        List<List<String>> paginas = new ArrayList<>();
        List<String> paginaAtual = new ArrayList<>();

        if (linhas == null || linhas.isEmpty()) {
            linhas = List.of("");
        }

        for (String linha : linhas) {
            for (String linhaQuebrada : quebrarLinha(linha)) {
                if (paginaAtual.size() >= MAX_LINES_PER_PAGE) {
                    paginas.add(paginaAtual);
                    paginaAtual = new ArrayList<>();
                }
                paginaAtual.add(linhaQuebrada);
            }
        }

        if (!paginaAtual.isEmpty()) {
            paginas.add(paginaAtual);
        }

        return paginas;
    }

    private static List<String> quebrarLinha(String linha) {
        String texto = normalizarTexto(linha);
        if (texto.length() <= MAX_CHARS_PER_LINE) {
            return List.of(texto);
        }

        List<String> partes = new ArrayList<>();
        int inicio = 0;
        while (inicio < texto.length()) {
            int fim = Math.min(inicio + MAX_CHARS_PER_LINE, texto.length());
            if (fim < texto.length()) {
                int ultimoEspaco = texto.lastIndexOf(' ', fim);
                if (ultimoEspaco > inicio) {
                    fim = ultimoEspaco;
                }
            }

            partes.add(texto.substring(inicio, fim).trim());
            inicio = fim;
            while (inicio < texto.length() && texto.charAt(inicio) == ' ') {
                inicio++;
            }
        }

        return partes;
    }

    private static String montarObjetoPaginas(int quantidadePaginas) {
        StringBuilder kids = new StringBuilder();
        for (int indice = 0; indice < quantidadePaginas; indice++) {
            kids.append(3 + (indice * 2)).append(" 0 R ");
        }
        return "<< /Type /Pages /Kids [" + kids + "] /Count " + quantidadePaginas + " >>";
    }

    private static String montarConteudo(List<String> linhas) {
        StringBuilder conteudo = new StringBuilder();
        conteudo.append("BT\n");
        conteudo.append("/F1 10 Tf\n");
        conteudo.append("50 800 Td\n");
        for (String linha : linhas) {
            conteudo.append("(").append(escaparTexto(linha)).append(") Tj\n");
            conteudo.append("0 -14 Td\n");
        }
        conteudo.append("ET\n");
        return conteudo.toString();
    }

    private static byte[] escreverPdf(List<String> objetos) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();

        escrever(out, "%PDF-1.4\n");
        for (int indice = 0; indice < objetos.size(); indice++) {
            offsets.add(out.size());
            escrever(out, (indice + 1) + " 0 obj\n");
            escrever(out, objetos.get(indice));
            escrever(out, "\nendobj\n");
        }

        int inicioXref = out.size();
        escrever(out, "xref\n");
        escrever(out, "0 " + (objetos.size() + 1) + "\n");
        escrever(out, "0000000000 65535 f \n");
        for (Integer offset : offsets) {
            escrever(out, String.format("%010d 00000 n \n", offset));
        }
        escrever(out, "trailer\n");
        escrever(out, "<< /Size " + (objetos.size() + 1) + " /Root 1 0 R >>\n");
        escrever(out, "startxref\n");
        escrever(out, inicioXref + "\n");
        escrever(out, "%%EOF");

        return out.toByteArray();
    }

    private static void escrever(ByteArrayOutputStream out, String conteudo) {
        byte[] bytes = conteudo.getBytes(StandardCharsets.ISO_8859_1);
        out.write(bytes, 0, bytes.length);
    }

    private static String escaparTexto(String texto) {
        return normalizarTexto(texto)
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private static String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.replaceAll("[^\\x20-\\x7E]", " ");
    }
}
