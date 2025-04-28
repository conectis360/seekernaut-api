package com.seekernaut.seekernaut.api.ollama.controller;

import com.seekernaut.seekernaut.api.ollama.dto.ModelListDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaModelInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Tag(name = "Status")
@RequestMapping("/v1/ollama")
public interface OllamaApi {

    @Operation(summary = "Inserir novo usuario", description = "Inserir novo usuario")
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping(value = "/findModels", produces = {MediaType.APPLICATION_JSON_VALUE})
    ModelListDto listModels();

}
