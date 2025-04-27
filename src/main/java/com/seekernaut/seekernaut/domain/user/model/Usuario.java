package com.seekernaut.seekernaut.domain.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "usuario"),
            @UniqueConstraint(columnNames = "email")
		})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "nome")
    private String nome;

    @Column(name = "foto", columnDefinition = "bytea") // Nova coluna para a foto (bytea no banco de dados)
    private byte[] foto; // Armazena a foto como um array de bytes

    @JsonIgnore
    @Column(name = "senha")
    private String senha;

    @Column(name = "usuario")
    private String usuario;

    private Boolean accountNonLocked = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<TipoUsuario> tipoUsuario = new HashSet<>();

    public Usuario(String usuario, String email, String senha) {
        this.usuario = usuario;
        this.email = email;
        this.senha = senha;
    }
}
