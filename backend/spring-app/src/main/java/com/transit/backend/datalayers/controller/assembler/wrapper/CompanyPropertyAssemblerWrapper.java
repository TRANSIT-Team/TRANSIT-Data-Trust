package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.CompanyController;
import com.transit.backend.datalayers.controller.CompanyPropertyController;
import com.transit.backend.datalayers.controller.assembler.CompanyPropertyAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperNestedAbstract;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class CompanyPropertyAssemblerWrapper extends AssemblerWrapperNestedAbstract<CompanyProperty, CompanyPropertyDTO, Company, CompanyDTO, CompanyController, CompanyPropertyController> {
	
	@Autowired
	CompanyPropertyAssembler companyPropertyAssembler;
	
	@Override
	public RepresentationModelAssemblerSupport<CompanyProperty, CompanyPropertyDTO> getAssemblerSupport() {
		return this.companyPropertyAssembler;
	}
	
	@Override
	public Class<CompanyPropertyController> getNestedControllerClazz() {
		return CompanyPropertyController.class;
	}
	
	@Override
	public Class<CompanyController> getRootControllerClazz() {
		return CompanyController.class;
	}
	
	@Override
	public Class<Company> getDomainNameClazz() {
		return Company.class;
	}
}
