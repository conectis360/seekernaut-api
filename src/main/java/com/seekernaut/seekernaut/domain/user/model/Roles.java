package com.seekernaut.seekernaut.domain.user.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "user_roles")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Roles implements Serializable {

    @Id
    @Transient // @EmbeddedId não tem um equivalente direto como @Id composto em R2DBC
    private RolesPK id;

    @Column("user_id")
    private Long userId;

    @Column("role_id")
    private Long roleId;

    @Transient
    @ToString.Exclude
    private transient Usuario usuario; // Referência transient para Usuario, busque via join se necessário
}