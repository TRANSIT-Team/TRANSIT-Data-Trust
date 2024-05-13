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
@Entity(name = "Car")
@Table(name = "car")
@Audited(withModifiedFlag = true)
@Cache(region = "carCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Car extends AbstractParentEntity<CarProperty> implements Serializable, Comparable<Car> {
	
	
	private String plate;
	
	private String type;
	
	private String capacity;
	
	private String weight;
	
	
	@OneToMany
	@OrderBy("createDate")
	private SortedSet<Location> locations;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "car")
	@OrderBy("key ASC")
	private SortedSet<CarProperty> carProperties;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "car")
	@OrderBy("createDate ASC")
	private SortedSet<OrderLeg> orderLegs;
	
	
	@Override
	public int compareTo(@NotNull Car o) {
		return this.getId().compareTo(o.getId());
	}
	
	@Override
	public SortedSet<CarProperty> getProperties() {
		return this.carProperties;
	}
	
	@Override
	public void setProperties(SortedSet<CarProperty> carProperties) {
		this.setCarProperties(carProperties);
	}
	
	public void setCarProperties(SortedSet<CarProperty> carProperties) {
		if (this.carProperties == null) {
			this.carProperties = new TreeSet<>();
		}
		if (!this.carProperties.equals(carProperties)) {
			this.carProperties.clear();
			if (carProperties != null) {
				this.carProperties.addAll(carProperties);
			}
		}
	}
	
	@Override
	public void addProperty(CarProperty carProperty) {
		this.addCarProperty(carProperty);
	}
	
	public void addCarProperty(CarProperty carProperty) {
		if (this.carProperties == null) {
			this.carProperties = new TreeSet<>();
		}
		this.carProperties.add(carProperty);
		carProperty.setCar(this);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Car car = (Car) o;
		return Objects.equal(plate, car.plate) && Objects.equal(type, car.type) && Objects.equal(capacity, car.capacity) && Objects.equal(weight, car.weight) && Objects.equal(locations, car.locations) && Objects.equal(carProperties, car.carProperties);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), plate, type, capacity, weight, locations, carProperties);
	}
	
	@PrePersist
	@PreUpdate
	private void save() {
		if (this.carProperties == null) {
			this.carProperties = new TreeSet<>();
		}
	}
}
