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
@Entity(name = "OrderLeg")
@Table(name = "orderLeg")
@Audited(withModifiedFlag = true)
@Cache(region = "orderLegCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class OrderLeg extends AbstractEntity implements Serializable {
	
	
	private String type;
	
	
	private String status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Car car;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Warehouse warehouse;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private OrderRoute orderRoute;
	
	
	@Override
	public boolean equals(Object o) {
		
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		OrderLeg orderLeg = (OrderLeg) o;
		return Objects.equal(type, orderLeg.type) && Objects.equal(status, orderLeg.status) && Objects.equal(car, orderLeg.car) && Objects.equal(warehouse, orderLeg.warehouse) && Objects.equal(orderRoute, orderLeg.orderRoute);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), type, status, car, warehouse, orderRoute);
	}
}
