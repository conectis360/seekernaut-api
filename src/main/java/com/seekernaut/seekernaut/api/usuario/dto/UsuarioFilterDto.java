package com.seekernaut.seekernaut.api.usuario.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UsuarioFilterDto {

    private Long codigoUsuario;
    private Long codigoTipoUsuario;

}
