package com.transit.backend.datalayers.service.helper;

import com.transit.backend.exeptions.exeption.BadRequestException;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import com.transit.backend.security.authmodel.TransitAuthorities;
import lombok.Getter;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Component
public class KeycloakRolesManagement {
	RealmResource realmResource;
	UsersResource usersResource;
	
	public KeycloakRolesManagement(KeycloakServiceManager keycloakServiceManager) {
		this.realmResource = keycloakServiceManager.getRealmResource();
		this.usersResource = keycloakServiceManager.getUsersResource();
	}
	
	public UserRepresentation createKeycloakUserRepresentation(UserRepresentation userRepresentation) {
		var response = this.usersResource.create(userRepresentation);
		if (response.getStatus() != HttpStatus.CREATED.value()) {
			throw new UnprocessableEntityExeption("Cannot create User in Keycloak with status code: " + response.getStatus() + " . See https://github.com/keycloak/keycloak-community/blob/main/design/rest-api-guideline.md for failure description.");
		}
		var roles = userRepresentation.getRealmRoles();
		String userId = CreatedResponseUtil.getCreatedId(response);
		var userRepresentationCreated = this.usersResource.get(userId).toRepresentation();
		userRepresentationCreated.setRealmRoles(roles);
		updateKeycloakRoles(userRepresentationCreated, this.usersResource.get(userId));
		return userRepresentationCreated;
		
	}
	
	private void updateKeycloakRoles(UserRepresentation userRepresentation, UserResource userResource) {
		List<RoleRepresentation> userRoles = new ArrayList<>();
		for (String role : userRepresentation.getRealmRoles()) {
			RoleRepresentation realmRole = realmResource.roles().get(role).toRepresentation();
			if (realmRole != null) {
				userRoles.add(realmRole);
			} else {
				throw new UnprocessableEntityExeption("Role: " + role + " does not exists in Keycloak.");
			}
		}
		var userRolesOld = userResource.roles().realmLevel().listEffective();
		List<RoleRepresentation> rolesToDelete = new ArrayList<>();
		List<String> allRoles = Arrays.asList(TransitAuthorities.ADMIN_GLOBAL.getStringValues());
		for (var oldRole : userRolesOld) {
			if (allRoles.contains(oldRole.getName())) {
				rolesToDelete.add(oldRole);
			}
		}
		userResource.roles().realmLevel().remove(rolesToDelete);
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (Exception ex) {
		
		}
		
		userResource.roles().realmLevel().add(userRoles);
		
	}
	
	public UserRepresentation updateKeycloakUserRepresentation(UserRepresentation userRepresentation) {
		var userResource = this.usersResource.get(userRepresentation.getId());
		var userRepresentationOld = userResource.toRepresentation();
		if (userRepresentationOld == null) {
			throw new BadRequestException("User with keycloakId: " + userRepresentation.getId() + " does not exists.");
		}
		var roles = userRepresentation.getRealmRoles();
		userResource.update(userRepresentation);
		var userRepresentationUpdated = this.usersResource.get(userRepresentation.getId()).toRepresentation();
		userRepresentationUpdated.setRealmRoles(roles);
		updateKeycloakRoles(userRepresentationUpdated, this.usersResource.get(userRepresentation.getId()));
		return userRepresentationUpdated;
	}
	
	public UserRepresentation updateOnlyRoles(UserRepresentation userRepresentation) {
		var roles = userRepresentation.getRealmRoles();
		var userResource = this.usersResource.get(userRepresentation.getId());
		var userRepresentationOld = userResource.toRepresentation();
		if (userRepresentationOld == null) {
			throw new BadRequestException("User with keycloakId: " + userRepresentation.getId() + " does not exists.");
		}
		if (userRepresentation.getFirstName().equals(userRepresentationOld.getFirstName()) &&
				userRepresentation.getLastName().equals(userRepresentationOld.getLastName()) &&
				userRepresentation.getUsername().equals(userRepresentationOld.getUsername()) &&
				userRepresentation.getEmail().equals(userRepresentationOld.getEmail())) {
			
			updateKeycloakRoles(userRepresentation, this.usersResource.get(userRepresentation.getId()));
			return userRepresentation;
		} else {
			throw new BadRequestException("Can  only update Roles");
		}
	}
	
	public UserRepresentation getUserRepresentation(UUID keycloakId) {
		var userResource = this.usersResource.get(keycloakId.toString());
		var userRepresentation = userResource.toRepresentation();
		if (userRepresentation == null) {
			throw new BadRequestException("User with keycloakId: " + keycloakId + " does not exists.");
		}
		var userRealmRoles = userResource.roles().realmLevel().listEffective();
		List<String> allRoles = Arrays.asList(TransitAuthorities.ADMIN_GLOBAL.getStringValues());
		List<String> transitRoles = new ArrayList<>();
		for (var role : userRealmRoles) {
			if (allRoles.contains(role.getName())) {
				transitRoles.add(role.getName());
			}
		}
		userRepresentation.setRealmRoles(transitRoles);
		return userRepresentation;
	}
}
