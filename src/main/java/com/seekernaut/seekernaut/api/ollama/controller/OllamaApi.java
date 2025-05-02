package com.seekernaut.seekernaut.api.ollama.controller;

import com.seekernaut.seekernaut.api.ollama.dto.ModelListDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateRequestDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateResponseDto;
import com.seekernaut.seekernaut.api.ollamastreaming.dto.ConversationStartResponse;
import com.seekernaut.seekernaut.api.ollamastreaming.dto.OllamaChatRequestDto;
import com.seekernaut.seekernaut.api.ollamastreaming.dto.OllamaChatResponseDto;
import com.seekernaut.seekernaut.domain.messages.model.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name = "Status")
@RequestMapping("/v1/ollama")
public interface OllamaApi {

    @Operation(summary = "Find models", description = "Find existing models")
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping(value = "/findModels", produces = {MediaType.APPLICATION_JSON_VALUE})
    ModelListDto listModels();

    @Operation(summary = "Generate Completion", description = "Send a prompt to the specified model and return the answer ")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/generate-completion", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    Flux<OllamaGenerateResponseDto> generateCompletion(@RequestBody @Validated OllamaGenerateRequestDto body);

    @Operation(summary = "Start New Chat", description = "Initiates a new chat conversation by receiving the first user message and returning the first response and conversation ID.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/conversations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<OllamaChatResponseDto> startChat(@RequestBody @Validated OllamaChatRequestDto request);

    @Operation(summary = "Chat in Conversation", description = "Send a message to an existing conversation.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/conversations/{conversationId}/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Flux<OllamaChatResponseDto> chat(@PathVariable UUID conversationId, @RequestBody @Validated OllamaChatRequestDto request);

    @Operation(summary = "Get Chat history", description = "Get all messages by conversationId")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('TEST')")
    @GetMapping(value = "/conversations/{conversationId}/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    Flux<Message> chatHistory(@PathVariable UUID conversationId);
}