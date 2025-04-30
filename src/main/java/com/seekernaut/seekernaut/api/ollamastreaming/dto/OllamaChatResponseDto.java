package com.seekernaut.seekernaut.api.ollamastreaming.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * <p>DTO para cada evento da stream de resposta (ou a resposta única) do endpoint de chat do Ollama (`/chat`).</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OllamaChatResponseDto {

    /**
     * <p>O nome do modelo que gerou esta resposta.</p>
     */
    private String model;

    /**
     * <p>A data e hora de criação desta resposta.</p>
     */
    @JsonProperty("created_at")
    private String createdAt;

    /**
     * <p>O conteúdo da resposta do modelo para este evento (ou a resposta completa se não for streaming).</p>
     */
    private String response;

    /**
     * <p>Indica se esta é a última parte da resposta (fim da stream).</p>
     */
    private Boolean done;

    /**
     * <p>Informações adicionais sobre a geração da resposta (opcional).</p>
     */
    private Map<String, Object> context;

    /**
     * <p>Estatísticas sobre a geração da resposta (opcional).</p>
     */
    @JsonProperty("total_duration")
    private Long totalDuration;

    /**
     * <p>Duração do carregamento do modelo (opcional).</p>
     */
    @JsonProperty("load_duration")
    private Long loadDuration;

    /**
     * <p>Número de tokens processados (opcional).</p>
     */
    @JsonProperty("prompt_eval_count")
    private Integer promptEvalCount;

    /**
     * <p>Duração da avaliação do prompt (opcional).</p>
     */
    @JsonProperty("prompt_eval_duration")
    private Long promptEvalDuration;

    /**
     * <p>Número de tokens gerados (opcional).</p>
     */
    @JsonProperty("eval_count")
    private Integer evalCount;

    /**
     * <p>Duração da geração da resposta (opcional).</p>
     */
    @JsonProperty("eval_duration")
    private Long evalDuration;
}