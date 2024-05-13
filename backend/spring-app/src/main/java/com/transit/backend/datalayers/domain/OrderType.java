package com.transit.backend.datalayers.domain;

import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "OrderType")
@Table(name = "orderTypes")
@Audited(withModifiedFlag = true)
@Cache(region = "orderTypeCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class OrderType extends AbstractParentEntity<OrderTypeProperty> implements Serializable, Comparable<OrderType> {
	
	
	private String typename;
	
	@OneToMany
	@OrderBy("key ASC")
	private SortedSet<OrderTypeProperty> orderTypeProperties;
	
	@Override
	public SortedSet<OrderTypeProperty> getProperties() {
		return this.orderTypeProperties;
	}
	
	@Override
	public void setProperties(SortedSet<OrderTypeProperty> orderProperties) {
		this.setOrderTypeProperties(orderProperties);
	}
	
	public void setOrderTypeProperties(SortedSet<OrderTypeProperty> orderTypeProperties) {
		if (this.orderTypeProperties == null) {
			this.orderTypeProperties = new TreeSet<>();
		}
		if (!this.orderTypeProperties.equals(orderTypeProperties)) {
			this.orderTypeProperties.clear();
			if (orderTypeProperties != null) {
				this.orderTypeProperties.addAll(orderTypeProperties);
			}
		}
	}
	
	@Override
	public void addProperty(OrderTypeProperty orderTypeProperty) {
		this.addOrderProperties(orderTypeProperty);
	}
	
	public void addOrderProperties(OrderTypeProperty orderTypeProperty) {
		if (this.orderTypeProperties == null) {
			this.orderTypeProperties = new TreeSet<>();
		}
		this.orderTypeProperties.add(orderTypeProperty);
		orderTypeProperty.setOrderType(this);
	}
	
	@PrePersist
	@PreUpdate
	private void save() {
		if (this.orderTypeProperties == null) {
			this.orderTypeProperties = new TreeSet<>();
		}
		
	}
	
	@Override
	public int compareTo(@NotNull OrderType o) {
		return this.hashCode() - o.hashCode();
	}
}
