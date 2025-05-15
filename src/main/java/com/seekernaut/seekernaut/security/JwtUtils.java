package com.seekernaut.seekernaut.security;

import com.seekernaut.seekernaut.domain.token.model.RefreshToken;
import com.seekernaut.seekernaut.domain.token.service.RefreshTokenService;
import com.seekernaut.seekernaut.domain.user.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Componente utilitário para geração e validação de tokens JWT (JSON Web Tokens) para autenticação.
 */
@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * Segredo usado para assinar e verificar a assinatura dos tokens JWT.
     * O valor é injetado da configuração da aplicação.
     */
    @Value("${seekernaut.app.jwtSecret}")
    private String jwtSecret;

    /**
     * Tempo de expiração dos tokens JWT em milissegundos.
     * O valor é injetado da configuração da aplicação.
     */
    @Value("${seekernaut.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    @Value("${seekernaut.app.jwtRefreshTokenExpirationDays}")
    private long jwtRefreshTokenExpirationDays;

    /**
     * Gera uma chave de assinatura {@link Key} a partir do {@link #jwtSecret}.
     * Utiliza o algoritmo HMAC-SHA-512 para a assinatura.
     * A chave secreta é decodificada de Base64 antes de ser utilizada.
     *
     * @return Uma instância de {@link Key} para assinar os tokens.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gera um token JWT para um usuário autenticado.
     * O token contém o nome de usuário como subject, a data de emissão e a data de expiração.
     * O token é assinado usando o algoritmo HMAC-SHA-512 e a chave secreta configurada.
     *
     * @param authentication Objeto {@link Authentication} representando a autenticação do usuário.
     * @return O token JWT gerado.
     */
    public String generateJwtToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        String refreshTokenValue = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusSeconds(jwtRefreshTokenExpirationDays * 24 * 60 * 60);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userPrincipal.getId()); // Assuming User has an getId() method
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiryDate(expiryDate);

        refreshTokenService.saveRefreshToken(refreshToken).subscribe(); // Save asynchronously

        return refreshTokenValue;
    }

    /**
     * Extrai o nome de usuário (subject) do token JWT.
     *
     * @param token O token JWT do qual extrair o nome de usuário.
     * @return O nome de usuário contido no token.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser() // Use parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valida um token JWT.
     * Verifica a assinatura, a expiração e a estrutura geral do token.
     * Registra erros específicos no log em caso de falha na validação.
     *
     * @param authToken O token JWT a ser validado.
     * @return {@code true} se o token for válido, {@code false} caso contrário.
     */
    public boolean validateJwtToken(String authToken) {
        // Verifica se o token é nulo ou vazio
        if (authToken == null || authToken.isEmpty()) {
            logger.error("Token JWT é nulo ou vazio.");
            return false;
        }

        // Divide o token em partes e verifica a estrutura
        String[] tokenParts = authToken.split("\\.");
        if (tokenParts.length != 3) {
            logger.error("Token JWT inválido. Número de partes esperado: 3, encontrado: {}", tokenParts.length);
            return false;
        }

        try {
            Jwts.parser() // Use Jwts.parser() para versões mais antigas
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Assinatura JWT inválida: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Token JWT inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT não suportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("String de claims JWT está vazia: {}", e.getMessage());
        }

        return false;
    }
}