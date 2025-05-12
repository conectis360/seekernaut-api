package com.seekernaut.seekernaut.domain.token.service;

import com.seekernaut.seekernaut.domain.token.model.RefreshToken;
import com.seekernaut.seekernaut.domain.token.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    /**
     * Gera um novo Refresh Token.
     *
     * @param userId ID do usuário para o qual o token será gerado.
     * @return Mono<RefreshToken> contendo o token gerado.
     */
    public Mono<RefreshToken> generateRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // Expira em 7 dias
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Salva um Refresh Token existente.
     *
     * @param refreshToken O Refresh Token a ser salvo.
     * @return Mono<RefreshToken> contendo o token salvo.
     */
    public Mono<RefreshToken> saveRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Busca um Refresh Token pelo seu valor.
     *
     * @param token O valor do Refresh Token a ser buscado.
     * @return Mono<RefreshToken> contendo o token encontrado (ou Mono.empty() se não encontrado).
     */
    public Mono<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token); // Assumindo que você adicionou esse método ao seu RefreshTokenRepository
    }

    /**
     * Invalida (exclui) um Refresh Token.
     *
     * @param token O valor do Refresh Token a ser invalidado.
     * @return Mono<Void> indicando a conclusão da operação.
     */
    public Mono<Void> deleteByToken(String token) {
        return refreshTokenRepository.deleteByToken(token); // Assumindo que você adicionou esse método ao seu RefreshTokenRepository
    }
}
