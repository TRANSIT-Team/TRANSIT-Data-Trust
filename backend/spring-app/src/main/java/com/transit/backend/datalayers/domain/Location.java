package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Point;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Location")
@Table(name = "locations")
@Inheritance(strategy = InheritanceType.JOINED)
@Audited(withModifiedFlag = true)
@Cache(region = "locationCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Location extends AbstractEntity implements Serializable, Comparable<Location> {
	
	
	private Point locationPoint;
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof Location location)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(locationPoint, location.locationPoint);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), locationPoint);
	}
	
	@Override
	public int compareTo(@NotNull Location o) {
		if (this.locationPoint == null || o.getLocationPoint() == null) {
			if (this.getId() == null || o.getId() == null) {
				return -1;
			}
			return this.getId().compareTo(o.getId());
		}
		return this.locationPoint.compareTo(o.getLocationPoint());
	}
	
	
}
