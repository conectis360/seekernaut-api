package com.seekernaut.seekernaut.domain.user.repository;


import com.seekernaut.seekernaut.domain.user.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long>, JpaSpecificationExecutor<Roles> {
    List<Roles> findByRoleId(Long roleId);
}
