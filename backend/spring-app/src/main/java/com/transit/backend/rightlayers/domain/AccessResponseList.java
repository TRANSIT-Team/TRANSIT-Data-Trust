package com.transit.backend.rightlayers.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccessResponseList {
	
	private List<AccessResponseDTO> objects;
}
