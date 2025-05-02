package com.seekernaut.seekernaut.domain.ollama.service;

import com.seekernaut.seekernaut.api.ollamastreaming.dto.MessageDto;
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
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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


    public Flux<OllamaChatResponseDto> chat(UUID conversationId, OllamaChatRequestDto request) {
        // 1. Recupera o histórico completo da conversa do banco de dados (bloqueante)
        List<Message> conversationHistory = messageRepository.findByConversation_ConversationIdOrderBySentAt(conversationId);

        // 2. Extrai e persiste a nova mensagem do usuário (bloqueante em thread separado)
        Mono<Message> newUserMessageMono = Mono.fromCallable(() -> {
            Message newUserMessage = Message.builder()
                    .conversation(Conversation.builder().conversationId(conversationId).build())
                    .senderType("user")
                    .content(request.getMessages().get(0).getContent()) // Assumindo que a primeira mensagem é a nova do usuário
                    .sentAt(OffsetDateTime.now())
                    .build();
            return messageRepository.save(newUserMessage);
        }).subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> {
                    log.error("Erro ao persistir nova mensagem do usuário: {}", e.getMessage());
                    return Mono.empty();
                });

        // Bloco para esperar a persistência da mensagem do usuário antes de prosseguir
        Message newUserMessage = newUserMessageMono.block();
        if (newUserMessage == null) {
            return Flux.error(new RuntimeException("Falha ao persistir a mensagem do usuário."));
        }

        // 3. Combina o histórico com a nova mensagem do usuário para enviar ao Ollama
        List<MessageDto> ollamaMessages = conversationHistory.stream()
                .map(message -> MessageDto.builder()
                        .role(message.getSenderType())
                        .content(message.getContent())
                        .build())
                .collect(Collectors.toList());
        ollamaMessages.add(MessageDto.builder()
                .role(newUserMessage.getSenderType())
                .content(newUserMessage.getContent())
                .build());

        // 4. Cria um novo OllamaChatRequestDto com o histórico completo
        Mono<OllamaChatRequestDto> ollamaRequestMono = Mono.just(OllamaChatRequestDto.builder()
                .model(request.getModel())
                .messages(ollamaMessages)
                .stream(request.getStream())
                .format(request.getFormat())
                .options(request.getOptions())
                .build());

        // 5. Envia a requisição para o Ollama e processa a stream da resposta
        AtomicReference<StringBuilder> fullResponseBuilder = new AtomicReference<>(new StringBuilder());
        return ollamaRequestMono.flatMapMany(ollamaChatApiClient::chatStream)
                .doOnNext(responseDto -> {
                    if (responseDto != null && responseDto.getMessage() != null && responseDto.getMessage().getContent() != null) {
                        fullResponseBuilder.get().append(responseDto.getMessage().getContent());
                        // Aqui você enviaria 'formattedResponse' para o seu frontend em tempo real
                    }
                })
                .doOnComplete(() -> {
                    Mono.fromCallable(() -> {
                        Message modelMessage = Message.builder()
                                .conversation(Conversation.builder().conversationId(conversationId).build())
                                .senderType("model")
                                .content(fullResponseBuilder.get().toString()) // Salva a concatenação bruta (formatada)
                                .sentAt(OffsetDateTime.now())
                                .build();
                        return messageRepository.save(modelMessage);
                    }).subscribeOn(Schedulers.boundedElastic()).subscribe();
                });
    }
}
