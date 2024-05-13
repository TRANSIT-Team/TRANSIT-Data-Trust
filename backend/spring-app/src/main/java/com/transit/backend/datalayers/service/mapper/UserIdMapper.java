package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.UserIdDTO;
import com.transit.backend.transferentities.UserIdTransfer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserIdMapper extends AbstractMapper<UserIdTransfer, UserIdDTO> {
	
	UserIdTransfer toEntity(UserIdDTO dto);
	
	@Mapping(source = "userId", target = "userId")
	UserIdDTO toDto(UserIdTransfer entity);
	
}
