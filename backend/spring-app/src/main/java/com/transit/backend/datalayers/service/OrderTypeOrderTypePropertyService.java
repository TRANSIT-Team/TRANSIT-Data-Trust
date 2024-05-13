package com.transit.backend.datalayers.service;


import com.transit.backend.datalayers.domain.OrderTypeProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;

import java.util.UUID;

public interface OrderTypeOrderTypePropertyService extends CrudServiceSubRessource<OrderTypeProperty, UUID, UUID> {
}
