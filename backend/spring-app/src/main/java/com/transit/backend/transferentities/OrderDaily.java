package com.transit.backend.transferentities;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface OrderDaily {
	UUID getId();
	
	String getOrderStatus();
	
	OffsetDateTime getPickUpDate();
	
	OffsetDateTime getDestinationDate();
}
