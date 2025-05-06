package com.seekernaut.seekernaut.domain.conversations.repository;


import com.seekernaut.seekernaut.domain.conversations.model.Conversation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConversationRepository extends R2dbcRepository<Conversation, UUID> {
    Mono<Conversation> findByConversationId(UUID conversationId);

    @Query("SELECT c.*, u.* FROM conversations c INNER JOIN usuario u ON c.user_id = u.id WHERE c.conversation_id = :conversationId")
    Mono<Conversation> findByConversationIdWithUser(UUID conversationId);

    Flux<Conversation> findAll();

    Flux<Conversation> findByUserId(Long userId);

    @Query("SELECT * FROM conversations " +
            "WHERE (:userId IS NULL OR user_id = :userId) " +
            "ORDER BY started_at DESC " + // Ordenando por data de início (mais recente primeiro) para facilitar a obtenção das mais recentes
            "LIMIT :pageSize OFFSET :offset")
    Flux<Conversation> findAllWithFiltersAndPagination(Long userId, int pageSize, long offset);

    @Query("SELECT COUNT(*) FROM conversations " +
            "WHERE (:userId IS NULL OR user_id = :userId)")
    Mono<Long> countAllWithFilters(Long userId);

    // Método específico para buscar as N conversas mais recentes de um usuário
    @Query("SELECT * FROM conversations " +
            "WHERE user_id = :userId " +
            "ORDER BY started_at DESC " +
            "LIMIT :limit")
    Flux<Conversation> findTopNByUserIdOrderByStartedAtDesc(Long userId, int limit);

}
