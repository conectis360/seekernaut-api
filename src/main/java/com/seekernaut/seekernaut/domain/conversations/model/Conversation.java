package com.seekernaut.seekernaut.domain.conversations.model;

import com.seekernaut.seekernaut.domain.user.model.Usuario;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <p>Entidade R2DBC que representa uma conversa.</p>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "conversations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @Column("conversation_id")
    @EqualsAndHashCode.Include
    private UUID conversationId;

    @Column("started_at")
    private OffsetDateTime startedAt = OffsetDateTime.now();

    @Column("title")
    private String title;

    @Column("user_id")
    private Long userId; // Referencia para o Usuario usando o ID

    // Em R2DBC, relacionamentos ManyToOne/OneToMany geralmente são gerenciados por ID
    // Se você precisar da entidade Usuario diretamente, precisará fazer um join na sua consulta reativa.
    @Transient
    private transient Usuario user;
}