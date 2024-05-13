package com.transit.geoservice.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "zipcodesgermanypolygonscombined")
public class ZipCodesCombinedPolygon {
	
	@Id
	@Type(type = "text")
	private String IdString;
	
	Geometry geometry;
}
