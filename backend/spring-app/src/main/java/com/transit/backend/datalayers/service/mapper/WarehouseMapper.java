package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.WarehouseDTO;
import com.transit.backend.datalayers.domain.Warehouse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WarehouseMapper extends AbstractMapper<Warehouse, WarehouseDTO> {
	
	Warehouse toEntity(WarehouseDTO dto);
	
	WarehouseDTO toDto(Warehouse entity);
	
}