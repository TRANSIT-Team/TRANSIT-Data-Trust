package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.CompanyController;
import com.transit.backend.datalayers.controller.CompanyDeliveryAreaController;
import com.transit.backend.datalayers.controller.assembler.CompanyDeliveryAreaAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.CompanyDeliveryAreaDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyDeliveryArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanyCompanyDeliveryAreaAssemblerWrapper extends AssemblerWrapperAbstract<CompanyDeliveryArea, CompanyDeliveryAreaDTO, Company, CompanyDTO, CompanyController, CompanyDeliveryAreaController> {
	
	@Autowired
	CompanyDeliveryAreaAssembler companyDeliveryAreaAssembler;
	
	public CompanyDeliveryAreaDTO toModel(CompanyDeliveryArea entity, UUID companyId, boolean backwardLink) {
		return super.toModel(entity, companyId, backwardLink);
	}
	
	@Override
	public RepresentationModelAssemblerSupport<CompanyDeliveryArea, CompanyDeliveryAreaDTO> getAssemblerSupport() {
		return this.companyDeliveryAreaAssembler;
	}
	
	@Override
	public Class<CompanyDeliveryAreaController> getNestedControllerClazz() {
		return CompanyDeliveryAreaController.class;
	}
	
	@Override
	public Class<CompanyController> getRootControllerClazz() {
		return CompanyController.class;
	}
	
	@Override
	public Class<Company> getDomainNameClazz() {
		return Company.class;
	}
	
	public CollectionModel<CompanyDeliveryAreaDTO> toCollectionModel(Iterable<? extends CompanyDeliveryArea> entities, UUID companyId, boolean backwardLink) {
		return super.toCollectionModelWithExtraParameter(entities, companyId, backwardLink);
	}
}