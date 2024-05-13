package com.transit.backend.exeptions.exeption;

public class RightsServiceNotAvailableException extends RuntimeException {
	
	public RightsServiceNotAvailableException() {
		super("Rights Service not Available");
	}
}
