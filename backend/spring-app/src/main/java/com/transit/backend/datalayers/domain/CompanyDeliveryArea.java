package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "CompanyDeliveryArea")
@Table(name = "companydeliveryareas", indexes = {@Index(columnList = "company_id")})
@Audited(withModifiedFlag = true)
@Cache(region = "companyDeliveryAreaCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CompanyDeliveryArea extends AbstractEntity implements Serializable, Comparable<CompanyDeliveryArea> {
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<String> deliveryAreaZips;
	
	@Type(type = "text")
	@Basic(fetch = FetchType.LAZY)
	private String deliveryAreaPolyline;
	
	
	private Geometry deliveryAreaGeom;
	
	@OneToOne(fetch = FetchType.LAZY)
	
	private Company company;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), deliveryAreaZips, deliveryAreaPolyline, deliveryAreaGeom);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof CompanyDeliveryArea that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(deliveryAreaZips, that.deliveryAreaZips) && Objects.equal(deliveryAreaPolyline, that.deliveryAreaPolyline) && Objects.equal(deliveryAreaGeom, that.deliveryAreaGeom);
	}
	
	@Override
	public int compareTo(@NotNull CompanyDeliveryArea o) {
		var y = 0;
		if (this.deliveryAreaPolyline != null && o.getDeliveryAreaPolyline() != null) {
			y = this.deliveryAreaPolyline.compareTo(o.getDeliveryAreaPolyline());
		}
		var z = 0;
		if (this.deliveryAreaGeom != null && o.getDeliveryAreaGeom() != null) {
			z = this.deliveryAreaGeom.compareTo(o.getDeliveryAreaGeom());
		}
		return 2 ^ y * 3 ^ z;
	}
}
