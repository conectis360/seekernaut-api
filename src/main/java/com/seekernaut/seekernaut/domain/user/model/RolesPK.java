package com.seekernaut.seekernaut.domain.user.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RolesPK implements Serializable {
    @Column("user_id")
    private Long userId;
    @Column("role_id")
    private Long roleId;
}
