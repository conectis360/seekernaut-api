package com.seekernaut.seekernaut.domain.ollama.service;

import com.seekernaut.seekernaut.api.ollama.dto.ModelListDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateRequestDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateResponseDto;
import com.seekernaut.seekernaut.client.ollama.api.OllamaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaService {
    private final OllamaClient ollamaClient;

    public ModelListDto listModels() {
        return ollamaClient.listModels();
    }

    public OllamaGenerateResponseDto generateCompletion(OllamaGenerateRequestDto body) {
        return ollamaClient.generateCompletion(body);
    }
}
