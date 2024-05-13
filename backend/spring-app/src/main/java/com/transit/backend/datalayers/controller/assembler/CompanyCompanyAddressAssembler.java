package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.CompanyCompanyAddressController;
import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.service.mapper.CompanyAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class CompanyCompanyAddressAssembler extends RepresentationModelAssemblerSupport<CompanyAddress, CompanyAddressDTO> {
	@Autowired
	private CompanyAddressMapper companyAddressMapper;
	
	
	public CompanyCompanyAddressAssembler() {
		super(CompanyCompanyAddressController.class, CompanyAddressDTO.class);
	}
	
	@Override
	public CompanyAddressDTO toModel(CompanyAddress entity) {
		return companyAddressMapper.toDto(entity);
	}
	
	@Override
	public CollectionModel<CompanyAddressDTO> toCollectionModel(Iterable<? extends CompanyAddress> entities) {
		return super.toCollectionModel(entities);
	}
	
	
}