package com.seekernaut.seekernaut.domain.user.repository;


import com.seekernaut.seekernaut.domain.user.model.Roles;
import feign.Param;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RoleRepository extends R2dbcRepository<Roles, Long> {

    Mono<Roles> findByUserIdAndRoleId(Long userId, Long roleId);

    Flux<Roles> findByUserId(Long userId);

    Flux<Roles> findByRoleId(Long roleId);

}
