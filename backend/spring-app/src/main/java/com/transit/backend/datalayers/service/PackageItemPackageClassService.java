package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.PackageClass;

import java.util.Optional;
import java.util.UUID;

public interface PackageItemPackageClassService {
	Optional<PackageClass> readOne(UUID packageItemId, UUID packageClassId);
}
