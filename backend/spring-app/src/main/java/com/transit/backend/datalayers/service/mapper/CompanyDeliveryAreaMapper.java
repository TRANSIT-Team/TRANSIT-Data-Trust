package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.CompanyDeliveryAreaDTO;
import com.transit.backend.datalayers.domain.CompanyDeliveryArea;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyDeliveryAreaMapper extends AbstractMapper<CompanyDeliveryArea, CompanyDeliveryAreaDTO> {
	
	@Mapping(target = "company", ignore = true)
	CompanyDeliveryArea toEntity(CompanyDeliveryAreaDTO dto);
	
	@Mapping(source = "company.id", target = "companyId")
	CompanyDeliveryAreaDTO toDto(CompanyDeliveryArea entity);
}
