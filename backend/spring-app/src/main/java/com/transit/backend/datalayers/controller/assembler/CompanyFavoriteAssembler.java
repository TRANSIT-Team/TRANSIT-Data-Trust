package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.CompanyCompanyAddressController;
import com.transit.backend.datalayers.controller.CompanyFavoriteController;
import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.controller.dto.CompanyDeliveryAreaDTO;
import com.transit.backend.datalayers.controller.dto.CompanyFavoriteDTO;
import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.domain.CompanyDeliveryArea;
import com.transit.backend.datalayers.domain.CompanyFavorite;
import com.transit.backend.datalayers.service.mapper.CompanyAddressMapper;
import com.transit.backend.datalayers.service.mapper.CompanyFavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class CompanyFavoriteAssembler extends RepresentationModelAssemblerSupport<CompanyFavorite, CompanyFavoriteDTO> {
	@Autowired
	private CompanyFavoriteMapper companyAddressMapper;
	
	
	public CompanyFavoriteAssembler() {
		super(CompanyFavoriteController.class, CompanyFavoriteDTO.class);
	}
	
	@Override
	public CompanyFavoriteDTO toModel(CompanyFavorite entity) {
		return companyAddressMapper.toDto(entity);
	}
	
	@Override
	public CollectionModel<CompanyFavoriteDTO> toCollectionModel(Iterable<? extends CompanyFavorite> entities) {
		return super.toCollectionModel(entities);
	}
	
	
}