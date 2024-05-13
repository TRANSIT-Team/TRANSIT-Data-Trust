package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.CompanyFavoriteController;
import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.controller.dto.CompanyFavoriteDTO;
import com.transit.backend.datalayers.controller.dto.CompanyFavoriteOverviewDTO;
import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.domain.CompanyFavorite;
import com.transit.backend.datalayers.service.mapper.CompanyFavoriteOverviewMapper;
import com.transit.backend.transferentities.CompanyFavoriteOverview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class CompanyFavoriteOverviewAssembler extends RepresentationModelAssemblerSupport<CompanyFavoriteOverview, CompanyFavoriteOverviewDTO> {
	
	@Autowired
	private CompanyFavoriteOverviewMapper mapper;
	
	public CompanyFavoriteOverviewAssembler() {
		super(CompanyFavoriteController.class, CompanyFavoriteOverviewDTO.class);
	}
	
	
	@Override
	public CompanyFavoriteOverviewDTO toModel(CompanyFavoriteOverview entity) {
		return mapper.toDto(entity);
	}
}
