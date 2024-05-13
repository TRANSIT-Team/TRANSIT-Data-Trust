package com.transit.backend.config;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Constants {
	public static final List<String> CREATE_ACTION_EMAILS = new ArrayList<>(Arrays.asList("UPDATE_PASSWORD", "VERIFY_EMAIL", "UPDATE_PROFILE"));
	public static final List<String> UPDATE_ACTION_EMAILS = new ArrayList<>(List.of("VERIFY_EMAIL"));
	
	
	public static final String DELIMITER = ",";
	public static final String SOURCE = "source";
	
	public static final String TARGET = "target";
	
	public static final int DEFAULT_SKIP = -1;
	
	public static final int DEFAULT_TAKE = 0;
	
	public static final String EMPTY_STRING = "";
	
	public static final String EMAIL_STRING = "Email";
	
	public static final String UNREAD_MESSAGE_NOTIFICATION_TIME = "unreadMessagesNotificationTime";
	public static final boolean FILTER_BY_MY_OWN_GENERATED_DATA = true;
	
	public static final boolean FILTER_ALL_OWN_AND_SHARED = false;
	
}
