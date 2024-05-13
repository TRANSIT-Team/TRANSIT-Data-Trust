package com.transit.backend.transferentities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CompanyFavoriteOverview {
	
	private String name;
	
	private UUID id;
}
