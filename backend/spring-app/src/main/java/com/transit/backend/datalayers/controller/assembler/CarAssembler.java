package com.transit.backend.datalayers.controller.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.datalayers.controller.CarCarLocationController;
import com.transit.backend.datalayers.controller.CarCarPropertyController;
import com.transit.backend.datalayers.controller.CarController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerExtendProperties;
import com.transit.backend.datalayers.controller.assembler.wrapper.CarCarLocationAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.CarCarPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.CarDTO;
import com.transit.backend.datalayers.controller.dto.CarPropertyDTO;
import com.transit.backend.datalayers.controller.dto.LocationDTO;
import com.transit.backend.datalayers.controller.dto.OrderLegDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.datalayers.domain.OrderLeg;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CarMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import static com.transit.backend.config.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CarAssembler extends AbstractAssemblerExtendProperties<Car, CarDTO, CarProperty, CarPropertyDTO, CarCarPropertyController, CarController> {
	
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private CarMapper carMapper;
	
	@Autowired
	private CarCarPropertyAssemblerWrapper carCarPropertyAssemblerWrapper;
	
	@Autowired
	private CarCarLocationAssemblerWrapper carCarLocationAssemblerWrapper;
	
	public CarAssembler() {
		super(CarController.class, CarDTO.class);
	}
	
	@Override
	public CarDTO toModel(Car car) {
		
		var dto = super.toModel(car);
		dto.setLocations(toLocationsDTO(car.getId(), car.getLocations()));
		dto.setOrderLegs(toOrderLegDTO(car.getId(), car.getOrderLegs()));
		dto.add(linkTo(methodOn(CarCarPropertyController.class).read(dto.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, FILTER_ALL_OWN_AND_SHARED)).withRel("carProperties"));
		dto.add(linkTo(methodOn(CarCarLocationController.class).read(dto.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, FILTER_ALL_OWN_AND_SHARED)).withRel("carLocations"));
		return dto;
	}
	
	@Override
	public AbstractMapper<Car, CarDTO> getMapper() {
		return this.carMapper;
	}
	
	@Override
	public CollectionModel<CarDTO> toCollectionModel(Iterable<? extends Car> entities) {
		return super.toCollectionModel(entities);
		
	}
	
	@Override
	public Class<CarController> getControllerClass() {
		return CarController.class;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<CarProperty, CarPropertyDTO, Car, CarDTO, CarController, CarCarPropertyController> getSubAssemblerWrapper() {
		return this.carCarPropertyAssemblerWrapper;
	}
	
	private SortedSet<LocationDTO> toLocationsDTO(UUID carId, SortedSet<Location> carLocations) {
		if (carLocations == null || carLocations.isEmpty()) {
			return new TreeSet<>();
		} else {
			return new TreeSet<>(carCarLocationAssemblerWrapper.toCollectionModel(carLocations, carId, false).getContent());
		}
	}
	
	private SortedSet<OrderLegDTO> toOrderLegDTO(UUID carId, SortedSet<OrderLeg> orderLegs) {
		if (orderLegs == null || orderLegs.isEmpty()) {
			return new TreeSet<>();
		} else {
			throw new NotImplementedException();
			//return new TreeSet<>(...AssemblerWrapper.toCollectionModel(orderLegs, carId, false).getContent());
		}
	}
	
}
