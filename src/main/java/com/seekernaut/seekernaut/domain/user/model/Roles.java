package com.seekernaut.seekernaut.domain.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(schema = "public", name = "user_roles")
public class Roles implements Serializable {

    @EmbeddedId
    private RolesPK id;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private Usuario usuario;

    @Column(name = "role_id", updatable = false, insertable = false)
    private Long roleId;
}
