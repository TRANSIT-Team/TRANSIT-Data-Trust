package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.PaymentPropertyDTO;
import com.transit.backend.datalayers.domain.PaymentProperty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentPropertyMapper extends AbstractMapper<PaymentProperty, PaymentPropertyDTO> {
	@Mapping(target = "payment", ignore = true)
	PaymentProperty toEntity(PaymentPropertyDTO dto);
	
	PaymentPropertyDTO toDto(PaymentProperty entity);
	
}