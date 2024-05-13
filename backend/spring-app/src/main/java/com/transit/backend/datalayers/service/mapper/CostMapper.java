package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.CostDTO;
import com.transit.backend.datalayers.controller.dto.PackageItemDTO;
import com.transit.backend.datalayers.domain.Cost;
import com.transit.backend.datalayers.domain.PackageItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CostMapper extends AbstractMapper<Cost, CostDTO> {
	
	@Mapping(target = "costProperties.cost", ignore = true)
	PackageItemDTO toDto(PackageItem entity);
	
}