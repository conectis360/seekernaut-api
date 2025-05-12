package com.seekernaut.seekernaut.domain.token.repository;


import com.seekernaut.seekernaut.domain.token.model.RefreshToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface RefreshTokenRepository extends R2dbcRepository<RefreshToken, Long> {

}
