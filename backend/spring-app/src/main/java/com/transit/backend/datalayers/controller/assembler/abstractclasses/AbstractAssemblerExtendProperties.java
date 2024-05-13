package com.transit.backend.datalayers.controller.assembler.abstractclasses;

import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerExtend;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTOIdCanBeNull;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractPropertiesParentDTO;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractPropertyEntity;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public abstract class AbstractAssemblerExtendProperties<test extends AbstractParentEntity<property>, testDTO extends AbstractPropertiesParentDTO<testDTO, propertyDTO>, property extends AbstractPropertyEntity<test>, propertyDTO extends AbstractDTOIdCanBeNull<propertyDTO>, controller extends CrudControllerSubResource<propertyDTO, UUID, UUID>, rootController extends CrudControllerExtend<testDTO, UUID>> extends AbstractAssembler<test, testDTO, rootController> {
	public AbstractAssemblerExtendProperties(Class<?> controllerClass, Class<testDTO> resourceType) {
		super(controllerClass, resourceType);
	}
	
	public testDTO toModel(test entity) {
		testDTO dto = super.toModel(entity);
		dto.setProperties(toPropertyDTO(entity.getId(), entity.getProperties()));
		return dto;
	}
	
	public abstract AbstractMapper<test, testDTO> getMapper();
	
	@Override
	public CollectionModel<testDTO> toCollectionModel(Iterable<? extends test> entities) {
		return super.toCollectionModel(entities).add(linkTo(getControllerClass()).withSelfRel());
	}
	
	public abstract Class<rootController> getControllerClass();
	
	private SortedSet<propertyDTO> toPropertyDTO(UUID rootId, SortedSet<property> properties) {
		if (properties == null || properties.isEmpty()) {
			return new TreeSet<>();
		} else {
			return new TreeSet<>(getSubAssemblerWrapper().toCollectionModel(properties, rootId, false).getContent());
		}
	}
	
	public abstract AssemblerWrapperSubAbstract<property, propertyDTO, test, testDTO, rootController, controller> getSubAssemblerWrapper();
	
	
}
