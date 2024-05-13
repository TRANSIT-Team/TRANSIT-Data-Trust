package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.PackageClass;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PackageClassRepository extends AbstractRepository<PackageClass> {
	
	public Optional<PackageClass> findPackageClassByName(String name);
	
}