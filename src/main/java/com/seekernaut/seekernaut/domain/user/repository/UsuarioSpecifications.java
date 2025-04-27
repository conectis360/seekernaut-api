package com.seekernaut.seekernaut.domain.user.repository;

import com.seekernaut.seekernaut.api.usuario.dto.UsuarioFilterDto;
import com.seekernaut.seekernaut.domain.user.model.Usuario;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UsuarioSpecifications { // Criei uma classe para a Specification

    public static Specification<Usuario> usuarioFilter(UsuarioFilterDto usuarioFilterDto) {
        return (Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            query.distinct(true);

            // Adicione aqui seus predicados de filtro, por exemplo:
            // predicates.add(builder.equal(root.get("nome"), "João"));
            // predicates.add(builder.like(root.get("email"), "%@exemplo.com%"));

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}