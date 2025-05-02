package com.seekernaut.seekernaut.domain.user.model;


import com.seekernaut.seekernaut.config.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "tipo_usuario")
@NoArgsConstructor
public class TipoUsuario {

    @Id
    @Column("id")
    private Long id;

    @Column("tipo_usuario")
    private String tipoUsuario;
}
