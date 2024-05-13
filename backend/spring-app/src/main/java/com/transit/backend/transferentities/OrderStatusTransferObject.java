package com.transit.backend.transferentities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderStatusTransferObject {
	
	private String orderStatus;
	
	
	private UUID orderId;
}
