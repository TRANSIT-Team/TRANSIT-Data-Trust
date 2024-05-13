package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.CompanyDeliveryArea;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyDeliveryAreaRepository extends AbstractRepository<CompanyDeliveryArea> {
	
	Optional<CompanyDeliveryArea> findByIdAndDeleted(UUID id, boolean deleted);
}
