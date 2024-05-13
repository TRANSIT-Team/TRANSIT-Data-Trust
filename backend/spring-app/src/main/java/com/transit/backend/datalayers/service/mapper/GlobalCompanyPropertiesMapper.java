package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.GlobalCompanyPropertiesDTO;
import com.transit.backend.datalayers.domain.GlobalCompanyProperties;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GlobalCompanyPropertiesMapper extends AbstractMapper<GlobalCompanyProperties, GlobalCompanyPropertiesDTO> {
	
	
	GlobalCompanyProperties toEntity(GlobalCompanyPropertiesDTO dto);
	
	GlobalCompanyPropertiesDTO toDto(GlobalCompanyProperties entity);
	
	
}
