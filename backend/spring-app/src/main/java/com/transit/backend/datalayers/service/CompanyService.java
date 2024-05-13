package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.DefaultSharingRights;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;

import java.util.Optional;
import java.util.UUID;


public interface CompanyService extends CrudServiceExtend<Company, UUID> {
	
	Optional<DefaultSharingRights> getDefaultSharingRights(UUID companyId);
	
	DefaultSharingRights setDefaultSharingRights(DefaultSharingRights defaultSharingRights);
}