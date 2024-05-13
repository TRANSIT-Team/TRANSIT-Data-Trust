package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.Cost;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;

import java.util.UUID;

public interface CostService extends CrudServiceExtend<Cost, UUID> {
}
