package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.Warehouse;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;

import java.util.UUID;

public interface WarehouseService extends CrudServiceExtend<Warehouse, UUID> {
}
