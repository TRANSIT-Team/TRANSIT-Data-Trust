package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;

import java.util.UUID;

public interface OrderTypeService extends CrudServiceExtend<Order, UUID> {
}
