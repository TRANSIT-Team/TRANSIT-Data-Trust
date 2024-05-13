package com.transit.backend.rightlayers.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RIghtsDtoCoreProperties {
	
	
	List<String> readProperties;
	List<String> writeProperties;
	
}
