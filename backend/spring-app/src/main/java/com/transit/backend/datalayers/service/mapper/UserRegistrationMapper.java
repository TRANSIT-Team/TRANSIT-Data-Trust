package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.registration.UserDTORegistration;
import com.transit.backend.transferentities.UserRegistrationObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CompanyMapperRegistration.class, CompanyAddressMapperRegistration.class})
public interface UserRegistrationMapper extends AbstractMapper<UserRegistrationObject, UserDTORegistration> {
	
	
	@Mapping(target = "userRepresentation.id", source = "keycloakId")
	
	@Mapping(target = "userRepresentation.realmRoles", source = "realmRoles")
	@Mapping(target = "userRepresentation.groups", source = "groups")
	
	@Mapping(target = "user.keycloakId", source = "keycloakId")
	@Mapping(target = "user.jobPosition", source = "jobPosition")
	
	@Mapping(target = "user.userProperties", source = "userProperties")
	
	@Mapping(target = "company", source = "company")
	@Mapping(target = "companyAddresses", source = "company.companyAddresses")
	@Mapping(target = "userRepresentation.enabled", source = "enabled")
	UserRegistrationObject toEntity(UserDTORegistration dto);
	
	
	UserDTORegistration toDto(UserRegistrationObject entity);
	
	
}
