package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.UserUserPropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerWithoutLink;
import com.transit.backend.datalayers.controller.dto.UserPropertyDTO;
import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.UserPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

@Component
public class UserPropertyAssembler extends AbstractAssemblerWithoutLink<UserProperty, UserPropertyDTO, UserUserPropertyController> {
	@Autowired
	private UserPropertyMapper userPropertyMapper;
	
	
	public UserPropertyAssembler() {
		super(UserUserPropertyController.class, UserPropertyDTO.class);
	}
	
	@Override
	public UserPropertyDTO toModel(UserProperty entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<UserProperty, UserPropertyDTO> getMapper() {
		return this.userPropertyMapper;
	}
	
	@Override
	public CollectionModel<UserPropertyDTO> toCollectionModel(Iterable<? extends UserProperty> entities) {
		return super.toCollectionModel(entities);
	}
	
	@Override
	public Class<UserUserPropertyController> getControllerClass() {
		return null;
	}
	
	
}