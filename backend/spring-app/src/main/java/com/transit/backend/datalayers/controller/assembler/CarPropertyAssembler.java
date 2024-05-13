package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.CarCarPropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerWithoutLink;
import com.transit.backend.datalayers.controller.dto.CarPropertyDTO;
import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CarPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarPropertyAssembler extends AbstractAssemblerWithoutLink<CarProperty, CarPropertyDTO, CarCarPropertyController> {
	@Autowired
	private CarPropertyMapper carPropertyMapper;
	
	public CarPropertyAssembler() {
		super(CarCarPropertyController.class, CarPropertyDTO.class);
	}
	
	@Override
	public AbstractMapper<CarProperty, CarPropertyDTO> getMapper() {
		return carPropertyMapper;
	}
}
