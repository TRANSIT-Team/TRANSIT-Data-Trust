package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
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
@Entity(name = "Warehouse")
@Table(name = "warehouse")
@Audited(withModifiedFlag = true)
@Cache(region = "warehouseCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Warehouse extends AbstractParentEntity<WarehouseProperty> implements Serializable, Comparable<Warehouse> {
	
	
	private String name;
	@OneToOne
	
	private Address address;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "warehouse")
	@OrderBy("key ASC")
	private SortedSet<WarehouseProperty> warehouseProperties;
	
	private Long capacity;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "warehouse")
	@OrderBy("createDate ASC")
	private SortedSet<OrderLeg> orderLegs;
	
	
	@Override
	public SortedSet<WarehouseProperty> getProperties() {
		return this.warehouseProperties;
	}
	
	@Override
	public void setProperties(SortedSet<WarehouseProperty> warehouseProperties) {
		this.setWarehouseProperties(warehouseProperties);
	}
	
	public void setWarehouseProperties(SortedSet<WarehouseProperty> warehouseProperties) {
		if (this.warehouseProperties == null) {
			this.warehouseProperties = new TreeSet<>();
		}
		if (!this.warehouseProperties.equals(warehouseProperties)) {
			this.warehouseProperties.clear();
			if (warehouseProperties != null) {
				this.warehouseProperties.addAll(warehouseProperties);
			}
		}
	}
	
	@Override
	public void addProperty(WarehouseProperty warehouseProperty) {
		this.addWarehouseProperty(warehouseProperty);
	}
	
	public void addWarehouseProperty(WarehouseProperty warehouseProperty) {
		if (this.warehouseProperties == null) {
			this.warehouseProperties = new TreeSet<>();
		}
		this.warehouseProperties.add(warehouseProperty);
		warehouseProperty.setWarehouse(this);
	}
	
	@Override
	public int compareTo(@NotNull Warehouse o) {
		return this.getId().compareTo(o.getId());
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Warehouse warehouse = (Warehouse) o;
		return Objects.equal(address, warehouse.address) && Objects.equal(warehouseProperties, warehouse.warehouseProperties) && Objects.equal(capacity, warehouse.capacity) && Objects.equal(orderLegs, warehouse.orderLegs);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), address, warehouseProperties, capacity, orderLegs);
	}
}
