package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceNested;

import java.util.UUID;

public interface CarPropertyService extends CrudServiceNested<CarProperty, UUID> {
}
