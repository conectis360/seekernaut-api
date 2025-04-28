package com.seekernaut.seekernaut.api.auth.controller;

import com.seekernaut.seekernaut.api.auth.dto.LoginDTO;
import com.seekernaut.seekernaut.api.usuario.dto.UsuarioDTO;
import com.seekernaut.seekernaut.api.usuario.dto.UsuarioFilterDto;
import com.seekernaut.seekernaut.response.DefaultPaginationResponse;
import com.seekernaut.seekernaut.response.DefaultRequestParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Status")
@RequestMapping("/v1/auth")
public interface AuthApi {

    @Operation(summary = "Inserir novo usuario", description = "Inserir novo usuario")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<?> login(@RequestBody @Validated LoginDTO loginDTO);

}
