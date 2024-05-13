package com.transit.geoservice.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "zipcodesgermanypolygonscidMapping")
public class IdCombineMapping {
	
	@Id
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	@Type(type = "text")
	String zips;
}
