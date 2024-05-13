package com.transit.backend.rightlayers.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateInternTransferObject {
	
	private UUID identityId;
	private UUID objectId;
	
	private UUID orderId;
	
	private OAPropertiesDTO oaPropertiesDTO;
}
