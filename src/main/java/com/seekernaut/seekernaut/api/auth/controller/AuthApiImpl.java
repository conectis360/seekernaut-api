package com.seekernaut.seekernaut.api.auth.controller;


import com.seekernaut.seekernaut.api.auth.dto.LoginDTO;
import com.seekernaut.seekernaut.domain.auth.service.AuthService;
import com.seekernaut.seekernaut.response.JwtResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
public class AuthApiImpl implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<?> login(LoginDTO loginDTO) {
        JwtResponse token = authService.fazLogin(loginDTO);
        return ResponseEntity.ok(token);
    }


}
