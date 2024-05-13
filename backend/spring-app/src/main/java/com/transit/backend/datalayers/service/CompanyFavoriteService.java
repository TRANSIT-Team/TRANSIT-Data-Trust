package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.CompanyFavorite;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.transferentities.CompanyFavoriteOverview;

import java.util.List;
import java.util.UUID;

public interface CompanyFavoriteService extends CrudServiceSubRessource<CompanyFavorite, UUID, UUID> {
	
	List<CompanyFavoriteOverview> getOverview(UUID companyId);
}
