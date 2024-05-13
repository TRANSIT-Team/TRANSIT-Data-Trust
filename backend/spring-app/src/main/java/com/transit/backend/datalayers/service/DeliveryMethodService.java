package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.DeliveryMethod;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;

import java.util.UUID;

public interface DeliveryMethodService extends CrudServiceExtend<DeliveryMethod, UUID> {
}
