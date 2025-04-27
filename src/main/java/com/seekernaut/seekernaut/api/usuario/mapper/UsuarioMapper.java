package com.seekernaut.seekernaut.api.usuario.mapper;

import com.seekernaut.seekernaut.api.usuario.dto.UsuarioDTO;
import com.seekernaut.seekernaut.domain.user.model.Usuario;
import com.seekernaut.seekernaut.mapper.BeanMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = BeanMapper.SPRING)
public interface UsuarioMapper extends BeanMapper<Usuario, UsuarioDTO> {

    @Override
    @InheritInverseConfiguration
    Usuario toEntity(UsuarioDTO usuarioDTO);

    @Override
    @Mapping(source = "id", target = "codigoUsuario")
    UsuarioDTO toDto(Usuario usuario);

}
