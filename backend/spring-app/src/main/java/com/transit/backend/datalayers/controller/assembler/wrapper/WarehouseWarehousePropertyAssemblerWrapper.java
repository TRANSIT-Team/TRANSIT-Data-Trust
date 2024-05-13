package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.WarehouseController;
import com.transit.backend.datalayers.controller.WarehouseWarehousePropertyController;
import com.transit.backend.datalayers.controller.assembler.WarehousePropertyAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.WarehouseDTO;
import com.transit.backend.datalayers.controller.dto.WarehousePropertyDTO;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class WarehouseWarehousePropertyAssemblerWrapper extends AssemblerWrapperSubAbstract<WarehouseProperty, WarehousePropertyDTO, Warehouse, WarehouseDTO, WarehouseController, WarehouseWarehousePropertyController> {
	
	@Autowired
	WarehousePropertyAssembler warehousePropertyAssembler;
	
	@Override
	public RepresentationModelAssemblerSupport<WarehouseProperty, WarehousePropertyDTO> getAssemblerSupport() {
		return this.warehousePropertyAssembler;
	}
	
	@Override
	public Class<WarehouseWarehousePropertyController> getNestedControllerClazz() {
		return WarehouseWarehousePropertyController.class;
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
