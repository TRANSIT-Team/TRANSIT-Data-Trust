package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "PackageRoute")
@Table(name = "packageRoutes")
@Audited(withModifiedFlag = true)
@Cache(region = "packageRouteCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class PackageRoute extends AbstractEntity implements Serializable {
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	private PackageItem packageItem;
	
	@ManyToOne
	private OrderLeg orderLegs;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), orderLegs);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof PackageRoute that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(orderLegs, that.orderLegs);
	}
}
