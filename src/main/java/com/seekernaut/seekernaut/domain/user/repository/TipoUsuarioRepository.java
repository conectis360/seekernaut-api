package com.seekernaut.seekernaut.domain.user.repository;


import com.seekernaut.seekernaut.domain.user.model.TipoUsuario;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TipoUsuarioRepository extends R2dbcRepository<TipoUsuario, Long> {
    Mono<TipoUsuario> findByTipoUsuario(String name); // Alterado para String
}
