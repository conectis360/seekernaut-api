package com.seekernaut.seekernaut.domain.user.repository;


import com.seekernaut.seekernaut.config.Role;
import com.seekernaut.seekernaut.domain.user.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long>, JpaSpecificationExecutor<Usuario> {
    Optional<Usuario> findByUsuario(String username);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.tipoUsuario WHERE u.usuario = :username")
    Optional<Usuario> findByUsuarioWithTipoUsuario(String username);

    Boolean existsByUsuario(String username);

    Boolean existsByEmail(String email);

    UserDetails findByEmail(String email);

    List<Usuario> findByTipoUsuarioTipoUsuario(Role role);


}
