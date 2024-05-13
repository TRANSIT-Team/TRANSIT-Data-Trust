package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.LocationDTO;
import com.transit.backend.datalayers.domain.Location;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface LocationMapper extends AbstractMapper<Location, LocationDTO> {
	
	Location toEntity(LocationDTO dto);
	
	LocationDTO toDto(Location entity);
	
}