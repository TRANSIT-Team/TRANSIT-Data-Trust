package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.registration.CompanyAddressDTORegistration;
import com.transit.backend.datalayers.domain.CompanyAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface CompanyAddressMapperRegistration extends AbstractMapper<CompanyAddress, CompanyAddressDTORegistration> {
	
	@Mapping(target = "address.locationPoint", source = "locationPoint")
	@Mapping(target = "address.street", source = "street")
	@Mapping(target = "address.zip", source = "zip")
	@Mapping(target = "address.city", source = "city")
	@Mapping(target = "address.state", source = "state")
	@Mapping(target = "address.addressExtra", source = "addressExtra")
	@Mapping(target = "address.companyName", source = "companyName")
	@Mapping(target = "address.clientName", source = "clientName")
	@Mapping(target = "addressType", source = "addressType")
	@Mapping(target = "address.country", source = "country")
	@Mapping(target = "address.isoCode", source = "isoCode")
	@Mapping(target = "company", ignore = true)
	CompanyAddress toEntity(CompanyAddressDTORegistration dto);
	
	
	CompanyAddressDTORegistration toDto(CompanyAddress entity);
}
