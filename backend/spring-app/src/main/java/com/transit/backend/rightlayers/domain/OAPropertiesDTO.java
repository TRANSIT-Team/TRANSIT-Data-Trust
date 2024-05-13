package com.transit.backend.rightlayers.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OAPropertiesDTO {
	
	private Set<String> readProperties;
	
	private Set<String> writeProperties;
}
