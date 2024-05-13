package com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses;

import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerExtend;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTOIdCanBeNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public abstract class AssemblerWrapperSubAbstract<test, testDTO extends AbstractDTOIdCanBeNull<testDTO>, testRoot, testRootDTO, rootController extends CrudControllerExtend<testRootDTO, UUID>, subController extends CrudControllerSubResource<testDTO, UUID, UUID>> {
	
	
	public testDTO toModel(test entity, UUID id, boolean backwardLink) {
		var dto = getAssemblerSupport().toModel(entity);
		return addLinks(dto, id, backwardLink);
	}
	
	public abstract RepresentationModelAssemblerSupport<test, testDTO> getAssemblerSupport();
	
	public testDTO addLinks(testDTO dto, UUID id, boolean backwardLink) {
		var dtoLink = dto.add(linkTo(methodOn(getNestedControllerClazz()).readOne(id, dto.getId())).withSelfRel());
		if (backwardLink) {
			dtoLink.add(linkTo(methodOn(getRootControllerClazz()).readOne(id)).withRel(getDomainNameClazz().getSimpleName().toLowerCase()));
		}
		return dtoLink;
	}
	
	public abstract Class<subController> getNestedControllerClazz();
	
	public abstract Class<rootController> getRootControllerClazz();
	
	public abstract Class<testRoot> getDomainNameClazz();
	
	public CollectionModel<testDTO> toCollectionModel(Iterable<? extends test> entities, UUID id, boolean backwardLink) {
		return CollectionModel.of(getAssemblerSupport()
				.toCollectionModel(entities).getContent()
				.stream()
				.map(testDTO -> addLinks(testDTO, id, backwardLink)).collect(Collectors.toList()), linkTo(getNestedControllerClazz(), id).withSelfRel());
	}
}
