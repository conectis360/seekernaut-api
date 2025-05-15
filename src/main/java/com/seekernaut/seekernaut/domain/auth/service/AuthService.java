package com.seekernaut.seekernaut.domain.auth.service;

import com.nimbusds.openid.connect.sdk.LogoutRequest;
import com.seekernaut.seekernaut.api.auth.dto.LoginDTO;
import com.seekernaut.seekernaut.components.Messages;
import com.seekernaut.seekernaut.domain.token.model.RefreshToken;
import com.seekernaut.seekernaut.domain.token.service.RefreshTokenService;
import com.seekernaut.seekernaut.domain.user.model.User;
import com.seekernaut.seekernaut.domain.user.service.UsuarioService;
import com.seekernaut.seekernaut.exception.BusinessException;
import com.seekernaut.seekernaut.response.JwtResponse;
import com.seekernaut.seekernaut.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
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
    private final RefreshTokenService refreshTokenService;

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
                                .accessToken(jwt)
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

    public Mono<?> gerarNovoAccessToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .flatMap(token -> {
                    if (token.getExpiryDate().isBefore(Instant.now())) {
                        return refreshTokenService.deleteByToken(refreshToken)
                                .then(Mono.error(new RuntimeException("Refresh token expirado")));
                    }

                    return usuarioServiceReactive.findById(token.getUserId())
                            .flatMap(user -> usuarioServiceReactive.buscarRolesPorUsername(user.getUsuario())
                                    .map(roles -> {
                                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                                .map(SimpleGrantedAuthority::new)
                                                .collect(Collectors.toList());

                                        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                                        String newAccessToken = jwtUtils.generateJwtToken(authentication);
                                        String newRefreshToken = jwtUtils.generateRefreshToken(authentication);

                                        // Invalida o refresh token antigo e salva o novo
                                        return refreshTokenService.deleteByToken(refreshToken)
                                                .then(refreshTokenService.saveRefreshToken(RefreshToken.builder()
                                                        .userId(user.getId())
                                                        .token(newRefreshToken)
                                                        .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                                                        .build()))
                                                .thenReturn(JwtResponse.builder()
                                                        .accessToken(newAccessToken)
                                                        .refreshToken(newRefreshToken)
                                                        .id(user.getId())
                                                        .username(user.getUsuario())
                                                        .email(user.getEmail())
                                                        .roles(roles) // Mantemos a lista de strings no DTO para a resposta
                                                        .nome(user.getNome())
                                                        .build());
                                    }));
                });
    }

    public Mono<Void> logout(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .flatMap(token -> {
                    if (token.getExpiryDate().isBefore(Instant.now())) {
                        // Token já expirado - deleta e retorna sucesso
                        return refreshTokenService.deleteByToken(refreshToken);
                    }
                    // Token válido - invalida
                    return refreshTokenService.deleteByToken(refreshToken);
                })
                .then()
                .onErrorResume(e -> {
                    // Log de erro opcional
                    return Mono.empty();
                });
    }
}