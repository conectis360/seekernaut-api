package com.seekernaut.seekernaut.api.ollama.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class OllamaGenerateResponseDto {
    private String model;
    private OffsetDateTime created_at;
    private String response;
    private String done; // Pode ser "true" ou "false" como String

    // Construtor padrão necessário para desserialização
    public OllamaGenerateResponseDto() {
    }
}