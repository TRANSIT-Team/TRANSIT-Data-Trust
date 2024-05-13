package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.LocationController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssembler;
import com.transit.backend.datalayers.controller.dto.LocationDTO;
import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.LocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

@Component
public class LocationAssembler extends AbstractAssembler<Location, LocationDTO, LocationController> {
	@Autowired
	private LocationMapper locationMapper;
	
	public LocationAssembler() {
		super(LocationController.class, LocationDTO.class);
	}
	
	@Override
	public LocationDTO toModel(Location entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<Location, LocationDTO> getMapper() {
		return this.locationMapper;
	}
	
	@Override
	public CollectionModel<LocationDTO> toCollectionModel(Iterable<? extends Location> entities) {
		return super.toCollectionModel(entities);
	}
	
	@Override
	public Class<LocationController> getControllerClass() {
		return LocationController.class;
	}
	
	
}