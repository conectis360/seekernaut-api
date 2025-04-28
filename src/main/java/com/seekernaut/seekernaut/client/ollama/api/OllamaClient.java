package com.seekernaut.seekernaut.client.ollama.api;

import com.seekernaut.seekernaut.api.ollama.dto.ModelListDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaModelInfoDTO;
import com.seekernaut.seekernaut.client.ollama.config.OllamaClientConfig;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "ollamaClient", configuration = OllamaClientConfig.class, url = "http://localhost:11434/")
public interface OllamaClient {

    @Headers("Content-Type: application/json")
    @GetMapping("/api/tags")
    ModelListDto listModels();
}
