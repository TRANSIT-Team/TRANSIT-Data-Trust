package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;
import com.transit.backend.transferentities.UserIdTransfer;
import com.transit.backend.transferentities.UserRegistrationObject;
import com.transit.backend.transferentities.UserTransferObject;

import java.util.Optional;
import java.util.UUID;


public interface UserService extends CrudServiceExtend<UserTransferObject, UUID> {
	
	UserTransferObject createOwnerWithCompany(UserRegistrationObject entity);
	
	Optional<UserIdTransfer> getUserID();
	
	Optional<UserTransferObject> getSelfUserDTO();
	
}