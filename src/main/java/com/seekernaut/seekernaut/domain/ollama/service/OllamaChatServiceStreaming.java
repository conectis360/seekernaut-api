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

    /**
     * Inicia uma nova conversa, persistindo a conversa, a mensagem inicial do usuário,
     * enviando a mensagem inicial para o Ollama, persistindo a primeira resposta do Ollama
     * e retornando essa primeira resposta com o ID da conversa.
     *
     * @param ollamaChatRequestDto A requisição de chat inicial do usuário.
     * @param usuario              O usuário que iniciou a conversa.
     * @return Um Mono de OllamaChatResponseDto contendo a primeira resposta do Ollama
     * e o ID da conversa.
     */
    public Mono<OllamaChatResponseDto> startNewChatReativo(OllamaChatRequestDto ollamaChatRequestDto, Usuario usuario) {
        // 1. Cria e salva a nova conversa de forma reativa.
        Mono<Conversation> newConversationMono = Mono.just(Conversation.builder()
                .user(usuario)
                .startedAt(OffsetDateTime.now())
                .build())
                .flatMap(conversationRepository::save)
                .subscribeOn(Schedulers.boundedElastic()); // Salva em thread separado não bloqueante.

        // 2. Persiste a mensagem inicial do usuário de forma reativa, dependendo da conversa salva
        //    e acessando o conteúdo da mensagem do DTO dentro do Mono<List<MessageDto>>.
        Mono<Message> userMessageMono = newConversationMono.flatMap(savedConversation ->
                ollamaChatRequestDto.getMessages()
                        .flatMap(messagesDtoList -> {
                            if (!messagesDtoList.isEmpty()) {
                                MessageDto firstMessageDto = messagesDtoList.get(0);
                                return Mono.just(Message.builder()
                                        .conversation(savedConversation)
                                        .senderType("user")
                                        .content(firstMessageDto.getContent())
                                        .sentAt(OffsetDateTime.now())
                                        .build())
                                        .flatMap(messageRepository::save)
                                        .subscribeOn(Schedulers.boundedElastic()); // Salva em thread separado não bloqueante.
                            } else {
                                return Mono.empty(); // Se a lista de mensagens estiver vazia, emite um Mono vazio.
                            }
                        })
        );

// 3. Envia a mensagem inicial para o Ollama e obtém a primeira resposta (em stream),
        //    dependendo da conversa salva e da mensagem do usuário persistida.
        Mono<OllamaChatResponseDto> firstOllamaResponseMono = Mono.zip(newConversationMono, userMessageMono)
                .flatMap(tuple -> {
                    Conversation savedConversation = tuple.getT1();
                    return ollamaChatApiClient.chatStream(ollamaChatRequestDto)
                            .next() // Pega a primeira resposta do stream.
                            .flatMap(firstResponse -> {
                                // 4. Persiste a primeira resposta do Ollama de forma reativa.
                                return Mono.just(Message.builder()
                                        .conversation(savedConversation)
                                        .senderType(firstResponse.getMessage().getRole())
                                        .content(firstResponse.getMessage().getContent())
                                        .sentAt(OffsetDateTime.now())
                                        .build())
                                        .flatMap(messageRepository::save)
                                        .subscribeOn(Schedulers.boundedElastic()) // Salva em thread separado não bloqueante.
                                        .map(savedOllamaResponse -> {
                                            // 5. Gera e atualiza o título da conversa (opcional, em background).
                                            ollamaChatRequestDto.getMessages()
                                                    .map(messagesDtoList -> !messagesDtoList.isEmpty() ? messagesDtoList.get(0).getContent() : "")
                                                    .flatMap(titleGeneratorService::generateTitleReativo)
                                                    .flatMap(generatedTitle -> {
                                                        savedConversation.setTitle(generatedTitle);
                                                        return Mono.fromCallable(() -> conversationRepository.save(savedConversation))
                                                                .subscribeOn(Schedulers.boundedElastic());
                                                    })
                                                    .subscribe(); // Inicia a atualização do título em background.

                                            // 6. Retorna a primeira resposta do Ollama com o ID da conversa.
                                            firstResponse.setConversationId(savedConversation.getConversationId());
                                            return firstResponse;
                                        });
                            });
                });

        return firstOllamaResponseMono;
    }

    /**
     * Inicia ou continua uma conversa, persistindo a mensagem do usuário,
     * obtendo o histórico da conversa e interagindo com o cliente Ollama para obter
     * uma resposta em stream, persistindo também a resposta do modelo.
     *
     * @param conversationId O ID da conversa.
     * @param request        A requisição de chat do usuário contendo a nova mensagem (em um Mono de lista).
     * @return Um Flux de OllamaChatResponseDto representando a stream de resposta do Ollama.
     */
    public Flux<OllamaChatResponseDto> chat(UUID conversationId, OllamaChatRequestDto request) {
        // 1. Recupera o histórico completo da conversa do banco de dados (reativo)
        Flux<Message> conversationHistoryFlux = messageRepository.findByConversationIdOrderBySentAt(conversationId);

        // 2. Extrai e persiste a nova mensagem do usuário (não bloqueante com flatMap)
        Mono<Message> newUserMessageMono = request.getMessages()
                .flatMap(messagesDtoList -> {
                    if (!messagesDtoList.isEmpty()) {
                        MessageDto firstMessageDto = messagesDtoList.get(0);
                        return Mono.just(Message.builder()
                                .conversation(Conversation.builder().conversationId(conversationId).build())
                                .senderType("user")
                                .content(firstMessageDto.getContent())
                                .sentAt(OffsetDateTime.now())
                                .build())
                                .flatMap(messageRepository::save)
                                .subscribeOn(Schedulers.boundedElastic()); // Persiste em thread separado não bloqueante.
                    } else {
                        return Mono.empty(); // Se a lista de mensagens estiver vazia, emite um Mono vazio.
                    }
                })
                .onErrorResume(e -> {
                    log.error("Erro ao persistir nova mensagem do usuário: {}", e.getMessage());
                    return Mono.empty(); // Em caso de erro na persistência, emite um Mono vazio.
                });

        // Bloco para esperar a persistência da mensagem do usuário antes de prosseguir
        Message newUserMessage = newUserMessageMono.block();
        if (newUserMessage == null) {
            return Flux.error(new RuntimeException("Falha ao persistir a mensagem do usuário."));
        }

        // 3. Combina o histórico com a nova mensagem do usuário para enviar ao Ollama (reativo)
        Mono<List<MessageDto>> ollamaMessagesMono = conversationHistoryFlux
                .map(message -> MessageDto.builder()
                        .role(message.getSenderType())
                        .content(message.getContent())
                        .build())
                .collectList() // Coleta todas as mensagens históricas em uma lista (Mono<List<MessageDto>>)
                .map(historyMessages -> {
                    // Adiciona a nova mensagem do usuário à lista
                    historyMessages.add(MessageDto.builder()
                            .role(newUserMessage.getSenderType())
                            .content(newUserMessage.getContent())
                            .build());
                    return historyMessages;
                });

        // 4. Cria um novo OllamaChatRequestDto com o histórico completo
        Mono<OllamaChatRequestDto> ollamaRequestMono = Mono.just(OllamaChatRequestDto.builder()
                .model(request.getModel())
                .messages(ollamaMessagesMono)
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

    public Flux<Message> chatHistory(UUID conversationId) {
        return messageRepository.findByConversationIdOrderBySentAt(conversationId);
    }
}
