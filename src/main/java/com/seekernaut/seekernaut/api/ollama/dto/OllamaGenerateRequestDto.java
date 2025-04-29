package com.seekernaut.seekernaut.api.ollama.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OllamaGenerateRequestDto {
    private String prompt;
    private String model;
    private Boolean stream;
    private Map<String, Object> format; // Pode ser String ou Map, dependendo da necessidade
    private Integer seed;
    private Integer threads;
    private Boolean raw;
    private Boolean keep_alive;
    private List<String> stop;
    private Integer tfs_z;
    private Double typical_p;
    private Double repeat_penalty;
    private Integer repeat_last_n;
    private Double temperature;
    private Double top_p;
    private Integer min_p;
    private Integer mirostat;
    private Double mirostat_tau;
    private Double mirostat_eta;
    private Integer penalize_newline;
    private String logprobs;
    private Map<String, Object> extra; // Para quaisquer outros parâmetros não mapeados explicitamente

    // Construtor padrão necessário para desserialização
    public OllamaGenerateRequestDto() {
        this.stream = false; // Define um valor padrão sensato para stream
    }
}