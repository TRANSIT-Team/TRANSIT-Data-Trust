package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.GlobalCompanyPropertiesController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssembler;
import com.transit.backend.datalayers.controller.dto.GlobalCompanyPropertiesDTO;
import com.transit.backend.datalayers.domain.GlobalCompanyProperties;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.GlobalCompanyPropertiesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GlobalCompanyPropertiesAssembler extends AbstractAssembler<GlobalCompanyProperties, GlobalCompanyPropertiesDTO, GlobalCompanyPropertiesController> {
	@Autowired
	private GlobalCompanyPropertiesMapper globalCompanyPropertiesMapper;
	
	public GlobalCompanyPropertiesAssembler() {
		super(GlobalCompanyPropertiesController.class, GlobalCompanyPropertiesDTO.class);
	}
	
	@Override
	public AbstractMapper<GlobalCompanyProperties, GlobalCompanyPropertiesDTO> getMapper() {
		return globalCompanyPropertiesMapper;
	}
	
	@Override
	public Class<GlobalCompanyPropertiesController> getControllerClass() {
		return GlobalCompanyPropertiesController.class;
	}
}
