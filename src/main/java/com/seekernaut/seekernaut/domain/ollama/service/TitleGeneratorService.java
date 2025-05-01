package com.seekernaut.seekernaut.domain.ollama.service;

import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateRequestDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>Serviço responsável pela geração de títulos concisos para conversas,
 * utilizando a API do Ollama.</p>
 */
@Service
@RequiredArgsConstructor
public class TitleGeneratorService {

    private final OllamaService ollamaService;

    /**
     * Nome do modelo do Ollama a ser utilizado para a geração de títulos.
     * O valor é injetado da configuração da aplicação (application.properties ou application.yml).
     */
    @Value("${ollama.title-generation-model}")
    private String titleGenerationModel;

    /**
     * Prefixo a ser adicionado ao prompt enviado ao Ollama para a geração de títulos.
     * Permite instruir o modelo sobre o formato e o objetivo do título.
     * O valor é injetado da configuração da aplicação (com um valor padrão caso não seja configurado).
     */
    @Value("${ollama.title-generation-prompt}")
    private String titleGenerationPrompt;

    /**
     * <p>Gera um título conciso para um dado texto (prompt) utilizando o modelo Ollama configurado.</p>
     * <p>Este método constrói uma requisição para a API do Ollama, solicitando uma resposta única
     * (sem streaming) para a geração do título. A primeira resposta do fluxo é bloqueada e utilizada
     * como o título gerado.</p>
     *
     * @param prompt O texto base para o qual o título deve ser gerado. Este texto será concatenado
     *               com o {@link #titleGenerationPrompt}. Geralmente, é a primeira mensagem do usuário
     *               ou um resumo das primeiras interações.
     * @return Uma {@link String} contendo o título gerado pelo modelo Ollama. Retorna "Sem título"
     * em caso de erro, resposta vazia ou nula do modelo.
     */
    public String generateTitle(String prompt) {
        // Realiza a chamada para a API do Ollama para gerar a completação (o título).
        // O parâmetro 'stream(false)' indica que esperamos uma resposta única.
        OllamaGenerateResponseDto responseDto = ollamaService.generateCompletion(
                OllamaGenerateRequestDto.builder()
                        .model(titleGenerationModel) // Utiliza o modelo configurado
                        .prompt(titleGenerationPrompt + prompt) // Combina o prefixo com o prompt fornecido
                        .stream(false) // Solicita uma resposta não em stream
                        .build()
        ).blockFirst(); // Bloqueia a thread e obtém o primeiro (e esperado único) item do Flux.
        // Se o Flux estiver vazio, 'responseDto' será null.

        // Verifica se a resposta do modelo não é nula e se contém um texto de resposta não vazio.
        if (responseDto != null && responseDto.getResponse() != null && !responseDto.getResponse().trim().isEmpty()) {
            // Retorna o texto da resposta (o título), removendo espaços em branco no início e no fim.
            return responseDto.getResponse().trim();
        } else {
            // Se a resposta for nula ou vazia, loga um erro e retorna um título padrão.
            System.err.println("Erro ao gerar título ou resposta vazia.");
            return "Sem título";
        }
    }
}
