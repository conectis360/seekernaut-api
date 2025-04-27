package com.seekernaut.seekernaut.api.auth.controller;

import com.seekernaut.seekernaut.api.auth.dto.LoginDTO;
import com.seekernaut.seekernaut.domain.auth.service.AuthService;
import com.seekernaut.seekernaut.response.JwtResponse;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/autenticacao")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> fazLogin(@Valid @RequestBody LoginDTO loginDTO) {
        JwtResponse token = authService.fazLogin(loginDTO);
        return ResponseEntity.ok(token);
    }
}