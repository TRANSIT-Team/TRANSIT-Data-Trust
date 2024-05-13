package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.CostDefaultPropertyDTO;
import com.transit.backend.datalayers.domain.CostDefaultProperty;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CostDefaultPropertyMapper extends AbstractMapper<CostDefaultProperty, CostDefaultPropertyDTO> {

}