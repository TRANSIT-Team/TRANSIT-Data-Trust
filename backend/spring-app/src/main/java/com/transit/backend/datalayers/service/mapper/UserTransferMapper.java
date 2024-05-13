package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.UserDTO;
import com.transit.backend.transferentities.UserTransferObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserTransferMapper extends AbstractMapper<UserTransferObject, UserDTO> {
	
	@Mapping(target = "user.company.id", source = "companyId")
	@Mapping(target = "user.jobPosition", source = "jobPosition")
	@Mapping(target = "user.userProperties", source = "userProperties")
	
	@Mapping(target = "user.id", source = "id")
	@Mapping(target = "user.keycloakId", source = "keycloakId")
	@Mapping(target = "userRepresentation.id", source = "keycloakId")
	@Mapping(target = "userRepresentation.firstName", source = "firstName")
	@Mapping(target = "userRepresentation.lastName", source = "lastName")
	@Mapping(target = "userRepresentation.email", source = "email")
	@Mapping(target = "userRepresentation.username", source = "email")
	@Mapping(target = "userRepresentation.realmRoles", source = "realmRoles")
	@Mapping(target = "userRepresentation.groups", source = "groups")
	@Mapping(target = "userRepresentation.enabled", source = "enabled")
	@Mapping(target = "user.createDate", source = "createDate")
	@Mapping(target = "user.modifyDate", source = "modifyDate")
	@Mapping(target = "user.createdBy", source = "createdBy")
	@Mapping(target = "user.lastModifiedBy", source = "lastModifiedBy")
	@Mapping(target = "user.deleted", source = "deleted")
	@Mapping(target = "user.keycloakEmail", source = "email")
	UserTransferObject toEntity(UserDTO dto);
	
	
	@Mapping(source = "user.company.id", target = "companyId")
	@Mapping(source = "user.jobPosition", target = "jobPosition")
	@Mapping(source = "user.userProperties", target = "userProperties")
	@Mapping(source = "user.id", target = "id")
	@Mapping(source = "user.keycloakId", target = "keycloakId")
	@Mapping(source = "userRepresentation.firstName", target = "firstName")
	@Mapping(source = "userRepresentation.lastName", target = "lastName")
	@Mapping(source = "userRepresentation.email", target = "email")
	@Mapping(source = "userRepresentation.realmRoles", target = "realmRoles")
	@Mapping(source = "userRepresentation.groups", target = "groups")
	@Mapping(source = "userRepresentation.enabled", target = "enabled")
	@Mapping(source = "user.createDate", target = "createDate")
	@Mapping(source = "user.modifyDate", target = "modifyDate")
	@Mapping(source = "user.createdBy", target = "createdBy")
	@Mapping(source = "user.lastModifiedBy", target = "lastModifiedBy")
	@Mapping(source = "user.deleted", target = "deleted")
	UserDTO toDto(UserTransferObject entity);
	
	
}
