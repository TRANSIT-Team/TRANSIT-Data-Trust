package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.ContactPerson;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;

import java.util.UUID;

public interface ContactPersonService extends CrudServiceExtend<ContactPerson, UUID> {
}
