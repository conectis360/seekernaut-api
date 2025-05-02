package com.seekernaut.seekernaut.domain.messages.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seekernaut.seekernaut.domain.conversations.model.Conversation;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <p>Entidade R2DBC que representa uma mensagem na conversa.</p>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "messages")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @Column("message_id") // Nome da coluna no banco
    @EqualsAndHashCode.Include
    private Long messageId;

    @Column("sender_type")
    private String senderType;

    @Column("content")
    private String content;

    @Column("sent_at")
    private OffsetDateTime sentAt = OffsetDateTime.now();

    @Column("conversation_id")
    private UUID conversationId; // Referencia para a Conversation usando o ID

    // Em R2DBC, relacionamentos ManyToOne/OneToMany geralmente são gerenciados por ID
    // Se você precisar da entidade Conversation diretamente, precisará fazer um join na sua consulta reativa.
    // A anotação @Transient indica que este campo não será mapeado diretamente para uma coluna.
    @Transient
    @JsonIgnoreProperties("messages") // Evita loops de serialização, ajuste conforme necessário
    private transient Conversation conversation;
}