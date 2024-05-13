package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceNested;

import java.util.UUID;


public interface UserPropertyService extends CrudServiceNested<UserProperty, UUID> {

}