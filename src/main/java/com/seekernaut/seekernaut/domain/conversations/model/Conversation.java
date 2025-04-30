package com.seekernaut.seekernaut.domain.conversations.model;

import com.seekernaut.seekernaut.domain.user.model.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <p>Entidade JPA que representa uma conversa.</p>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@NoArgsConstructor
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "conversation_id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID conversationId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "started_at")
    private OffsetDateTime startedAt = OffsetDateTime.now();

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Usuario user;

    public Conversation(Integer userId, String title) {
        this.userId = userId;
        this.title = title;
    }

    @Builder
    public Conversation(UUID conversationId, Integer userId, OffsetDateTime startedAt, String title, Usuario user) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.startedAt = startedAt;
        this.title = title;
        this.user = user;
    }
}
