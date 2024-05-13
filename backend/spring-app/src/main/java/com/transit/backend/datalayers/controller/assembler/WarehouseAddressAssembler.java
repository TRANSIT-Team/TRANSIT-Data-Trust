package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.WarehouseAddressController;
import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.service.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class WarehouseAddressAssembler extends RepresentationModelAssemblerSupport<Address, AddressDTO> {
	@Autowired
	private AddressMapper addressMapper;
	
	public WarehouseAddressAssembler() {
		super(WarehouseAddressController.class, AddressDTO.class);
	}
	
	@Override
	public AddressDTO toModel(Address entity) {
		return addressMapper.toDto(entity);
	}
}
