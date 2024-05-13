package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.PackageClassController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssembler;
import com.transit.backend.datalayers.controller.dto.PackageClassDTO;
import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PackageClassMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

@Component
public class PackageClassAssembler extends AbstractAssembler<PackageClass, PackageClassDTO, PackageClassController> {
	@Autowired
	private PackageClassMapper packageClassMapper;
	
	public PackageClassAssembler() {
		super(PackageClassController.class, PackageClassDTO.class);
	}
	
	@Override
	public PackageClassDTO toModel(PackageClass entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<PackageClass, PackageClassDTO> getMapper() {
		return this.packageClassMapper;
	}
	
	@Override
	public CollectionModel<PackageClassDTO> toCollectionModel(Iterable<? extends PackageClass> entities) {
		return super.toCollectionModel(entities);
	}
	
	@Override
	public Class<PackageClassController> getControllerClass() {
		return PackageClassController.class;
	}
	
	
}