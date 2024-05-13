package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.WarehouseAddressController;
import com.transit.backend.datalayers.controller.WarehouseController;
import com.transit.backend.datalayers.controller.assembler.WarehouseAddressAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.controller.dto.WarehouseDTO;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.Warehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class WarehouseWarehouseAddressAssemblerWrapper extends AssemblerWrapperAbstract<Address, AddressDTO, Warehouse, WarehouseDTO, WarehouseController, WarehouseAddressController> {
	
	@Autowired
	WarehouseAddressAssembler warehouseAddressAssembler;
	
	@Override
	public RepresentationModelAssemblerSupport<Address, AddressDTO> getAssemblerSupport() {
		return this.warehouseAddressAssembler;
	}
	
	@Override
	public Class<WarehouseAddressController> getNestedControllerClazz() {
		return WarehouseAddressController.class;
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
