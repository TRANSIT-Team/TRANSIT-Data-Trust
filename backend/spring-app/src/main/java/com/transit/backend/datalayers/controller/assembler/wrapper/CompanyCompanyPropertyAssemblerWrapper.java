package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.CompanyCompanyPropertyController;
import com.transit.backend.datalayers.controller.CompanyController;
import com.transit.backend.datalayers.controller.assembler.CompanyPropertyAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanyCompanyPropertyAssemblerWrapper extends AssemblerWrapperSubAbstract<CompanyProperty, CompanyPropertyDTO, Company, CompanyDTO, CompanyController, CompanyCompanyPropertyController> {
	
	@Autowired
	CompanyPropertyAssembler companyPropertyAssembler;
	
	public CompanyPropertyDTO toModel(CompanyProperty entity, UUID companyId, boolean backwardLink) {
		return super.toModel(entity, companyId, backwardLink);
	}
	
	@Override
	public RepresentationModelAssemblerSupport<CompanyProperty, CompanyPropertyDTO> getAssemblerSupport() {
		return this.companyPropertyAssembler;
	}
	
	public CompanyPropertyDTO addLinks(CompanyPropertyDTO dto, UUID companyId, boolean backwardLink) {
		return super.addLinks(dto, companyId, backwardLink);
	}
	
	@Override
	public Class<CompanyCompanyPropertyController> getNestedControllerClazz() {
		return CompanyCompanyPropertyController.class;
	}
	
	@Override
	public Class<CompanyController> getRootControllerClazz() {
		return CompanyController.class;
	}
	
	@Override
	public Class<Company> getDomainNameClazz() {
		return Company.class;
	}
	
	public CollectionModel<CompanyPropertyDTO> toCollectionModel(Iterable<? extends CompanyProperty> entities, UUID companyId, boolean backwardLink) {
		return super.toCollectionModel(entities, companyId, backwardLink);
	}
}
