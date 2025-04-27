package com.seekernaut.seekernaut.domain.user.repository;


import com.seekernaut.seekernaut.config.Role;
import com.seekernaut.seekernaut.domain.user.model.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long>, JpaSpecificationExecutor<TipoUsuario> {
    Optional<TipoUsuario> findByTipoUsuario(Role name);
}
