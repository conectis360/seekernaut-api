package com.seekernaut.seekernaut.mapper;

public interface BeanMapper<Entity, Dto> {

    public static final String SPRING = "Spring";

    Dto toDto(Entity entity);

    Entity toEntity(Dto dto);

}
