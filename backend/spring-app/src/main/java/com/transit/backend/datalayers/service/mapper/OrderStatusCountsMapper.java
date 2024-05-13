package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.OrderStatusCountsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderStatusCountsMapper extends AbstractMapper<OrderStatusCountsDTO, OrderStatusCountsDTO> {

}
