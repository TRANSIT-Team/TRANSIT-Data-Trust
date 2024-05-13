package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.PackageClassDTO;
import com.transit.backend.datalayers.domain.PackageClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface PackageClassMapper extends AbstractMapper<PackageClass, PackageClassDTO> {
	
	@Mapping(target = "packageItems", ignore = true)
	PackageClass toEntity(PackageClassDTO dto);
	
	PackageClassDTO toDto(PackageClass entity);
	
}