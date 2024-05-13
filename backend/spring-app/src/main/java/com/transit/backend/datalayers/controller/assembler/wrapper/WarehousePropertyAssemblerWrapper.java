package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.WarehouseController;
import com.transit.backend.datalayers.controller.WarehousePropertyController;
import com.transit.backend.datalayers.controller.assembler.WarehousePropertyAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperNestedAbstract;
import com.transit.backend.datalayers.controller.dto.WarehouseDTO;
import com.transit.backend.datalayers.controller.dto.WarehousePropertyDTO;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class WarehousePropertyAssemblerWrapper extends AssemblerWrapperNestedAbstract<WarehouseProperty, WarehousePropertyDTO, Warehouse, WarehouseDTO, WarehouseController, WarehousePropertyController> {
	@Autowired
	WarehousePropertyAssembler warehousePropertyAssembler;
	
	@Override
	public RepresentationModelAssemblerSupport<WarehouseProperty, WarehousePropertyDTO> getAssemblerSupport() {
		return this.warehousePropertyAssembler;
	}
	
	@Override
	public Class<WarehousePropertyController> getNestedControllerClazz() {
		return WarehousePropertyController.class;
	}
	
	@Override
	public Class<WarehouseController> getRootControllerClazz() {
		return WarehouseController.class;
	}
	
	@Override
	public Class<Warehouse> getDomainNameClazz() {
		return Warehouse.class;
	}
}
