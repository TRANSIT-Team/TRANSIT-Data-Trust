package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.PackageItemController;
import com.transit.backend.datalayers.controller.PackagePackagePropertyController;
import com.transit.backend.datalayers.controller.assembler.PackagePackagePropertyAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperNestedAbstract;
import com.transit.backend.datalayers.controller.dto.PackageItemDTO;
import com.transit.backend.datalayers.controller.dto.PackagePackagePropertyDTO;
import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.PackagePackageProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class PackagePackagePropertyAssemblerWrapper extends AssemblerWrapperNestedAbstract<PackagePackageProperty, PackagePackagePropertyDTO, PackageItem, PackageItemDTO, PackageItemController, PackagePackagePropertyController> {
	
	@Autowired
	PackagePackagePropertyAssembler packagePackageProperty;
	
	
	@Override
	public RepresentationModelAssemblerSupport<PackagePackageProperty, PackagePackagePropertyDTO> getAssemblerSupport() {
		return this.packagePackageProperty;
	}
	
	@Override
	public Class<PackagePackagePropertyController> getNestedControllerClazz() {
		return PackagePackagePropertyController.class;
	}
	
	@Override
	public Class<PackageItemController> getRootControllerClazz() {
		return PackageItemController.class;
	}
	
	@Override
	public Class<PackageItem> getDomainNameClazz() {
		return PackageItem.class;
	}
}
