package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.OrderLeg;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;

import java.util.UUID;

public interface OrderOrderLegService extends CrudServiceSubRessource<OrderLeg, UUID, UUID> {
}
