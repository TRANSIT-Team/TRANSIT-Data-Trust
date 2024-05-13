package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.UserDTO;
import com.transit.backend.datalayers.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper extends AbstractMapper<User, UserDTO> {
	@Mapping(target = "company.id", ignore = true)
	User toEntity(UserDTO dto);
	
	@Mapping(target = "userProperties.user", ignore = true)
	UserDTO toDto(User entity);
	
}