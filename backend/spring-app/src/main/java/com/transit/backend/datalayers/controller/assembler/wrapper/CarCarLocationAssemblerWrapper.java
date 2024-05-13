package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.CarCarLocationController;
import com.transit.backend.datalayers.controller.CarController;
import com.transit.backend.datalayers.controller.assembler.CarLocationAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubExtraAbstract;
import com.transit.backend.datalayers.controller.dto.CarDTO;
import com.transit.backend.datalayers.controller.dto.LocationDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

;

@Component
public class CarCarLocationAssemblerWrapper extends AssemblerWrapperSubExtraAbstract<Location, LocationDTO, Car, CarDTO, CarController, CarCarLocationController> {
	
	@Autowired
	CarLocationAssembler carLocationAssembler;
	
	public LocationDTO toModel(Location entity, UUID carId, boolean backwardLink) {
		return super.toModel(entity, carId, backwardLink);
	}
	
	@Override
	public RepresentationModelAssemblerSupport<Location, LocationDTO> getAssemblerSupport() {
		return this.carLocationAssembler;
	}
	
	public LocationDTO addLinks(LocationDTO dto, UUID carId, boolean backwardLink) {
		return super.addLinks(dto, carId, backwardLink);
	}
	
	@Override
	public Class<CarCarLocationController> getNestedControllerClazz() {
		return CarCarLocationController.class;
	}
	
	@Override
	public Class<CarController> getRootControllerClazz() {
		return CarController.class;
	}
	
	@Override
	public Class<Car> getDomainNameClazz() {
		return Car.class;
	}
	
	public CollectionModel<LocationDTO> toCollectionModel(Iterable<? extends Location> entities, UUID carId, boolean backwardLink) {
		return super.toCollectionModel(entities, carId, backwardLink);
	}
}
