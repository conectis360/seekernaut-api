package com.seekernaut.seekernaut.domain.auth.service;

import com.seekernaut.seekernaut.api.auth.dto.LoginDTO;
import com.seekernaut.seekernaut.components.Messages;
import com.seekernaut.seekernaut.domain.user.model.User;
import com.seekernaut.seekernaut.domain.user.service.UsuarioService;
import com.seekernaut.seekernaut.exception.BusinessException;
import com.seekernaut.seekernaut.response.JwtResponse;
import com.seekernaut.seekernaut.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final ReactiveAuthenticationManager authenticationManager; // Use ReactiveAuthenticationManager
    private final UsuarioService usuarioServiceReactive;
    private final JwtUtils jwtUtils;
    private final Messages messages;

    private Mono<Authentication> autenticaUsuario(String username, String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))
                .onErrorResume(e -> {
                    if (e instanceof BadCredentialsException) {
                        return Mono.error(new BusinessException(messages.get("login.usuario-senha-invalido")));
                    }
                    return Mono.error(e);
                });
    }

    public Mono<JwtResponse> fazLoginReativo(LoginDTO loginDTO) {
        Mono<Authentication> authenticationMono = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );

        return authenticationMono.flatMap(autenticado -> {
            SecurityContextHolder.getContext().setAuthentication(autenticado);
            String jwt = jwtUtils.generateJwtToken(autenticado);
            String refreshToken = jwtUtils.generateRefreshToken(autenticado);
            User userDetails = (User) autenticado.getPrincipal();

            // Busca as roles do usuário de forma reativa usando o serviço de usuários
            return usuarioServiceReactive.buscarRolesPorUsername(userDetails.getUsername())
                    .map(roles -> {
                        JwtResponse token = JwtResponse.builder()
                                .token(jwt)
                                .id(userDetails.getId())
                                .username(userDetails.getUsername())
                                .email(userDetails.getEmail())
                                .roles(roles)
                                .nome(userDetails.getNome())
                                .refreshToken(refreshToken)
                                .build();
                        return token;
                    });
        });
    }
}