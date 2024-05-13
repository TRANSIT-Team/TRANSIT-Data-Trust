package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.PackageItemPackageClassController;
import com.transit.backend.datalayers.controller.assembler.PackagePackageClassAssembler;
import com.transit.backend.datalayers.controller.dto.PackageClassDTO;
import com.transit.backend.datalayers.domain.PackageClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PackageItemPackageClassAssemblerWrapper {
	
	@Autowired
	PackagePackageClassAssembler packagePackageClassAssembler;
	
	
	public PackageClassDTO toModel(PackageClass entity, UUID packageItemId) {
		var dto = packagePackageClassAssembler.toModel(entity);
		return addLinks(dto, packageItemId);
	}
	
	public PackageClassDTO addLinks(PackageClassDTO dto, UUID packageItemId) {
		return dto.add(linkTo(methodOn(PackageItemPackageClassController.class).readOne(packageItemId, dto.getId())).withSelfRel());
	}
	
	public CollectionModel<PackageClassDTO> toCollectionModel(Iterable<? extends PackageClass> entities, UUID packageItemId) {
		return CollectionModel.of(packagePackageClassAssembler
				.toCollectionModel(entities).getContent()
				.stream()
				.map(packageClassDTO -> addLinks(packageClassDTO, packageItemId)).collect(Collectors.toList()), linkTo(PackageItemPackageClassController.class, packageItemId).withSelfRel());
	}
}
