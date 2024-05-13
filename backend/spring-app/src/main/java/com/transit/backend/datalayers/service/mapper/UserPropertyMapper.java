package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.UserPropertyDTO;
import com.transit.backend.datalayers.domain.UserProperty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserPropertyMapper extends AbstractMapper<UserProperty, UserPropertyDTO> {
	@Mapping(target = "user", ignore = true)
	UserProperty toEntity(UserPropertyDTO dto);
	
	UserPropertyDTO toDto(UserProperty entity);
	
}