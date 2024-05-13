package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.CompanyController;
import com.transit.backend.datalayers.controller.CompanyFavoriteController;
import com.transit.backend.datalayers.controller.assembler.CompanyFavoriteAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.CompanyFavoriteDTO;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyFavorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanyFavoriteAssemblerWrapper extends AssemblerWrapperAbstract<CompanyFavorite, CompanyFavoriteDTO, Company, CompanyDTO, CompanyController, CompanyFavoriteController> {
	@Autowired
	CompanyFavoriteAssembler companyCompanyFavoriteAssembler;
	
	@Override
	public RepresentationModelAssemblerSupport<CompanyFavorite, CompanyFavoriteDTO> getAssemblerSupport() {
		return companyCompanyFavoriteAssembler;
	}
	
	@Override
	public Class<CompanyFavoriteController> getNestedControllerClazz() {
		return CompanyFavoriteController.class;
	}
	
	@Override
	public Class<CompanyController> getRootControllerClazz() {
		return CompanyController.class;
	}
	
	@Override
	public Class<Company> getDomainNameClazz() {
		return Company.class;
	}
	
	
	public CompanyFavoriteDTO toModel(CompanyFavorite entity, UUID companyId, boolean backwardLink) {
		return super.toModel(entity, companyId, backwardLink);
	}
	
	public CompanyFavoriteDTO addLinks(CompanyFavoriteDTO dto, UUID companyId, boolean backwardLink) {
		return super.addLinks(dto, companyId, backwardLink);
	}
	
	
	public CollectionModel<CompanyFavoriteDTO> toCollectionModel(Iterable<? extends CompanyFavorite> entities, UUID companyId, boolean backwardLink) {
		return super.toCollectionModelWithExtraParameter(entities, companyId, backwardLink);
	}
}
