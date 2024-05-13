package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.PackagePackageProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceNested;

import java.util.UUID;

public interface PackageItemPackagePropertyService extends CrudServiceNested<PackagePackageProperty, UUID> {
}
