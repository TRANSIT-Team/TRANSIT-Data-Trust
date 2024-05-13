package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.domain.enums.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.ValidationException;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "PackageTracking")
@Table(name = "packageTracking")
@Audited(withModifiedFlag = true)
@Cache(region = "packageTrackingCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class PackageTracking extends AbstractEntity implements Serializable {
	
	@ManyToOne(fetch = FetchType.LAZY)
	private PackageItem packageItem;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Location location;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Car car;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Warehouse warehouse;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private User supplier;
	
	@Enumerated(EnumType.STRING)
	private PackageStatus status;
	
	@Type(type = "text")
	private String comment;
	
	@PrePersist
	@PreUpdate
	private void validate() {
		if (
				(this.location == null && this.car == null && this.warehouse == null) ||
						(this.location == null && this.car != null && this.warehouse != null) ||
						(this.location != null && this.car == null && this.warehouse != null) ||
						(this.location != null && this.car != null && this.warehouse == null)
		) {
			throw new ValidationException();
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), packageItem, location, car, warehouse, supplier, status, comment);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		PackageTracking that = (PackageTracking) o;
		return Objects.equal(packageItem, that.packageItem) && Objects.equal(location, that.location) && Objects.equal(car, that.car) && Objects.equal(warehouse, that.warehouse) && Objects.equal(supplier, that.supplier) && status == that.status && Objects.equal(comment, that.comment);
	}
}
