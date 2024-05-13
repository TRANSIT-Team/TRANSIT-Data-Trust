package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.PackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageProperty;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PackagePropertyMapper extends AbstractMapper<PackageProperty, PackagePropertyDTO> {
	
	
	PackageProperty toEntity(PackagePropertyDTO dto);
	
	PackagePropertyDTO toDto(PackageProperty entity);
	
}