package com.seekernaut.seekernaut.client.ollama.api;

import com.seekernaut.seekernaut.api.ollama.dto.ModelListDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateRequestDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateResponseDto;
import com.seekernaut.seekernaut.client.ollama.config.OllamaClientConfig;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ollamaClient", configuration = OllamaClientConfig.class, url = "${ollama.base-url}")
public interface OllamaClient {

    @Headers("Content-Type: application/json")
    @GetMapping("/api/tags")
    ModelListDto listModels();


    @Headers("Content-Type: application/json")
    @PostMapping("/api/generate")
    OllamaGenerateResponseDto generateCompletion(@RequestBody OllamaGenerateRequestDto body);

    /* TODO: ler todos os métodos e ver quais serão adicionados;
    @Headers("Content-Type: application/json")
    @PostMapping("/api/chat")
    OllamaChatResponseDto generateChatCompletion(@RequestBody OllamaChatRequestDto body);

    @Headers("Content-Type: application/json")
    @PostMapping("/api/create")
    void createModel(@RequestBody OllamaCreateModelRequestDto body);

    @Headers("Content-Type: application/json")
    @GetMapping("/api/show")
    ShowModelInfoDto showModelInformation(@RequestParam("name") String name);

    @Headers("Content-Type: application/json")
    @PostMapping("/api/copy")
    void copyModel(@RequestBody OllamaCopyModelRequestDto body);

    @DeleteMapping("/api/delete")
    void deleteModel(@RequestParam("name") String name);

    @Headers("Content-Type: application/json")
    @PostMapping("/api/pull")
    void pullModel(@RequestBody OllamaPullModelRequestDto body);

    @Headers("Content-Type: application/json")
    @PostMapping("/api/push")
    void pushModel(@RequestBody OllamaPushModelRequestDto body);

    @Headers("Content-Type: application/json")
    @PostMapping("/api/embeddings")
    OllamaEmbeddingsResponseDto generateEmbeddings(@RequestBody OllamaEmbeddingsRequestDto body);

    @Headers("Content-Type: application/json")
    @GetMapping("/api/version")
    OllamaVersionDto getVersion();

    // Adicional: Listar modelos em execução (se a API do Ollama realmente tiver esse endpoint)
    @Headers("Content-Type: application/json")
    @GetMapping("/api/status") // Ou outro endpoint específico para listar modelos rodando
    OllamaRunningModelsDto listRunningModels();
     */
}
