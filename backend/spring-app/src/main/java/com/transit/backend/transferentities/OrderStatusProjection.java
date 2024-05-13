package com.transit.backend.transferentities;

import java.util.UUID;

public interface OrderStatusProjection {
	
	UUID getId();
	
	UUID getCompanyId();
	
	boolean isSuborderType();
	
	String getOrderStatus();
	
	
	UUID getNewOrderId();
	
	UUID getOldOrderId();
	
	
}
