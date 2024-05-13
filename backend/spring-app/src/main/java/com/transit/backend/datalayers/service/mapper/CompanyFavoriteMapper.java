package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.controller.dto.CompanyFavoriteDTO;
import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.domain.CompanyFavorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyFavoriteMapper extends AbstractMapper<CompanyFavorite, CompanyFavoriteDTO> {
	
	
	@Mapping(target = "companyList", source = "companyIds")
	CompanyFavorite toEntity(CompanyFavoriteDTO dto);
	
	@Mapping(source = "companyList", target = "companyIds")
	CompanyFavoriteDTO toDto(CompanyFavorite entity);
	
	
}
