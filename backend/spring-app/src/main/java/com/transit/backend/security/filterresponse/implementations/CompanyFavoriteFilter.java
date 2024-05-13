package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.CompanyDeliveryArea;
import com.transit.backend.datalayers.domain.CompanyFavorite;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

@Component
public class CompanyFavoriteFilter extends AbstractEntityFilter<CompanyFavorite, CompanyFavorite> implements EntityFilterHelper<CompanyFavorite, CompanyFavorite> {
	@Override
	public CompanyFavorite transformToTransfer(CompanyFavorite entity) {
		return entity;
	}
	
	@Override
	public CompanyFavorite transformToEntity(CompanyFavorite entity) {
		return entity;
	}
	
	@Override
	public CompanyFavorite transformToTransfer(CompanyFavorite entity, CompanyFavorite entityOld) {
		return entity;
	}
	
	@Override
	public Class<CompanyFavorite> getClazz() {
		return CompanyFavorite.class;
	}
	
	@Override
	public String getPathToEntity(CompanyFavorite entity, CompanyFavorite entity2) {
		return "/companies/" + entity.getCompanyId() + "/favorites/" + entity.getId();
	}
	
}
