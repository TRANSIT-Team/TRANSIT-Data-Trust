package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;

import java.util.UUID;


public interface AddressService extends CrudServiceExtend<Address, UUID> {
}