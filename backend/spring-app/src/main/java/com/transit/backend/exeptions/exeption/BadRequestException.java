package com.transit.backend.exeptions.exeption;

public class BadRequestException extends RuntimeException {
	
	public BadRequestException(String message) {
		super(message);
	}
	
	
}
