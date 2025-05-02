package com.seekernaut.seekernaut.api.ollamastreaming.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * <p>DTO para a requisição ao endpoint de chat do Ollama (`/chat`).</p>
 * <p>Contém a lista de mensagens da conversa e parâmetros de geração.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OllamaChatRequestDto {

    /**
     * <p>O nome do modelo a ser usado para gerar a resposta do chat.</p>
     * <p>Obrigatório.</p>
     */
    @NotEmpty(message = "O modelo não pode estar vazio.")
    private String model;

    /**
     * <p>A lista de mensagens que compõem o histórico da conversa.</p>
     * <p>Cada mensagem é um objeto {@link MessageDto}.</p>
     * <p>Obrigatório e não pode estar vazio.</p>
     */
    @NotEmpty(message = "A lista de mensagens não pode estar vazia.")
    @Valid
    private Mono<List<MessageDto>> messages;

    /**
     * <p>Indica se a resposta deve ser enviada em stream (múltiplos eventos).</p>
     * <p>Embora o endpoint `/chat` possa não suportar streaming agora, incluímos para futura compatibilidade.</p>
     */
    private Boolean stream;

    /**
     * <p>Parâmetros de geração adicionais para o modelo.</p>
     * <p>Opcional. Consulte a documentação da API do Ollama para as opções disponíveis.</p>
     */
    private Map<String, Object> options;

    /**
     * <p>Formato da resposta a ser retornado.</p>
     * <p>Opcional. Consulte a documentação da API do Ollama para os formatos disponíveis.</p>
     */
    private String format;
}
