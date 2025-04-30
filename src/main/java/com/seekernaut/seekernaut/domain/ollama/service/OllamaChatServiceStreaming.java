package com.seekernaut.seekernaut.domain.ollama.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seekernaut.seekernaut.api.ollamastreaming.dto.OllamaChatRequestDto;
import com.seekernaut.seekernaut.api.ollamastreaming.dto.OllamaChatResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>Serviço responsável pela comunicação com a API de chat do Ollama.</p>
 * <p>Esta classe utiliza o {@link WebClient} para interagir de forma não bloqueante
 * com o endpoint `/chat` da API do Ollama, suportando a recepção de respostas em stream.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaChatServiceStreaming {

    private final WebClient ollamaWebClient;
    private final ObjectMapper objectMapper;

    /**
     * Utilizado para armazenar temporariamente a parte de uma linha JSON incompleta
     * que pode ser recebida em um {@link DataBuffer} e completada no próximo.
     * Garante que tentaremos desserializar apenas linhas JSON completas.
     */
    private final AtomicReference<String> incompleteLine = new AtomicReference<>("");

    /**
     * <p>Envia uma requisição de chat para a API do Ollama e retorna um fluxo da resposta.</p>
     * <p>Este método configura o {@link WebClient} para enviar uma requisição POST assíncrona
     * para o endpoint `/chat` e processa a resposta como um fluxo de eventos JSON, onde cada evento
     * é desserializado para um {@link OllamaChatResponseDto}.</p>
     *
     * @param request {@link OllamaChatRequestDto} contendo os detalhes da conversa e os parâmetros de geração.
     * @return {@link Flux} de {@link OllamaChatResponseDto}, representando a stream da resposta do chat do Ollama.
     * Se o Ollama não enviar a resposta em stream, o Flux conterá um único evento com a resposta completa.
     */
    public Flux<OllamaChatResponseDto> getChatCompletionStream(OllamaChatRequestDto request) {
        return ollamaWebClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON) // Define o tipo de conteúdo da requisição como JSON
                .accept(MediaType.TEXT_EVENT_STREAM) // Define o tipo de mídia esperado na resposta como um fluxo de eventos de texto
                .body(Mono.just(request), OllamaChatRequestDto.class) // Define o corpo da requisição como o objeto DTO convertido para um Mono
                .retrieve() // Realiza a requisição HTTP e obtém a resposta como um objeto de resposta do WebClient
                .bodyToFlux(DataBuffer.class) // Extrai o corpo da resposta como um Flux de DataBuffer (pedaços de dados binários)
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    String content = new String(bytes, StandardCharsets.UTF_8);
                    String currentBuffer = incompleteLine.get() + content;
                    String[] lines = currentBuffer.split("\\n");
                    incompleteLine.set(""); // Limpa o buffer de linha incompleta

                    // Se a última linha não parece ser um JSON completo, armazena para a próxima iteração
                    if (lines.length > 0 && !lines[lines.length - 1].trim().isEmpty() && !lines[lines.length - 1].trim().endsWith("}")) {
                        incompleteLine.set(lines[lines.length - 1]);
                        lines = Arrays.copyOf(lines, lines.length - 1);
                    }

                    return Flux.fromArray(lines)
                            .filter(line -> !line.trim().isEmpty()) // Filtra linhas vazias
                            .map(line -> {
                                try {
                                    // Tenta desserializar a linha JSON para um objeto OllamaChatResponseDto
                                    return objectMapper.readValue(line, OllamaChatResponseDto.class);
                                } catch (IOException e) {
                                    System.err.println("Erro ao desserializar linha da stream de chat: " + e.getMessage());
                                    return null; // Sinaliza um erro de desserialização
                                }
                            })
                            .filter(dto -> dto != null); // Filtra os objetos DTO que foram desserializados com sucesso (não nulos)
                })
                .timeout(Duration.ofMinutes(5)); // Define um timeout para a stream completa de 5 minutos
    }
}
