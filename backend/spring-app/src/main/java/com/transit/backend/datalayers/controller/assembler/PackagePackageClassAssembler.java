package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.PackageItemPackageClassController;
import com.transit.backend.datalayers.controller.dto.PackageClassDTO;
import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.datalayers.repository.PackageItemRepository;
import com.transit.backend.datalayers.service.mapper.PackageClassMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class PackagePackageClassAssembler extends RepresentationModelAssemblerSupport<PackageClass, PackageClassDTO> {
	@Autowired
	private PackageClassMapper packageClassRelationMapper;
	@Autowired
	private PackageItemRepository packageItemRepository;
	
	
	public PackagePackageClassAssembler() {
		super(PackageItemPackageClassController.class, PackageClassDTO.class);
	}
	
	@Override
	public PackageClassDTO toModel(PackageClass entity) {
		
		return packageClassRelationMapper.toDto(entity);
	}
	
	@Override
	public CollectionModel<PackageClassDTO> toCollectionModel(Iterable<? extends PackageClass> entities) {
		return super.toCollectionModel(entities);
	}
	
	
}
