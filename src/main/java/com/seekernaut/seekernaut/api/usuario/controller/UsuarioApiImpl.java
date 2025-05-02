package com.seekernaut.seekernaut.api.usuario.controller;


import com.seekernaut.seekernaut.api.usuario.dto.UsuarioDTO;
import com.seekernaut.seekernaut.api.usuario.dto.UsuarioFilterDto;
import com.seekernaut.seekernaut.api.usuario.mapper.UsuarioMapper;
import com.seekernaut.seekernaut.domain.user.model.Usuario;
import com.seekernaut.seekernaut.domain.user.service.UsuarioService;
import com.seekernaut.seekernaut.response.DefaultPaginationResponse;
import com.seekernaut.seekernaut.response.DefaultRequestParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@RestController
public class UsuarioApiImpl implements UserApi {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @Override
    public Mono<DefaultPaginationResponse<UsuarioDTO>> findAll(DefaultRequestParams requestParams, UsuarioFilterDto filter) {
        return usuarioService.findAllReactive(requestParams, filter);
    }

    @Override
    public UsuarioDTO findById(Long statusId) {
        log.debug("into findById method");
        return null;
    }

    public Mono<UsuarioDTO> insert(UsuarioDTO usuarioDTO) {
        Mono<Usuario> usuarioMono = usuarioService.registrarUsuarioReactive(usuarioMapper.toEntity(usuarioDTO));
        return usuarioMono.map(usuarioMapper::toDto);
    }

    @Override
    public UsuarioDTO update(Long usuarioId, UsuarioDTO usuarioDTO) {
        return null;
    }

    @Override
    public void delete(Long statusId) {
        log.debug("into delete method");
    }

}
