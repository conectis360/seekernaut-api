package com.seekernaut.seekernaut.api.auth.controller;


import com.seekernaut.seekernaut.api.auth.dto.LoginDTO;
import com.seekernaut.seekernaut.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@RestController
public class AuthApiImpl implements AuthApi {

    private final AuthService authService;

    @Override
    public Mono<ResponseEntity<?>> login(LoginDTO loginDTO) {
        return authService.fazLoginReativo(loginDTO)
                .map(token -> ResponseEntity.ok(token));
    }


}
