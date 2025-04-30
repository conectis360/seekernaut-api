package com.seekernaut.seekernaut.api.ollamastreaming.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>DTO para representar uma única mensagem na conversa do chat.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    /**
     * <p>O papel do remetente da mensagem (`user`, `assistant`, `system`).</p>
     * <p>Obrigatório e não pode estar vazio.</p>
     */
    @NotEmpty(message = "O papel da mensagem não pode estar vazio.")
    private String role;

    /**
     * <p>O conteúdo da mensagem.</p>
     * <p>Obrigatório e não pode estar vazio.</p>
     */
    @NotEmpty(message = "O conteúdo da mensagem não pode estar vazio.")
    private String content;
}
