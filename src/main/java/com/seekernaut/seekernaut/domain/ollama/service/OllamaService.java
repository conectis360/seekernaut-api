package com.seekernaut.seekernaut.domain.ollama.service;

import com.seekernaut.seekernaut.api.ollama.dto.ModelListDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateRequestDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateResponseDto;
import com.seekernaut.seekernaut.client.ollama.feign.api.OllamaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaService {

    private final OllamaClient ollamaClient;
    private final WebClient ollamaWebClient;

    public ModelListDto listModels() {
        return ollamaClient.listModels();
    }

    public Mono<OllamaGenerateResponseDto> generateCompletion(OllamaGenerateRequestDto request) {
        return ollamaWebClient.post()
                .uri("/generate")
                .body(Mono.just(request), OllamaGenerateRequestDto.class)
                .retrieve()
                .bodyToMono(OllamaGenerateResponseDto.class)
                .timeout(Duration.ofMinutes(5)); // Timeout de 30 segundos
    }
}
