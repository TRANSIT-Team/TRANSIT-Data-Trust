package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.PackagePackageClassDTO;
import com.transit.backend.datalayers.domain.PackageClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PackagePackageClassMapper extends AbstractMapper<PackagePackageClassDTO, PackageClass> {
	
	@Mapping(target = "packageItems", ignore = true)
	PackageClass toEntity(PackagePackageClassDTO dto);
	
	
	PackagePackageClassDTO toDto(PackageClass entity);
	
}