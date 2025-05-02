package com.seekernaut.seekernaut.domain.messages.repository;


import com.seekernaut.seekernaut.domain.messages.model.Message;
import feign.Param;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MessageRepository extends R2dbcRepository<Message, Long> {
    Flux<Message> findByConversationId(UUID conversationId);

    Mono<Message> findById(Long messageId);

        @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY sent_at ASC")
    Flux<Message> findByConversationIdOrderBySentAt(@Param("conversationId") UUID conversationId);
}
