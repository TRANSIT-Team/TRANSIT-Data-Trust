package com.transit.geoservice.domain;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
public class ZipStatesId implements Serializable {
	private String zipcode;
	
	private String place;
	
	// default constructor
	
	public ZipStatesId(String zipcode, String place) {
		this.zipcode = zipcode;
		this.place = place;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ZipStatesId that)) return false;
		return Objects.equals(zipcode, that.zipcode) && Objects.equals(place, that.place);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(zipcode, place);
	}
}