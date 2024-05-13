package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.CompanyDeliveryArea;
import com.transit.backend.datalayers.domain.CompanyFavorite;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.transferentities.CompanyFavoriteProjection;
import com.transit.backend.transferentities.OrderStatusProjection;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface CompanyFavoriteRepository extends AbstractRepository<CompanyFavorite> {
	
	
	public List<CompanyFavoriteProjection> findAllByCompanyId(UUID companyId);
	
	public List<CompanyFavorite> findAllByCompanyIdAndNameAndDeleted(UUID companyId, String name, boolean deleted);
}
