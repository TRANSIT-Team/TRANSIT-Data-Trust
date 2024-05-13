package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.CompanyCompanyAddressController;
import com.transit.backend.datalayers.controller.CompanyController;
import com.transit.backend.datalayers.controller.assembler.CompanyCompanyAddressAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanyCompanyAddressAssemblerWrapper extends AssemblerWrapperAbstract<CompanyAddress, CompanyAddressDTO, Company, CompanyDTO, CompanyController, CompanyCompanyAddressController> {
	
	@Autowired
	CompanyCompanyAddressAssembler companyCompanyAddressAssembler;
	
	public CompanyAddressDTO toModel(CompanyAddress entity, UUID companyId, boolean backwardLink) {
		return super.toModel(entity, companyId, backwardLink);
	}
	
	@Override
	public RepresentationModelAssemblerSupport<CompanyAddress, CompanyAddressDTO> getAssemblerSupport() {
		return this.companyCompanyAddressAssembler;
	}
	
	public CompanyAddressDTO addLinks(CompanyAddressDTO dto, UUID companyId, boolean backwardLink) {
		return super.addLinks(dto, companyId, backwardLink);
	}
	
	@Override
	public Class<CompanyCompanyAddressController> getNestedControllerClazz() {
		return CompanyCompanyAddressController.class;
	}
	
	@Override
	public Class<CompanyController> getRootControllerClazz() {
		return CompanyController.class;
	}
	
	@Override
	public Class<Company> getDomainNameClazz() {
		return Company.class;
	}
	
	public CollectionModel<CompanyAddressDTO> toCollectionModel(Iterable<? extends CompanyAddress> entities, UUID companyId, boolean backwardLink) {
		return super.toCollectionModelWithExtraParameter(entities, companyId, backwardLink);
	}
}
