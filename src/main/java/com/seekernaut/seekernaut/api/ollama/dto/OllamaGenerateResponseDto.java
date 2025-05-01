package com.seekernaut.seekernaut.api.ollama.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OllamaGenerateResponseDto {
    private String model;
    private OffsetDateTime created_at;
    private String response;
    private String done; // Pode ser "true" ou "false" como String
}