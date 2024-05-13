package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;

import java.util.UUID;

public interface WarehouseWarehousePropertyService extends CrudServiceSubRessource<WarehouseProperty, UUID, UUID> {
}
