package com.transit.backend.exeptions.exeption;

import com.transit.backend.datalayers.domain.CompanyAddressId;

import java.util.UUID;

public class NoSuchElementFoundException extends RuntimeException {
	
	public NoSuchElementFoundException(String className, UUID id) {
		super(className + " with id " + id + " does not exists.");
	}
	
	
	public NoSuchElementFoundException(String className, CompanyAddressId id) {
		super(className + " with id " + id + " does not exists.");
	}
	
	public NoSuchElementFoundException(String className, UUID id, UUID keycloakId) {
		super(className + " with KeycloakId " + keycloakId + " does not exists.");
	}
	
	public NoSuchElementFoundException(String text) {
		super(text);
	}
	
}