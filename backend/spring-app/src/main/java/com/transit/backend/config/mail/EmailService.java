package com.transit.backend.config.mail;

import javax.mail.MessagingException;

public interface EmailService {
	void sendSimpleMessage(String to,
	                       String subject,
	                       String text);
	
	
	void sendEmailRequest(String subCompanyName, String parentCompanyName, String linkToOrder, String subject, String to, String destinationZip, String destinationPlace) throws MessagingException;
	
	void sendEmailResendRequest(String subCompanyName, String parentCompanyName, String linkToOrder, String subject, String to, String destinationZip, String destinationPlace) throws MessagingException;
	
	void sendEmailRevoked(String subCompanyName, String parentCompanyName, String subject, String to, String destinationZip, String destinationPlace) throws MessagingException;
	
	void sendEmailAcceptedToOpen(String subCompanyName, String parentCompanyName, String linkToOrder, String subject, String to, String destinationZip, String destinationPlace) throws MessagingException;
	
	void sendEmailToCANCELED(String subCompanyName, String parentCompanyName, String linkToOrder, String subject, String to, String destinationZip, String destinationPlace, String reasonToCancel) throws MessagingException;
	
	void sendEmailNewMessages(String subCompanyName, String subject, String to, String linkToMessages) throws MessagingException;
}