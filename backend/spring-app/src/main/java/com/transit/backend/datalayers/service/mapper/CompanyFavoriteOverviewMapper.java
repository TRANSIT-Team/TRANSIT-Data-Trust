package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.CompanyFavoriteOverviewDTO;
import com.transit.backend.transferentities.CompanyFavoriteOverview;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyFavoriteOverviewMapper extends AbstractMapper<CompanyFavoriteOverview, CompanyFavoriteOverviewDTO> {
}
