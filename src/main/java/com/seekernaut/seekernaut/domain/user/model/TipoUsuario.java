package com.seekernaut.seekernaut.domain.user.model;


import com.seekernaut.seekernaut.config.Role;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@NoArgsConstructor
public class TipoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoUsuario")
    private Role tipoUsuario;
}
