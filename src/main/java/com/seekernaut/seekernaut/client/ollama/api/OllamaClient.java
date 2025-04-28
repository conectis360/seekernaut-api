package com.seekernaut.seekernaut.client.ollama.api;

import com.seekernaut.seekernaut.client.ollama.config.OllamaClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "ollamaClient", configuration = OllamaClientConfig.class, url = "${application.client.ollama-api}")
public interface OllamaClient {
    /*
    @Headers("Content-Type: application/json")
    @PostMapping("/v1/historico-log-sistema")
    Long insert(@RequestBody HistoricoLogSistemaDto log);

    @Headers("Content-Type: application/json")
    @PutMapping("/v1/historico-log-sistema")
    void update(@RequestBody HistoricoLogSistemaDto log);
     */

}
