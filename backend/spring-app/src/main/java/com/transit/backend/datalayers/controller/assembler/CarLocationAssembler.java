package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.CarCarLocationController;
import com.transit.backend.datalayers.controller.dto.LocationDTO;
import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.datalayers.service.mapper.LocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class CarLocationAssembler extends RepresentationModelAssemblerSupport<Location, LocationDTO> {
	@Autowired
	private LocationMapper locationMapper;
	
	public CarLocationAssembler() {
		super(CarCarLocationController.class, LocationDTO.class);
	}
	
	@Override
	public LocationDTO toModel(Location entity) {
		return locationMapper.toDto(entity);
	}
}
