package com.seekernaut.seekernaut.domain.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "usuario")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @Column("id")
    private Long id;

    @Column
    private String email;

    @Column
    private String nome;

    @Column
    private byte[] foto;

    @JsonIgnore
    @Column
    private String senha;

    @Column
    private String usuario;

    private Boolean accountNonLocked = true;

    @Transient
    @ToString.Exclude
    private Set<TipoUsuario> tipoUsuario = new HashSet<>();

    public Usuario(String usuario, String email, String senha) {
        this.usuario = usuario;
        this.email = email;
        this.senha = senha;
    }
}