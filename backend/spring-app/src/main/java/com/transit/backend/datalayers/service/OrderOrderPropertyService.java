package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.OrderProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;

import java.util.UUID;

public interface OrderOrderPropertyService extends CrudServiceSubRessource<OrderProperty, UUID, UUID> {
}
