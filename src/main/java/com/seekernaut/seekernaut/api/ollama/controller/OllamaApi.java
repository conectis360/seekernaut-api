package com.seekernaut.seekernaut.api.ollama.controller;

import com.seekernaut.seekernaut.api.ollama.dto.ModelListDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateRequestDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Status")
@RequestMapping("/v1/ollama")
public interface OllamaApi {

    @Operation(summary = "Inserir novo usuario", description = "Inserir novo usuario")
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping(value = "/findModels", produces = {MediaType.APPLICATION_JSON_VALUE})
    ModelListDto listModels();

    @Operation(summary = "Generate Completion", description = "Send a prompt to the specified model and return the answer ")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/generate-completion", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    OllamaGenerateResponseDto generateCompletion(@RequestBody @Validated OllamaGenerateRequestDto body);

}