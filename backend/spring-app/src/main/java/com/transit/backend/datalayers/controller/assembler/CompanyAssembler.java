package com.transit.backend.datalayers.controller.assembler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.datalayers.controller.*;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerExtendProperties;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyCompanyPropertyAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import static com.transit.backend.config.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CompanyAssembler extends AbstractAssemblerExtendProperties<Company, CompanyDTO, CompanyProperty, CompanyPropertyDTO, CompanyCompanyPropertyController, CompanyController> {
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private CompanyMapper companyMapper;
	
	@Autowired
	private CompanyCompanyPropertyAssemblerWrapper companyCompanyPropertyAssemblerWrapper;
	
	
	public CompanyAssembler() {
		super(CompanyController.class, CompanyDTO.class);
	}
	
	@Override
	public CompanyDTO toModel(Company company) {
		var dto = super.toModel(company);
		dto.add(linkTo(methodOn(CompanyCompanyPropertyController.class).read(dto.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, FILTER_ALL_OWN_AND_SHARED)).withRel("companyProperties"));
		dto.add(linkTo(methodOn(CompanyCompanyAddressController.class).read(dto.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, FILTER_ALL_OWN_AND_SHARED)).withRel("companyAddresses"));
		if (company.getCompanyDeliveryArea() != null) {
			dto.add(linkTo(methodOn(CompanyDeliveryAreaController.class).readOne(dto.getId(), company.getCompanyDeliveryArea().getId())).withRel("companyDeliveryArea"));
		}
		dto.add(linkTo(methodOn(CompanyCustomerController.class).read(dto.getId(), "deleted==false", DEFAULT_SKIP, DEFAULT_TAKE, EMPTY_STRING, true)).withRel("customers"));
		return dto;
	}
	
	@Override
	public AbstractMapper<Company, CompanyDTO> getMapper() {
		return this.companyMapper;
	}
	
	@Override
	public CollectionModel<CompanyDTO> toCollectionModel(Iterable<? extends Company> entities) {
		return super.toCollectionModel(entities);
		
	}
	
	@Override
	public Class<CompanyController> getControllerClass() {
		return CompanyController.class;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<CompanyProperty, CompanyPropertyDTO, Company, CompanyDTO, CompanyController, CompanyCompanyPropertyController> getSubAssemblerWrapper() {
		return this.companyCompanyPropertyAssemblerWrapper;
	}
	
	
}
