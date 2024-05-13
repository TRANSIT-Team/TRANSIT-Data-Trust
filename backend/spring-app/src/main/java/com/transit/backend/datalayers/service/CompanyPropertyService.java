package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.CompanyProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceNested;

import java.util.UUID;


public interface CompanyPropertyService extends CrudServiceNested<CompanyProperty, UUID> {
}