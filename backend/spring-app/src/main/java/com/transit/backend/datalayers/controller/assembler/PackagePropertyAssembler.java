package com.transit.backend.datalayers.controller.assembler;


import com.transit.backend.datalayers.controller.PackagePropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssembler;
import com.transit.backend.datalayers.controller.dto.PackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PackagePropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;


@Component

public class PackagePropertyAssembler extends AbstractAssembler<PackageProperty, PackagePropertyDTO, PackagePropertyController> {
	@Autowired
	private PackagePropertyMapper packagePropertyMapper;
	
	public PackagePropertyAssembler() {
		super(PackagePropertyController.class, PackagePropertyDTO.class);
	}
	
	@Override
	public PackagePropertyDTO toModel(PackageProperty entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<PackageProperty, PackagePropertyDTO> getMapper() {
		return this.packagePropertyMapper;
	}
	
	@Override
	public CollectionModel<PackagePropertyDTO> toCollectionModel(Iterable<? extends PackageProperty> entities) {
		return super.toCollectionModel(entities);
		
	}
	
	@Override
	public Class<PackagePropertyController> getControllerClass() {
		return PackagePropertyController.class;
	}
	
}