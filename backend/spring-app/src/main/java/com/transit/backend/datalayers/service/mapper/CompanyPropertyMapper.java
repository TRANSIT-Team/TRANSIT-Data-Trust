package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.domain.CompanyProperty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CompanyPropertyMapper extends AbstractMapper<CompanyProperty, CompanyPropertyDTO> {
	@Mapping(target = "company", ignore = true)
	CompanyProperty toEntity(CompanyPropertyDTO dto);
	
	CompanyPropertyDTO toDto(CompanyProperty entity);
	
}