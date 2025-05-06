package com.seekernaut.seekernaut.api.ollama.controller;


import com.seekernaut.seekernaut.api.ollama.dto.ModelListDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateRequestDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateResponseDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaModelInfoDTO;
import com.seekernaut.seekernaut.api.ollamastreaming.dto.OllamaChatRequestDto;
import com.seekernaut.seekernaut.api.ollamastreaming.dto.OllamaChatResponseDto;
import com.seekernaut.seekernaut.components.Messages;
import com.seekernaut.seekernaut.domain.conversations.model.Conversation;
import com.seekernaut.seekernaut.domain.messages.model.Message;
import com.seekernaut.seekernaut.domain.ollama.service.OllamaChatServiceStreaming;
import com.seekernaut.seekernaut.domain.ollama.service.OllamaService;
import com.seekernaut.seekernaut.domain.user.model.Usuario;
import com.seekernaut.seekernaut.domain.user.service.UsuarioService;
import com.seekernaut.seekernaut.response.DefaultPaginationResponse;
import com.seekernaut.seekernaut.response.DefaultRequestParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RestController
public class OllamaApiImpl implements OllamaApi {

    private final OllamaService ollamaService;
    private final UsuarioService usuarioService;
    private final OllamaChatServiceStreaming ollamaChatServiceStreaming;

    @Override
    public Mono<ModelListDto> listModels() {
        return ollamaService.listModels();
    }

    @Override
    public Flux<OllamaGenerateResponseDto> generateCompletion(OllamaGenerateRequestDto body) {
        return ollamaService.generateCompletion(body);
    }

    @Override
    public Mono<OllamaChatResponseDto> startChat(OllamaChatRequestDto requestDto) {
        Usuario usuario = usuarioService.obterUsuarioLogado().block();
        return ollamaChatServiceStreaming.startNewChatReativo(requestDto, usuario);
    }

    public Flux<OllamaChatResponseDto> chat(UUID conversationId, OllamaChatRequestDto request) {
        return ollamaChatServiceStreaming.chat(conversationId, request);
    }

    @Override
    public Flux<Message> chatHistory(UUID conversationId) {
        return ollamaChatServiceStreaming.chatHistory(conversationId);
    }

    @Override
    public Mono<DefaultPaginationResponse<Conversation>> conversationList(DefaultRequestParams requestParams, Long userId) {
        return ollamaChatServiceStreaming.findAllReactive(requestParams,userId);
    }

}

