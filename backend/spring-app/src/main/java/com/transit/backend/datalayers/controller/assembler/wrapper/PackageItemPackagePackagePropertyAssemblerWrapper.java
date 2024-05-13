package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.PackageItemController;
import com.transit.backend.datalayers.controller.PackageItemPackagePackagePropertyController;
import com.transit.backend.datalayers.controller.assembler.PackagePackagePropertyAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.PackageItemDTO;
import com.transit.backend.datalayers.controller.dto.PackagePackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PackageItemPackagePackagePropertyAssemblerWrapper extends AssemblerWrapperSubAbstract<PackagePackageProperty, PackagePackagePropertyDTO, PackageItem, PackageItemDTO, PackageItemController, PackageItemPackagePackagePropertyController> {
	
	@Autowired
	PackagePackagePropertyAssembler packagePackagePropertyAssembler;
	
	public PackagePackagePropertyDTO toModel(PackagePackageProperty entity, UUID packageItemId, boolean backwardLink) {
		return super.toModel(entity, packageItemId, backwardLink);
	}
	
	@Override
	public RepresentationModelAssemblerSupport<PackagePackageProperty, PackagePackagePropertyDTO> getAssemblerSupport() {
		return this.packagePackagePropertyAssembler;
	}
	
	public PackagePackagePropertyDTO addLinks(PackagePackagePropertyDTO dto, UUID packageItemId, boolean backwardLink) {
		return super.addLinks(dto, packageItemId, backwardLink);
	}
	
	@Override
	public Class<PackageItemPackagePackagePropertyController> getNestedControllerClazz() {
		return PackageItemPackagePackagePropertyController.class;
	}
	
	@Override
	public Class<PackageItemController> getRootControllerClazz() {
		return PackageItemController.class;
	}
	
	@Override
	public Class<PackageItem> getDomainNameClazz() {
		return PackageItem.class;
	}
	
	public CollectionModel<PackagePackagePropertyDTO> toCollectionModel(Iterable<? extends PackagePackageProperty> entities, UUID packageItemId, boolean backwardLink) {
		return super.toCollectionModel(entities, packageItemId, backwardLink);
	}
}