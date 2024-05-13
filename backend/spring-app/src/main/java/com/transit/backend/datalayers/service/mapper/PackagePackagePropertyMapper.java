package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.PackagePackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PackagePackagePropertyMapper extends AbstractMapper<PackagePackageProperty, PackagePackagePropertyDTO> {
	
	@Mapping(target = "packageItem", ignore = true)
	PackagePackageProperty toEntity(PackagePackagePropertyDTO dto);
	
	
	PackagePackagePropertyDTO toDto(PackagePackageProperty entity);
	
}