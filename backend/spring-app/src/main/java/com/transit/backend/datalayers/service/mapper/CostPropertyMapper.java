package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.CostPropertyDTO;
import com.transit.backend.datalayers.domain.CostProperty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CostPropertyMapper extends AbstractMapper<CostProperty, CostPropertyDTO> {
	@Mapping(target = "cost", ignore = true)
	CostProperty toEntity(CostPropertyDTO dto);
}
