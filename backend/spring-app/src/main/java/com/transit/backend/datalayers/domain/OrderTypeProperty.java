package com.transit.backend.datalayers.domain;

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
@Entity(name = "OrderTypeProperties")
@Table(name = "orderTypeProperties")
@Audited(withModifiedFlag = true)
@Cache(region = "orderTypePropertyCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class OrderTypeProperty extends AbstractPropertyEntity<OrderType> implements Serializable, Comparable<OrderTypeProperty> {
	
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
	
	private OrderType orderType;
	
	
	@Override
	
	public UUID getParentId() {
		return this.orderType.getId();
	}
	
	@Override
	
	public OrderType getParent() {
		return this.orderType;
	}
	
	@Override
	public void setParent(OrderType parent) {
		this.setOrderType(parent);
	}
	
	
	@Override
	public int compareTo(@NotNull OrderTypeProperty o) {
		if (this.key == null || o.getKey() == null) {
			if (this.getId() == null || o.getId() == null) {
				return -1;
			}
			return this.getId().compareTo(o.getId());
		}
		return this.key.compareTo(o.getKey());
	}
}
