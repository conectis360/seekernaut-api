package com.seekernaut.seekernaut.domain.user.service;

import com.seekernaut.seekernaut.domain.user.model.User;
import com.seekernaut.seekernaut.domain.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.fromCallable(() -> usuarioRepository.findByUsuarioWithTipoUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username)))
                .map(User::build);
    }
}
