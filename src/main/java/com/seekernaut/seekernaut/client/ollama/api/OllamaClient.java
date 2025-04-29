package com.seekernaut.seekernaut.client.ollama.api;

import com.seekernaut.seekernaut.api.ollama.dto.ModelListDto;
import com.seekernaut.seekernaut.client.ollama.config.OllamaClientConfig;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "ollamaClient", configuration = OllamaClientConfig.class, url = "${ollama.base-url}")
public interface OllamaClient {

    @Headers("Content-Type: application/json")
    @GetMapping("/api/tags")
    ModelListDto listModels();
    
}
