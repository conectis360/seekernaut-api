package com.seekernaut.seekernaut.api.ollama.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
public class OllamaModelInfoDTO {
    private String name;
    private OffsetDateTime modified_at;
    private Long size;
    private String digest;
    private Details details;

    @Data
    public static class Details {
        private String format;
        private String family;
        private String[] families;
        private String parameter_size;
        private String quantization_level;
        private Map<String, Object> additionalProperties; // Para quaisquer outras propriedades em "details"

        // Construtor padrão necessário para desserialização
        public Details() {
        }
    }

    // Construtor padrão necessário para desserialização
    public OllamaModelInfoDTO() {
    }
}