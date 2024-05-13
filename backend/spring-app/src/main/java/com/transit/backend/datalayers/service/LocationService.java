package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;

import java.util.UUID;


public interface LocationService extends CrudServiceExtend<Location, UUID> {

}