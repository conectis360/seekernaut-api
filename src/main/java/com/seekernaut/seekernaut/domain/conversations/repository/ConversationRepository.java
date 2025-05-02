package com.seekernaut.seekernaut.domain.conversations.repository;


import com.seekernaut.seekernaut.domain.conversations.model.Conversation;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ConversationRepository extends R2dbcRepository<Conversation, UUID> {
    Mono<Conversation> findByConversationId(UUID conversationId);

    @Query("SELECT c.*, u.* FROM conversations c INNER JOIN usuario u ON c.user_id = u.id WHERE c.conversation_id = :conversationId")
    Mono<Conversation> findByConversationIdWithUser(UUID conversationId);

    Flux<Conversation> findAll();
}
