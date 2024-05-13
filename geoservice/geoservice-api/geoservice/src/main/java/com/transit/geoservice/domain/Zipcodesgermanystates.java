package com.transit.geoservice.domain;


import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "zipcodesgermanystates")
@IdClass(ZipStatesId.class)
public class Zipcodesgermanystates implements Serializable {
	@Id
	@Column(name = "zipcode", nullable = false)
	private String zipcode;
	
	@Column(name = "country_code")
	private String country_code;
	@Id
	@Column(name = "place")
	private String place;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "latitude")
	private Double latitude;
	
	@Column(name = "longitude")
	private Double longitude;
	
}