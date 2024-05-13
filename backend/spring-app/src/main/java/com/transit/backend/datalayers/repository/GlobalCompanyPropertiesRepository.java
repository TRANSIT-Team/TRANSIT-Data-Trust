package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.GlobalCompanyProperties;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalCompanyPropertiesRepository extends AbstractRepository<GlobalCompanyProperties> {
	
	
	public Optional<GlobalCompanyProperties> findGlobalCompanyPropertiesByName(String name);
	
}
