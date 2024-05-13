package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;

import java.util.UUID;

public interface CarLocationService extends CrudServiceSubRessource<Location, UUID, UUID> {
}
