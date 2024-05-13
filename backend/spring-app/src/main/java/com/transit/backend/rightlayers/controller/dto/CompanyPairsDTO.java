package com.transit.backend.rightlayers.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyPairsDTO {
	
	
	private UUID hasRightsCompany;
	
	private UUID getRightsCompany;
	
	
}
