package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.WarehouseProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceNested;

import java.util.UUID;

public interface WarehousePropertyService extends CrudServiceNested<WarehouseProperty, UUID> {
}
