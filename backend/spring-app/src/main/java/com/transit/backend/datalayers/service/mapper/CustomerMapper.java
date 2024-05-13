package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.CustomerDTO;
import com.transit.backend.datalayers.domain.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper extends AbstractMapper<Customer, CustomerDTO> {
	
	Customer toEntity(CustomerDTO dto);
	
	
	CustomerDTO toDto(Customer entity);
}
