package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.OrderPartDTO;
import com.transit.backend.datalayers.domain.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderPartMapper {
	@Mapping(source = "id", target = "id")
	Order toEntity(OrderPartDTO dto);
	
	@Mapping(source = "id", target = "shortId")
	@Mapping(source = "id", target = "id")
	OrderPartDTO toDto(Order entity);
	
	
}
