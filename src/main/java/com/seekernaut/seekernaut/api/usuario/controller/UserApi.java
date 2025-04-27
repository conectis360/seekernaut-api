package com.seekernaut.seekernaut.api.usuario.controller;

import com.seekernaut.seekernaut.api.usuario.dto.UsuarioDTO;
import com.seekernaut.seekernaut.api.usuario.dto.UsuarioFilterDto;
import com.seekernaut.seekernaut.response.DefaultPaginationResponse;
import com.seekernaut.seekernaut.response.DefaultRequestParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Status")
@RequestMapping("/v1/user")
public interface UserApi {

    @Operation(summary = "Listar Status", description = "Listar todos Status")
    @PreAuthorize("@securityService.hasPermission('usuarioApi_findAll')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    DefaultPaginationResponse<UsuarioDTO> findAll(@ParameterObject DefaultRequestParams requestParams,
                                                  @ParameterObject UsuarioFilterDto filter);

    @Operation(summary = "Listar usuario por id", description = "Listar usuario por id")
    @PreAuthorize("@securityService.hasPermission('usuarioApi_findById')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{usuarioId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    UsuarioDTO findById(@PathVariable Long usuarioId);

    @Operation(summary = "Inserir novo usuario", description = "Inserir novo usuario")
    @PreAuthorize("@securityService.hasPermission('usuarioApi_insert')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    UsuarioDTO insert(@RequestBody @Validated UsuarioDTO usuarioDTO);

    @Operation(summary = "Atualizar usuario", description = "Atualizar usuario")
    @PreAuthorize("@securityService.hasPermission('usuarioApi_update')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{usuarioId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    UsuarioDTO update(@PathVariable Long usuarioId, @RequestBody @Validated UsuarioDTO usuarioDTO);

    @Operation(summary = "Deletar usuario", description = "Deletar usuario por id")
    @PreAuthorize("@securityService.hasPermission('usuarioApi_delete')")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{codigoUsuario}")
    void delete(@PathVariable Long codigoUsuario);

}
