package com.seekernaut.seekernaut.api.auth.controller;

import com.seekernaut.seekernaut.api.auth.dto.LoginDTO;
import com.seekernaut.seekernaut.api.auth.dto.RefreshTokenRequest;
import com.seekernaut.seekernaut.api.usuario.dto.UsuarioDTO;
import com.seekernaut.seekernaut.api.usuario.dto.UsuarioFilterDto;
import com.seekernaut.seekernaut.response.DefaultPaginationResponse;
import com.seekernaut.seekernaut.response.DefaultRequestParams;
import com.seekernaut.seekernaut.response.JwtResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Authentication")
@RequestMapping("/v1/auth")
public interface AuthApi {

    @Operation(summary = "Fazer login", description = "Realiza login de um usuário")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    Mono<ResponseEntity<?>> login(@RequestBody @Validated LoginDTO loginDTO);

    @Operation(summary = "Obter novo token de acesso", description = "Obter um novo token de acesso usando um refresh token válido")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/refresh", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    Mono<ResponseEntity<?>> refreshToken(@RequestBody @Validated RefreshTokenRequest request);

    @Operation(summary = "Fazer logout", description = "Faz logout da aplicação")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/logout", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    Mono<Void> logout(@RequestBody @Validated RefreshTokenRequest request);
}
