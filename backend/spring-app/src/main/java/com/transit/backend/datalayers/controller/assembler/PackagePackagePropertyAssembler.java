package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.PackageItemPackagePackagePropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerWithoutLink;
import com.transit.backend.datalayers.controller.dto.PackagePackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PackagePackagePropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

@Component
public class PackagePackagePropertyAssembler extends AbstractAssemblerWithoutLink<PackagePackageProperty, PackagePackagePropertyDTO, PackageItemPackagePackagePropertyController> {
	@Autowired
	private PackagePackagePropertyMapper packagePackagePropertyMapper;
	
	
	public PackagePackagePropertyAssembler() {
		super(PackageItemPackagePackagePropertyController.class, PackagePackagePropertyDTO.class);
	}
	
	@Override
	public PackagePackagePropertyDTO toModel(PackagePackageProperty entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<PackagePackageProperty, PackagePackagePropertyDTO> getMapper() {
		return this.packagePackagePropertyMapper;
	}
	
	@Override
	public CollectionModel<PackagePackagePropertyDTO> toCollectionModel(Iterable<? extends PackagePackageProperty> entities) {
		return super.toCollectionModel(entities);
	}
	
	
}