package com.seekernaut.seekernaut.domain.user.repository;


import com.seekernaut.seekernaut.domain.user.model.Usuario;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UsuarioRepository extends R2dbcRepository<Usuario, Long> {
    Mono<Usuario> findByUsuario(String username);
    Mono<Boolean> existsByUsuario(String username);
    Mono<Boolean> existsByEmail(String email);
    Mono<Usuario> findByEmail(String email);
    Flux<Usuario> findByTipoUsuario_TipoUsuario(String role);

    @Query("SELECT * FROM usuario " +
            "WHERE (:usuario IS NULL OR usuario = :usuario) " +
            "AND (:email IS NULL OR email = :email) " +
            "LIMIT :pageSize OFFSET :offset")
    Flux<Usuario> findAllWithFiltersAndPagination(String usuario, String email, int pageSize, long offset);

    @Query("SELECT COUNT(*) FROM usuario " +
            "WHERE (:usuario IS NULL OR usuario = :usuario) " +
            "AND (:email IS NULL OR email = :email)")
    Mono<Long> countAllWithFilters(String usuario, String email);
}
