package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.CarDTO;
import com.transit.backend.datalayers.domain.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarMapper extends AbstractMapper<Car, CarDTO> {
	@Mapping(target = "locations", ignore = true)
	@Mapping(target = "orderLegs", ignore = true)
	Car toEntity(CarDTO dto);
	
	@Mapping(target = "carProperties.car", ignore = true)
	CarDTO toDto(Car entity);
	
}