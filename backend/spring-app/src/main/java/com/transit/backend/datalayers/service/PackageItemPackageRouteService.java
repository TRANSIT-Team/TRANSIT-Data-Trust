package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.PackageRoute;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;

import java.util.UUID;

public interface PackageItemPackageRouteService extends CrudServiceSubRessource<PackageRoute, UUID, UUID> {
}
