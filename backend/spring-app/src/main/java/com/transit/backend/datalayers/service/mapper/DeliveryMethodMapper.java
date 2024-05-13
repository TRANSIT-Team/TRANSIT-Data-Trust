package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.DeliveryMethodDTO;
import com.transit.backend.datalayers.domain.DeliveryMethod;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryMethodMapper extends AbstractMapper<DeliveryMethod, DeliveryMethodDTO> {
	
	DeliveryMethod toEntity(DeliveryMethodDTO dto);
	
	DeliveryMethodDTO toDto(DeliveryMethod entity);
	
}