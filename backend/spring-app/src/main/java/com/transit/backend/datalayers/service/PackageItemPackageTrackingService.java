package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.PackageTracking;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;

import java.util.UUID;

public interface PackageItemPackageTrackingService extends CrudServiceSubRessource<PackageTracking, UUID, UUID> {
}
