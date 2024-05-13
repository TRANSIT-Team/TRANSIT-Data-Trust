package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PackageItemRepository extends AbstractRepository<PackageItem> {

	
}