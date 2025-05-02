package com.seekernaut.seekernaut.domain.ollama.service;

import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateRequestDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateResponseDto;
import com.seekernaut.seekernaut.domain.messages.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * <p>Serviço responsável pela geração de títulos concisos para conversas,
 * utilizando a API do Ollama.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
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
     * Realiza a chamada para a API do Ollama para gerar a completação (o título) de forma reativa.
     *
     * @param prompt O prompt a ser enviado para o modelo de geração de título.
     * @return Um Mono de String contendo o título gerado.
     */
    public Mono<String> generateTitleReativo(String prompt) {
        return ollamaService.generateCompletion(
                OllamaGenerateRequestDto.builder()
                        .model(titleGenerationModel)
                        .prompt(titleGenerationPrompt + prompt)
                        .stream(false) // Solicita uma resposta não em stream
                        .build()
        )
                .next() // Obtém o primeiro (e esperado único) item do Flux.
                .map(responseDto -> {
                    if (responseDto != null && responseDto.getResponse() != null && !responseDto.getResponse().trim().isEmpty()) {
                        return responseDto.getResponse().trim();
                    } else {
                        log.error("Erro ao gerar título ou resposta vazia.");
                        return "Sem título";
                    }
                });
    }
}
