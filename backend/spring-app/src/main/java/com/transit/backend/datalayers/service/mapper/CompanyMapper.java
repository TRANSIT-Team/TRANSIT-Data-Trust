package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.domain.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CompanyMapper extends AbstractMapper<Company, CompanyDTO> {
	
	
	@Mapping(target = "companyAddresses", ignore = true)
	@Mapping(target = "companyUsers", ignore = true)
	@Mapping(target = "companyDeliveryArea", ignore = true)
	Company toEntity(CompanyDTO dto);
	
	
	@Mapping(target = "companyProperties.company", ignore = true)
		//@Mapping(target = "companyAddresses.company", ignore = true)
		//@Mapping(target = "companyUsers.company", ignore = true)
	CompanyDTO toDto(Company entity);
	
	
}