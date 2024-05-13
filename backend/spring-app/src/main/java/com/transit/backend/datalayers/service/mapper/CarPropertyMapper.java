package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.CarPropertyDTO;
import com.transit.backend.datalayers.domain.CarProperty;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarPropertyMapper extends AbstractMapper<CarProperty, CarPropertyDTO> {
	
	CarProperty toEntity(CarPropertyDTO dto);
	
	CarPropertyDTO toDto(CarProperty entity);
	
}