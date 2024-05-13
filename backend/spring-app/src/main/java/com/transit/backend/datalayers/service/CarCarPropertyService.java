package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.CarProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;

import java.util.UUID;

public interface CarCarPropertyService extends CrudServiceSubRessource<CarProperty, UUID, UUID> {
}
