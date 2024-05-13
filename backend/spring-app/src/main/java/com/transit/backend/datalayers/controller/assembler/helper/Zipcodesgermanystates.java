package com.transit.backend.datalayers.controller.assembler.helper;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@Component
public class Zipcodesgermanystates implements Serializable {
	@Id
	@Column(name = "zipcode", nullable = false)
	private String zipcode;
	
	@Column(name = "country_code")
	private String country_code;
	
	@Column(name = "place")
	private String place;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "latitude")
	private Double latitude;
	
	@Column(name = "longitude")
	private Double longitude;
	
}