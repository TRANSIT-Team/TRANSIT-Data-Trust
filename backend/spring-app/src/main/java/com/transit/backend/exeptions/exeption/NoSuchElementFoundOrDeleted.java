package com.transit.backend.exeptions.exeption;

import com.transit.backend.datalayers.domain.CompanyAddressId;

import java.util.UUID;

public class NoSuchElementFoundOrDeleted extends RuntimeException {
	
	public NoSuchElementFoundOrDeleted(String className, UUID id) {
		super(className + " with id " + id + " does not exists or is deleted.");
	}
	
	public NoSuchElementFoundOrDeleted(String className, CompanyAddressId id) {
		super(className + " with id " + id + " does not exists or is deleted.");
	}
}
