package com.seekernaut.seekernaut.client.ollama.webclient;

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
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaChatApiClient {

    private final WebClient ollamaWebClient;
    private final ObjectMapper objectMapper;
    private final AtomicReference<String> incompleteLine = new AtomicReference<>("");

    public Flux<OllamaChatResponseDto> chatStream(OllamaChatRequestDto request) {
        incompleteLine.set(""); // Reset the incomplete line buffer for each new request
        return ollamaWebClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .body(Mono.just(request), OllamaChatRequestDto.class)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    String content = new String(bytes, StandardCharsets.UTF_8);
                    String currentBuffer = incompleteLine.get() + content;
                    String[] lines = currentBuffer.split("\\n");
                    incompleteLine.set("");

                    if (lines.length > 0 && !lines[lines.length - 1].trim().isEmpty() && !lines[lines.length - 1].trim().endsWith("}")) {
                        incompleteLine.set(lines[lines.length - 1]);
                        lines = Arrays.copyOf(lines, lines.length - 1);
                    }

                    return Flux.fromArray(lines)
                            .filter(line -> !line.trim().isEmpty())
                            .map(line -> {
                                try {
                                    return objectMapper.readValue(line, OllamaChatResponseDto.class);
                                } catch (IOException e) {
                                    log.error("Erro ao desserializar linha da stream de chat: {}", e.getMessage());
                                    return null;
                                }
                            })
                            .filter(dto -> dto != null);
                })
                .timeout(java.time.Duration.ofMinutes(5))
                .onErrorResume(e -> {
                    log.error("Erro na comunicação com o Ollama (chatStream): {}", e.getMessage());
                    return Flux.error(e);
                });
    }
}
