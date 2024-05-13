package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.OrderLegDTO;
import com.transit.backend.datalayers.domain.OrderLeg;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderLegMapper extends AbstractMapper<OrderLeg, OrderLegDTO> {


}