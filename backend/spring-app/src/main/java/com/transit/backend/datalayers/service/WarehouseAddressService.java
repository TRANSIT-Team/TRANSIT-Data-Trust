package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;

import java.util.UUID;

public interface WarehouseAddressService extends CrudServiceSubRessource<Address, UUID, UUID> {

}
