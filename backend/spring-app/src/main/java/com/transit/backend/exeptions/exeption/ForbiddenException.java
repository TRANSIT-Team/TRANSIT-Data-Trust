package com.transit.backend.exeptions.exeption;

import java.util.UUID;

public class ForbiddenException extends RuntimeException {
	
	public ForbiddenException() {
		super("You have no Access to Make this Authority or Group Change");
	}
	
	public ForbiddenException(UUID companyIdAuthPerson, UUID companyIdNewPerson) {
		super("Forbidden while not same CompanyID of CompanyAdmin and for user to be created or update. Company ID: " + companyIdAuthPerson + " and " + companyIdNewPerson);
	}
	
	public ForbiddenException(String value) {
		super(value);
	}
}
