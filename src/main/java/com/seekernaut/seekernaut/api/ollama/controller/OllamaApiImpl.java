package com.seekernaut.seekernaut.api.ollama.controller;


import com.seekernaut.seekernaut.api.ollama.dto.ModelListDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateRequestDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateResponseDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaModelInfoDTO;
import com.seekernaut.seekernaut.domain.ollama.service.OllamaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
public class OllamaApiImpl implements OllamaApi {

    private final OllamaService ollamaService;

    @Override
    public ModelListDto listModels() {
        return ollamaService.listModels();
    }

    @Override
    public Mono<OllamaGenerateResponseDto> generateCompletion(OllamaGenerateRequestDto body) {
        return ollamaService.generateCompletion(body);
    }


}
