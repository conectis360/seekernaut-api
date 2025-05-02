package com.seekernaut.seekernaut.domain.user.service;

import com.seekernaut.seekernaut.api.usuario.dto.UsuarioDTO;
import com.seekernaut.seekernaut.api.usuario.dto.UsuarioFilterDto;
import com.seekernaut.seekernaut.api.usuario.mapper.UsuarioMapper;
import com.seekernaut.seekernaut.components.Messages;
import com.seekernaut.seekernaut.domain.user.model.TipoUsuario;
import com.seekernaut.seekernaut.domain.user.model.Usuario;
import com.seekernaut.seekernaut.domain.user.repository.RoleRepository;
import com.seekernaut.seekernaut.domain.user.repository.TipoUsuarioRepository;
import com.seekernaut.seekernaut.domain.user.repository.UsuarioRepository;
import com.seekernaut.seekernaut.exception.BusinessException;
import com.seekernaut.seekernaut.response.DefaultPaginationResponse;
import com.seekernaut.seekernaut.response.DefaultRequestParams;
import com.seekernaut.seekernaut.security.SecurityUtils;
import com.seekernaut.seekernaut.utils.PageRequestHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final PasswordEncoder encoder;
    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final PageRequestHelper pageRequestHelper;
    private final RoleRepository rolesRepository;
    private final UsuarioMapper usuarioMapper;
    private final Messages messages;


    public Mono<DefaultPaginationResponse<UsuarioDTO>> findAllReactive(DefaultRequestParams request, UsuarioFilterDto usuarioFilterDto) {
        log.debug("into findAllReactive method");

        int pageNumber = request.getPageNumber();
        int pageSize = request.getPageSize();
        long offset = (long) pageNumber * pageSize;

        Flux<Usuario> usuariosFlux = usuarioRepository.findAllWithFiltersAndPagination(
                usuarioFilterDto.getUsuario(),
                usuarioFilterDto.getEmail(),
                pageSize,
                offset
        );

        Mono<Long> totalRecordsMono = usuarioRepository.countAllWithFilters(
                usuarioFilterDto.getUsuario(),
                usuarioFilterDto.getEmail()
        );

        return Mono.zip(usuariosFlux.map(usuarioMapper::toDto).collectList(), totalRecordsMono)
                .map(tuple -> {
                    return DefaultPaginationResponse.<UsuarioDTO>builder()
                            .pageNumber(pageNumber)
                            .totalPages((int) Math.ceil((double) tuple.getT2() / pageSize))
                            .totalRecords(tuple.getT2())
                            .pageSize(tuple.getT1().size())
                            .records(tuple.getT1())
                            .build();
                });
    }

    public Mono<Usuario> retornarUsuarioLogadoReactive(String username) {
        log.debug("into retornaUsuarioRegistrado method");
        return usuarioRepository.findByUsuario(username)
                .switchIfEmpty(Mono.error(new BusinessException(messages.get("usuario.nao-encontrado"))));
    }

    private Mono<Void> verificaUsuarioRegistradoReactive(Usuario usuario) {
        log.debug("into verificaUsuarioRegistrado method");
        return usuarioRepository.existsByUsuario(usuario.getUsuario())
                .flatMap(existsUsuario -> {
                    if (existsUsuario) {
                        return Mono.error(new BusinessException(messages.get("usuario.usuario-ja-cadastrado")));
                    }
                    return usuarioRepository.existsByEmail(usuario.getEmail())
                            .flatMap(existsEmail -> {
                                if (existsEmail) {
                                    return Mono.error(new BusinessException(messages.get("usuario.email-ja-cadastrado")));
                                }
                                return Mono.empty();
                            });
                });
    }

    public Mono<Usuario> registrarUsuarioReactive(Usuario usuario) {
        log.debug("into registrarUsuario method");
        return verificaUsuarioRegistradoReactive(usuario)
                .then(Mono.fromCallable(() -> new Usuario(usuario.getUsuario(), usuario.getEmail(), encoder.encode(usuario.getSenha()))))
                .flatMap(usuarioRepository::save);
    }

    public Mono<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsuario(username);
    }

    public Mono<List<String>> buscarRolesPorUsername(String username) {
        log.debug("into buscarRolesPorUsername method");
        return usuarioRepository.findByUsuario(username)
                .flatMapMany(usuario -> {
                    return rolesRepository.findByUserId(usuario.getId());
                })
                .flatMap(role -> {
                    return tipoUsuarioRepository.findById(role.getRoleId());
                })
                .filter(tipoUsuario -> tipoUsuario != null && tipoUsuario.getTipoUsuario() != null)
                .map(TipoUsuario::getTipoUsuario)
                .collectList();
    }

    public Mono<Usuario> obterUsuarioLogado() {
        log.debug("into obterUsuarioLogado method");
        return Mono.justOrEmpty(SecurityUtils.getCurrentUserLogin()) // Converte Optional<String> para Mono<String>
                .flatMap(this::retornarUsuarioLogadoReactive)
                .cast(Usuario.class) // Garante o tipo Usuario
                .switchIfEmpty(Mono.error(new BusinessException(messages.get("usuario.nao-encontrado"))));
    }

}
