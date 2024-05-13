package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.WarehousePropertyDTO;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WarehousePropertyMapper extends AbstractMapper<WarehouseProperty, WarehousePropertyDTO> {
	
	WarehouseProperty toEntity(WarehousePropertyDTO dto);
	
	WarehousePropertyDTO toDto(WarehouseProperty entity);
	
}