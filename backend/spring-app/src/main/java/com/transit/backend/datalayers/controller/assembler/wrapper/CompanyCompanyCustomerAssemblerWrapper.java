package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.AddressController;
import com.transit.backend.datalayers.controller.CompanyController;
import com.transit.backend.datalayers.controller.CompanyCustomerController;
import com.transit.backend.datalayers.controller.assembler.CustomerAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.CustomerDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CompanyCompanyCustomerAssemblerWrapper extends AssemblerWrapperAbstract<Customer, CustomerDTO, Company, CompanyDTO, CompanyController, CompanyCustomerController> {
	
	@Autowired
	CustomerAssembler companyCustomerAssembler;
	
	public CustomerDTO toModel(Customer entity, UUID companyId, boolean backwardLink) {
		var result = super.toModel(entity, companyId, backwardLink);
		if (entity.getAddressId() != null) {
			result.add(linkTo(methodOn(AddressController.class).readOne(entity.getAddressId())).withRel("address"));
		}
		return result;
	}
	
	@Override
	public RepresentationModelAssemblerSupport<Customer, CustomerDTO> getAssemblerSupport() {
		return this.companyCustomerAssembler;
	}
	
	public CustomerDTO addLinks(CustomerDTO dto, UUID companyId, boolean backwardLink) {
		return super.addLinks(dto, companyId, backwardLink);
	}
	
	@Override
	public Class<CompanyCustomerController> getNestedControllerClazz() {
		return CompanyCustomerController.class;
	}
	
	@Override
	public Class<CompanyController> getRootControllerClazz() {
		return CompanyController.class;
	}
	
	@Override
	public Class<Company> getDomainNameClazz() {
		return Company.class;
	}
	
	public CollectionModel<CustomerDTO> toCollectionModel(Iterable<? extends Customer> entities, UUID companyId, boolean backwardLink) {
		return super.toCollectionModelWithExtraParameter(entities, companyId, backwardLink);
	}
}
