package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.OrderPropertyDTO;
import com.transit.backend.datalayers.domain.OrderProperty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderPropertyMapper extends AbstractMapper<OrderProperty, OrderPropertyDTO> {
	@Mapping(target = "order", ignore = true)
	OrderProperty toEntity(OrderPropertyDTO dto);
	
	OrderPropertyDTO toDto(OrderProperty entity);
	
}