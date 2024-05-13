package com.transit.backend.datalayers.controller.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.datalayers.controller.WarehouseAddressController;
import com.transit.backend.datalayers.controller.WarehouseController;
import com.transit.backend.datalayers.controller.WarehouseWarehousePropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerExtendProperties;
import com.transit.backend.datalayers.controller.assembler.wrapper.WarehouseWarehouseAddressAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.WarehouseWarehousePropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.controller.dto.OrderLegDTO;
import com.transit.backend.datalayers.controller.dto.WarehouseDTO;
import com.transit.backend.datalayers.controller.dto.WarehousePropertyDTO;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.OrderLeg;
import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.WarehouseMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import static com.transit.backend.config.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class WarehouseAssembler extends AbstractAssemblerExtendProperties<Warehouse, WarehouseDTO, WarehouseProperty, WarehousePropertyDTO, WarehouseWarehousePropertyController, WarehouseController> {
	
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private WarehouseMapper warehouseMapper;
	
	@Autowired
	private WarehouseWarehousePropertyAssemblerWrapper warehouseWarehousePropertyAssemblerWrapper;
	
	@Autowired
	private WarehouseWarehouseAddressAssemblerWrapper warehouseWarehouseAddressAssemblerWrapper;
	
	public WarehouseAssembler() {
		super(Warehouse.class, WarehouseDTO.class);
	}
	
	@Override
	public WarehouseDTO toModel(Warehouse Warehouse) {
		
		var dto = super.toModel(Warehouse);
		dto.setAddress(toLocationsDTO(Warehouse.getId(), Warehouse.getAddress()));
		dto.setOrderLegs(toOrderLegDTO(Warehouse.getId(), Warehouse.getOrderLegs()));
		dto.add(linkTo(methodOn(WarehouseWarehousePropertyController.class).read(dto.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, FILTER_ALL_OWN_AND_SHARED)).withRel("warehouseProperties"));
		dto.add(linkTo(methodOn(WarehouseAddressController.class).read(dto.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, FILTER_ALL_OWN_AND_SHARED)).withRel("warehouseAddresses"));
		return dto;
	}
	
	@Override
	public AbstractMapper<Warehouse, WarehouseDTO> getMapper() {
		return this.warehouseMapper;
	}
	
	@Override
	public CollectionModel<WarehouseDTO> toCollectionModel(Iterable<? extends Warehouse> entities) {
		return super.toCollectionModel(entities);
		
	}
	
	@Override
	public Class<WarehouseController> getControllerClass() {
		return WarehouseController.class;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<WarehouseProperty, WarehousePropertyDTO, Warehouse, WarehouseDTO, WarehouseController, WarehouseWarehousePropertyController> getSubAssemblerWrapper() {
		return this.warehouseWarehousePropertyAssemblerWrapper;
	}
	
	private AddressDTO toLocationsDTO(UUID WarehouseId, Address address) {
		if (address == null) {
			return null;
		} else {
			return warehouseWarehouseAddressAssemblerWrapper.toModel(address, WarehouseId, false);
		}
	}
	
	private SortedSet<OrderLegDTO> toOrderLegDTO(UUID WarehouseId, SortedSet<OrderLeg> orderLegs) {
		if (orderLegs == null || orderLegs.isEmpty()) {
			return new TreeSet<>();
		} else {
			throw new NotImplementedException();
			//return new TreeSet<>(...AssemblerWrapper.toCollectionModel(orderLegs, WarehouseId, false).getContent());
		}
	}
	
}
