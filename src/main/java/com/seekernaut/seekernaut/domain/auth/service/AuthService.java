package com.seekernaut.seekernaut.domain.auth.service;

import com.seekernaut.seekernaut.api.auth.dto.LoginDTO;
import com.seekernaut.seekernaut.components.Messages;
import com.seekernaut.seekernaut.domain.user.model.User;
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

    public Mono<JwtResponse> fazLogin(LoginDTO loginDTO) {
        return this.autenticaUsuario(loginDTO.getUsername(), loginDTO.getPassword())
                .flatMap(autenticado -> {
                    SecurityContextHolder.getContext().setAuthentication(autenticado);
                    String jwt = jwtUtils.generateJwtToken(autenticado);

                    User userDetails = (User) autenticado.getPrincipal(); // Assumindo que seu principal é Usuario
                    List<String> roles = userDetails.getAuthorities().stream()
                            .map(item -> item.getAuthority())
                            .collect(Collectors.toList());

                    JwtResponse token = new JwtResponse(
                            jwt,
                            // Adapte para pegar o ID do Usuario se necessário (pode não estar no UserDetails padrão)
                            null, // Substitua pelo ID real
                            userDetails.getUsername(),
                            // Adapte para pegar o email do Usuario se necessário
                            null, // Substitua pelo email real
                            roles,
                            // Adapte para pegar o nome do Usuario se necessário
                            null // Substitua pelo nome real
                    );
                    return Mono.just(token);
                });
    }
}