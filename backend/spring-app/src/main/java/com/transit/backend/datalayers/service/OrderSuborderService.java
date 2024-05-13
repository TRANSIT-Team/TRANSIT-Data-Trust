package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;

import java.util.UUID;

public interface OrderSuborderService extends CrudServiceSubRessource<Order, UUID, UUID> {
}
