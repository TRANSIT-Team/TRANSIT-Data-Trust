package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.CompanyCustomerController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerWithoutLink;
import com.transit.backend.datalayers.controller.dto.CustomerDTO;
import com.transit.backend.datalayers.domain.Customer;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

@Component
public class CustomerAssembler extends AbstractAssemblerWithoutLink<Customer, CustomerDTO, CompanyCustomerController> {
	@Autowired
	private CustomerMapper addressMapper;
	
	public CustomerAssembler() {
		super(CompanyCustomerController.class, CustomerDTO.class);
	}
	
	@Override
	public CustomerDTO toModel(Customer entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<Customer, CustomerDTO> getMapper() {
		return this.addressMapper;
	}
	
	@Override
	public CollectionModel<CustomerDTO> toCollectionModel(Iterable<? extends Customer> entities) {
		return super.toCollectionModel(entities);
	}
	
	@Override
	public Class<CompanyCustomerController> getControllerClass() {
		return CompanyCustomerController.class;
	}
	
	
}

