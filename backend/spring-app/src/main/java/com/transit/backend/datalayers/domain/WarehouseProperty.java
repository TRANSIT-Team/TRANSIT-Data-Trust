package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractPropertyEntity;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "WarehouseProperties")
@Table(name = "warehouseProperties")
@Audited(withModifiedFlag = true)
@Cache(region = "warehousePropertyCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class WarehouseProperty extends AbstractPropertyEntity<Warehouse> implements Serializable, Comparable<WarehouseProperty> {
	
	
	private String key;
	
	
	private String value;
	
	private String type;
	
	@ManyToOne(fetch = FetchType.LAZY)
	
	private Warehouse warehouse;
	
	@Override
	
	public UUID getParentId() {
		return this.warehouse.getId();
	}
	
	@Override
	
	public Warehouse getParent() {
		return this.warehouse;
	}
	
	@Override
	public void setParent(Warehouse parent) {
		this.warehouse = parent;
	}
	
	
	@Override
	public int compareTo(@NotNull WarehouseProperty o) {
		if (this.key == null || o.getKey() == null) {
			if (this.getId() == null || o.getId() == null) {
				return -1;
			}
			return this.getId().compareTo(o.getId());
		}
		return this.key.compareTo(o.getKey());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), key, value, type);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		WarehouseProperty that = (WarehouseProperty) o;
		return Objects.equal(key, that.key) && Objects.equal(value, that.value) && Objects.equal(type, that.type);
	}
}
