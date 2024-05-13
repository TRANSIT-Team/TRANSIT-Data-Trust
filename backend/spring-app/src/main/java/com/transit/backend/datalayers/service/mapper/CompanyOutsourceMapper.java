package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.outsource.CompanyOutsourceDTO;
import com.transit.backend.datalayers.domain.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CompanyAddressMapper.class})

public interface CompanyOutsourceMapper extends AbstractMapper<Company, CompanyOutsourceDTO> {
	
	@Mapping(target = "companyAddresses", ignore = true)
	@Mapping(target = "companyUsers", ignore = true)
	@Mapping(target = "companyDeliveryArea", ignore = true)
	Company toEntity(CompanyOutsourceDTO dto);
	
	
	@Mapping(target = "companyProperties.company", ignore = true)
	CompanyOutsourceDTO toDto(Company entity);
}
