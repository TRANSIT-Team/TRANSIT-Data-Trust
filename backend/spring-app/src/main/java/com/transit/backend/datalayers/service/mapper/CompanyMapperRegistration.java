package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.registration.CompanyDTORegistration;
import com.transit.backend.datalayers.domain.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyMapperRegistration extends AbstractMapper<Company, CompanyDTORegistration> {
	@Mapping(target = "companyProperties.company", ignore = true)
	@Mapping(target = "properties.company", ignore = true)
	@Mapping(target = "companyAddresses", ignore = true)
	@Mapping(target = "companyUsers", ignore = true)
	Company toEntity(CompanyDTORegistration dto);
	
	
	CompanyDTORegistration toDto(Company entity);
}
