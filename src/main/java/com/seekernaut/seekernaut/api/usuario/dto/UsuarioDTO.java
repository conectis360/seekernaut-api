package com.seekernaut.seekernaut.api.usuario.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UsuarioDTO {
    private Long codigoUsuario;

    @NotBlank
    @Size(min = 3, max = 20)
    private String usuario;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String senha;
}