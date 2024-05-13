package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.domain.Address;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AddressMapper extends AbstractMapper<Address, AddressDTO> {
	
	Address toEntity(AddressDTO dto);
	
	AddressDTO toDto(Address entity);
	
}