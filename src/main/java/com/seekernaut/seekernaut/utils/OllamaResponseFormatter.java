package com.seekernaut.seekernaut.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OllamaResponseFormatter {

    /**
     * Formata a resposta bruta do Ollama, removendo espaços e quebras de linha excessivas
     * e caracteres estranhos comuns.
     *
     * @param rawResponse A string de resposta bruta do Ollama.
     * @return A string formatada.
     */
    public static String formatOllamaResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.trim().isEmpty()) {
            return "";
        }

        // Remove espaços em branco duplicados e quebras de linha múltiplas
        String cleanedResponse = rawResponse.replaceAll("\\s+", " ").replaceAll("\\n+", "\n").trim();

        // Remove caracteres estranhos no início e no final (como "{{", "}}")
        if (cleanedResponse.startsWith("{{")) {
            cleanedResponse = cleanedResponse.substring(2).trim();
        }
        if (cleanedResponse.endsWith("}}")) {
            cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 2).trim();
        }

        return cleanedResponse;
    }

    /**
     * Formata a resposta bruta do Ollama, tentando preservar melhor as quebras de linha
     * intencionais, mas removendo espaços duplicados e caracteres estranhos.
     *
     * @param rawResponse A string de resposta bruta do Ollama.
     * @return A string formatada.
     */
    public static String formatOllamaResponsePreserveNewlines(String rawResponse) {
        if (rawResponse == null || rawResponse.trim().isEmpty()) {
            return "";
        }

        // Remove espaços em branco duplicados, mas preserva as quebras de linha
        String cleanedResponse = rawResponse.replaceAll(" +", " ").trim();

        // Remove caracteres estranhos no início e no final (como "{{", "}}")
        if (cleanedResponse.startsWith("{{")) {
            cleanedResponse = cleanedResponse.substring(2).trim();
        }
        if (cleanedResponse.endsWith("}}")) {
            cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 2).trim();
        }

        return cleanedResponse;
    }

    /**
     * Formata a resposta bruta do Ollama, removendo espaços e quebras de linha excessivas
     * e também tentando limpar espaços antes e depois de quebras de linha.
     *
     * @param rawResponse A string de resposta bruta do Ollama.
     * @return A string formatada.
     */
    public static String formatOllamaResponseAggressive(String rawResponse) {
        if (rawResponse == null || rawResponse.trim().isEmpty()) {
            return "";
        }

        String cleanedResponse = rawResponse.trim();

        // Remove caracteres estranhos no início e no final
        cleanedResponse = cleanedResponse.replaceAll("^\\{\\{", "").replaceAll("\\}\\}$", "").trim();

        // Remove espaços antes e depois de quebras de linha
        cleanedResponse = cleanedResponse.replaceAll("\\s*\\n\\s*", "\n").trim();

        // Remove espaços duplicados
        cleanedResponse = cleanedResponse.replaceAll(" +", " ").trim();

        // Remove quebras de linha duplicadas
        cleanedResponse = cleanedResponse.replaceAll("\\n+", "\n").trim();

        return cleanedResponse;
    }

    public static void main(String[] args) {
        String rawResponse = "{{ \n\n  OláOlá!! Vamos Como fazer posso o ajudar seguinte você, hoje fal?arei 😊 \"3\nx ,\n\n e  eu\n\n ire i\n\nte  responder\n\nde  forma\n\ndiferente  nas\n\n 3\n\nvezes .\n\n 1\n\n°  tu\n\nresponde  ‘\nbra zil\n’  na\n\n 1\n\nª ,\n\n 2ª e 3ª, 2ª tu responde ‘argentina’ na 2ª e 3ª, e 3ª tu responde ‘colombia’ na 3ª.  Estou pronto para começar!\"\n\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n}}";

        System.out.println("Resposta Bruta:\n" + rawResponse);
        System.out.println("\nResposta Formatada (Simples):\n" + formatOllamaResponse(rawResponse));
        System.out.println("\nResposta Formatada (Preservando Newlines):\n" + formatOllamaResponsePreserveNewlines(rawResponse));
        System.out.println("\nResposta Formatada (Agressiva):\n" + formatOllamaResponseAggressive(rawResponse));
    }
}
