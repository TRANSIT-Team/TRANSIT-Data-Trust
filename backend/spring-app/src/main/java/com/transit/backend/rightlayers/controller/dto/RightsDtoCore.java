package com.transit.backend.rightlayers.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RightsDtoCore {
	private UUID companyId;
	private UUID entityId;
	private UUID orderId;
	private RIghtsDtoCoreProperties properties;
	
	
}
