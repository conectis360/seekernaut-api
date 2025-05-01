package com.seekernaut.seekernaut.domain.ollama.service;

import com.seekernaut.seekernaut.api.ollamastreaming.dto.OllamaChatRequestDto;
import com.seekernaut.seekernaut.api.ollamastreaming.dto.OllamaChatResponseDto;
import com.seekernaut.seekernaut.client.ollama.webclient.OllamaChatApiClient;
import com.seekernaut.seekernaut.domain.conversations.model.Conversation;
import com.seekernaut.seekernaut.domain.conversations.repository.ConversationRepository;
import com.seekernaut.seekernaut.domain.messages.model.Message;
import com.seekernaut.seekernaut.domain.messages.repository.MessageRepository;
import com.seekernaut.seekernaut.domain.user.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>Serviço responsável pela comunicação com a API de chat do Ollama.</p>
 * <p>Esta classe utiliza o {@link WebClient} para interagir de forma não bloqueante
 * com o endpoint `/chat` da API do Ollama, suportando a recepção de respostas em stream.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaChatServiceStreaming {

    private final OllamaChatApiClient ollamaChatApiClient;
    private final TitleGeneratorService titleGeneratorService;
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public Mono<OllamaChatResponseDto> startNewChat(OllamaChatRequestDto ollamaChatRequestDto, Usuario usuario) {
        // 1. Cria e salva a nova conversa
        return Mono.fromCallable(() -> {
            Conversation newConversation = Conversation.builder()
                    .user(usuario)
                    .startedAt(OffsetDateTime.now())
                    .build();
            return conversationRepository.save(newConversation);
        })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(savedConversation -> {
                    UUID conversationId = savedConversation.getConversationId();

                    // 2. Persiste a mensagem inicial do usuário
                    Message userMessage = Message.builder()
                            .conversation(savedConversation)
                            .senderType("user")
                            .content(ollamaChatRequestDto.getMessages().get(0).getContent()) // Assume que a primeira mensagem é do usuário
                            .sentAt(OffsetDateTime.now())
                            .build();
                    return Mono.fromCallable(() -> messageRepository.save(userMessage))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(savedUserMessage -> {
                                // 3. Envia a mensagem inicial para o Ollama e obtém a primeira resposta (em stream)
                                return ollamaChatApiClient.chatStream(ollamaChatRequestDto)
                                        .next() // Pega a primeira resposta do stream
                                        .flatMap(firstOllamaResponse -> {
                                            // 4. Persiste a primeira resposta do Ollama
                                            Message ollamaResponse = Message.builder()
                                                    .conversation(savedConversation)
                                                    .senderType(firstOllamaResponse.getMessage().getRole())
                                                    .content(firstOllamaResponse.getMessage().getContent())
                                                    .sentAt(OffsetDateTime.now())
                                                    .build();
                                            return Mono.fromCallable(() -> messageRepository.save(ollamaResponse))
                                                    .subscribeOn(Schedulers.boundedElastic())
                                                    .map(savedOllamaResponse -> {
                                                        // 5. Gera e atualiza o título da conversa (opcional, em background)
                                                        String generatedTitle = titleGeneratorService.generateTitle(ollamaChatRequestDto.getMessages().get(0).getContent());
                                                        savedConversation.setTitle(generatedTitle);
                                                        Mono.fromCallable(() -> conversationRepository.save(savedConversation))
                                                                .subscribeOn(Schedulers.boundedElastic())
                                                                .subscribe(); // Inicia a atualização do título em background

                                                        // 6. Retorna a primeira resposta do Ollama com o ID da conversa
                                                        firstOllamaResponse.setConversationId(conversationId);
                                                        return firstOllamaResponse;
                                                    });
                                        });
                            });
                });
    }

    /**
     * Utilizado para armazenar temporariamente a parte de uma linha JSON incompleta
     * que pode ser recebida em um {@link DataBuffer} e completada no próximo.
     * Garante que tentaremos desserializar apenas linhas JSON completas.
     */
    private final AtomicReference<String> incompleteLine = new AtomicReference<>("");

    /**
     * <p>Persiste a mensagem do usuário no banco de dados.</p>
     *
     * @param conversationId {@link UUID} identificador da conversa.
     * @param content        O conteúdo da mensagem do usuário.
     * @return {@link Mono} de {@link Message} representando a mensagem persistida.
     */
    private Mono<Message> persistUserMessage(UUID conversationId, String content) {
        return Mono.fromCallable(() -> {
            Message userMessage = Message.builder()
                    .senderType("user")
                    .content(content)
                    .sentAt(OffsetDateTime.now())
                    .build();
            return messageRepository.save(userMessage);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * <p>Persiste a resposta completa do modelo no banco de dados.</p>
     *
     * @param conversationId {@link UUID} identificador da conversa.
     * @param fullResponse   O conteúdo completo da resposta do modelo.
     * @return {@link Mono} de {@link Message} representando a mensagem persistida.
     */
    private Mono<Message> persistModelResponse(UUID conversationId, String fullResponse) {
        return Mono.fromCallable(() -> {
            Message modelMessage = Message.builder()
                    .senderType("model")
                    .content(fullResponse)
                    .sentAt(OffsetDateTime.now())
                    .build();
            return messageRepository.save(modelMessage);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * <p>Envia uma requisição de chat para a API do Ollama, persiste a mensagem do usuário
     * e a resposta completa do modelo (após a conclusão da stream), retornando um fluxo
     * da resposta para o cliente.</p>
     * <p>Este método realiza uma única chamada ao Ollama e utiliza operadores reativos
     * para processar a stream e persistir os dados.</p>
     *
     * @param conversationId {@link UUID} identificador da conversa.
     * @param request        {@link OllamaChatRequestDto} contendo os detalhes da conversa e os parâmetros de geração.
     * @return {@link Flux} de {@link OllamaChatResponseDto}, representando a stream da resposta do chat do Ollama.
     * O salvamento da resposta do modelo ocorre como um efeito colateral após a conclusão do fluxo.
     */
    public Flux<OllamaChatResponseDto> getChatCompletionAndPersist(UUID conversationId, OllamaChatRequestDto request) {
        Mono<Message> savedUserMessage = Mono.justOrEmpty(request.getMessages().stream().filter(message -> "user".equals(message.getRole())).findFirst())
                .flatMap(userMessageDto -> persistUserMessage(conversationId, userMessageDto.getContent()))
                .onErrorResume(e -> {
                    log.error("Erro ao persistir mensagem do usuário: {}", e.getMessage());
                    return Mono.empty();
                });

        AtomicReference<StringBuilder> fullResponseBuilder = new AtomicReference<>(new StringBuilder());
        Flux<OllamaChatResponseDto> ollamaResponseFlux = ollamaChatApiClient.chatStream(request)
                .doOnNext(responseDto -> {
                    if (responseDto != null && responseDto.getMessage().getContent() != null) {
                        fullResponseBuilder.get().append(responseDto.getMessage().getContent());
                    }
                });

        ollamaResponseFlux.doOnComplete(() -> {
            persistModelResponse(conversationId, fullResponseBuilder.get().toString())
                    .subscribe(); // Inicia a operação de persistência da resposta do modelo
        }).subscribe(); // Inicia a subscrição do Flux do Ollama

        // Trigger the user message persistence
        savedUserMessage.subscribe();

        return ollamaResponseFlux;
    }
}
