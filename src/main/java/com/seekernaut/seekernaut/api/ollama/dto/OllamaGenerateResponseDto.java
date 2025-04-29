package com.seekernaut.seekernaut.api.ollama.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
public class OllamaGenerateResponseDto {
    private String model;
    private OffsetDateTime created_at;
    private String response;
    private String done; // Pode ser "true" ou "false" como String
    private Map<String, Double> context;
    private Map<String, Double> total_duration;
    private Map<String, Double> load_duration;
    private Map<String, Double> prompt_eval_count;
    private Map<String, Double> prompt_eval_duration;
    private Map<String, Double> eval_count;
    private Map<String, Double> eval_duration;
    private String error;
    private Map<String, Object> logprobs; // Presente apenas se logprobs > 0 na requisição
    private Map<String, Object> extra; // Para quaisquer outras propriedades não mapeadas explicitamente

    // Construtor padrão necessário para desserialização
    public OllamaGenerateResponseDto() {
    }
}