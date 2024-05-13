package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.CarController;
import com.transit.backend.datalayers.controller.CarPropertyController;
import com.transit.backend.datalayers.controller.assembler.CarPropertyAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperNestedAbstract;
import com.transit.backend.datalayers.controller.dto.CarDTO;
import com.transit.backend.datalayers.controller.dto.CarPropertyDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.CarProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class CarPropertyAssemblerWrapper extends AssemblerWrapperNestedAbstract<CarProperty, CarPropertyDTO, Car, CarDTO, CarController, CarPropertyController> {
	
	@Autowired
	CarPropertyAssembler carPropertyAssembler;
	
	@Override
	public RepresentationModelAssemblerSupport<CarProperty, CarPropertyDTO> getAssemblerSupport() {
		return this.carPropertyAssembler;
	}
	
	@Override
	public Class<CarPropertyController> getNestedControllerClazz() {
		return CarPropertyController.class;
	}
	
	@Override
	public Class<CarController> getRootControllerClazz() {
		return CarController.class;
	}
	
	@Override
	public Class<Car> getDomainNameClazz() {
		return Car.class;
	}
}
