package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.domain.CompanyAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CompanyAddressMapper extends AbstractMapper<CompanyAddress, CompanyAddressDTO> {
	@Mapping(target = "address.id", source = "id")
	@Mapping(target = "address.locationPoint", source = "locationPoint")
	@Mapping(target = "address.street", source = "street")
	@Mapping(target = "address.zip", source = "zip")
	@Mapping(target = "address.city", source = "city")
	@Mapping(target = "address.state", source = "state")
	@Mapping(target = "address.addressExtra", source = "addressExtra")
	@Mapping(target = "address.companyName", source = "companyName")
	@Mapping(target = "address.clientName", source = "clientName")
	@Mapping(target = "addressType", source = "addressType")
	@Mapping(target = "company.id", source = "companyId")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "address.country", source = "country")
	@Mapping(target = "address.isoCode", source = "isoCode")
	@Mapping(source = "id", target = "id.addressId")
	@Mapping(source = "companyId", target = "id.companyId")
	@Mapping(target = "company.companyAddresses", ignore = true)
	CompanyAddress toEntity(CompanyAddressDTO dto);
	
	
	@Mapping(source = "address.locationPoint", target = "locationPoint")
	@Mapping(source = "address.street", target = "street")
	@Mapping(source = "address.zip", target = "zip")
	@Mapping(source = "address.city", target = "city")
	@Mapping(source = "address.state", target = "state")
	@Mapping(source = "address.addressExtra", target = "addressExtra")
	@Mapping(source = "address.companyName", target = "companyName")
	@Mapping(source = "address.clientName", target = "clientName")
	@Mapping(source = "addressType", target = "addressType")
	@Mapping(source = "address.country", target = "country")
	@Mapping(source = "address.isoCode", target = "isoCode")
	@Mapping(source = "id.addressId", target = "id")
	@Mapping(source = "id.companyId", target = "companyId")
	CompanyAddressDTO toDto(CompanyAddress entity);
	
}