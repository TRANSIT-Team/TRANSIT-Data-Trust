package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;

import java.util.UUID;

public interface CarService extends CrudServiceExtend<Car, UUID> {
}
