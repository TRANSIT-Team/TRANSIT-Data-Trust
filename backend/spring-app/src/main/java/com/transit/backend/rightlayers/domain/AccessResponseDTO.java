package com.transit.backend.rightlayers.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccessResponseDTO {
	private UUID objectId;
	private String objectEntityClass;
	private UUID identityId;
	private OAPropertiesDTO objectProperties;
}
