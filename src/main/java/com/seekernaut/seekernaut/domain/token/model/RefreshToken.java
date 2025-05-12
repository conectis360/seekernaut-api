package com.seekernaut.seekernaut.domain.token.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

/**
 * <p>Entidade R2DBC que representa token refresh.</p>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "refresh_token")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @Column("id")
    @EqualsAndHashCode.Include
    private Long tokenId;

    @Column("userId")
    private String userId;

    @Column("token")
    private String token;

    @Column("expiryDate")
    private Timestamp expiryDate;
}