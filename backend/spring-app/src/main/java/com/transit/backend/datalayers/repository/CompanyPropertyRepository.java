package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;

import java.util.SortedSet;
import java.util.UUID;

public interface CompanyPropertyRepository extends AbstractRepository<CompanyProperty> {
	
	
	SortedSet<CompanyProperty> findCompanyPropertiesByCompanyId(UUID companyId);
}
