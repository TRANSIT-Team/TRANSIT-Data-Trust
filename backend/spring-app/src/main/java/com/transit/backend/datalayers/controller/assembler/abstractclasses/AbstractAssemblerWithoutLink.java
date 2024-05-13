package com.transit.backend.datalayers.controller.assembler.abstractclasses;

import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.dto.abstractclasses.BaseTypeDTO;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public abstract class AbstractAssemblerWithoutLink<test extends AbstractEntity, testDTO extends BaseTypeDTO<testDTO>, controller extends CrudControllerSubResource<testDTO, UUID, UUID>> extends RepresentationModelAssemblerSupport<test, testDTO> {
	
	public AbstractAssemblerWithoutLink(Class<?> controllerClass, Class<testDTO> resourceType) {
		super(controllerClass, resourceType);
	}
	
	@Override
	public testDTO toModel(test entity) {
		return getMapper().toDto(entity);
	}
	
	public abstract AbstractMapper<test, testDTO> getMapper();
}
