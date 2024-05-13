package com.transit.backend.datalayers.controller.assembler.abstractclasses;

import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerNested;
import com.transit.backend.datalayers.controller.dto.abstractclasses.BaseTypeDTO;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public abstract class AbstractAssembler<test extends AbstractEntity, testDTO extends BaseTypeDTO<testDTO>, controller extends CrudControllerNested<testDTO, UUID>> extends RepresentationModelAssemblerSupport<test, testDTO> {
	public AbstractAssembler(Class<?> controllerClass, Class<testDTO> resourceType) {
		super(controllerClass, resourceType);
	}
	
	public testDTO toModel(test entity) {
		return getMapper()
				.toDto(entity)
				.add(linkTo(methodOn(getControllerClass()).readOne(entity.getId())).withSelfRel());
	}
	
	public abstract AbstractMapper<test, testDTO> getMapper();
	
	@Override
	public CollectionModel<testDTO> toCollectionModel(Iterable<? extends test> entities) {
		return super.toCollectionModel(entities).add(linkTo(getControllerClass()).withSelfRel());
	}
	
	public abstract Class<controller> getControllerClass();
	
	
}

