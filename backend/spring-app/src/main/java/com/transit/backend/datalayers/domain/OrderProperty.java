package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractPropertyEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "OrderProperty")
@Table(name = "orderProperties")
@Audited(withModifiedFlag = true)
@Cache(region = "orderPropertyCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class OrderProperty extends AbstractPropertyEntity<Order> implements Serializable, Comparable<OrderProperty> {
	
	@Column(name = "key")
	@Type(type = "text")
	private String key;
	
	@Column(name = "value")
	@Type(type = "text")
	private String value;
	
	@Column(name = "type")
	@Type(type = "text")
	private String type;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	
	private Order order;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), key, value, type);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		OrderProperty that = (OrderProperty) o;
		return Objects.equal(key, that.key) && Objects.equal(value, that.value) && Objects.equal(type, that.type);
	}
	
	@Override
	
	public UUID getParentId() {
		return this.order.getId();
	}
	
	@Override
	
	public Order getParent() {
		return this.order;
	}
	
	@Override
	public void setParent(Order parent) {
		this.setOrder(parent);
	}
	
	
	@Override
	public int compareTo(@NotNull OrderProperty o) {
		if (this.key == null || o.getKey() == null) {
			if (this.getId() == null || o.getId() == null) {
				return -1;
			}
			return this.getId().compareTo(o.getId());
		}
		return this.key.compareTo(o.getKey());
	}
	
	public OrderProperty copyOrderProperty() {
		var orderProp = new OrderProperty();
		orderProp.setKey(this.getKey());
		orderProp.setValue(this.getValue());
		orderProp.setType(this.getType());
		return orderProp;
	}
}
