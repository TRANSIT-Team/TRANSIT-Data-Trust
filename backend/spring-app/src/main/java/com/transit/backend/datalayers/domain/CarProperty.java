package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractPropertyEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "CarProperties")
@Table(name = "carProperties")
@Audited(withModifiedFlag = true)
@Cache(region = "carPropertyCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CarProperty extends AbstractPropertyEntity<Car> implements Serializable, Comparable<CarProperty> {
	
	
	private String key;
	
	
	private String value;
	
	private String type;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	
	private Car car;
	
	@Override
	
	public UUID getParentId() {
		return this.car.getId();
	}
	
	@Override
	
	public Car getParent() {
		return this.car;
	}
	
	
	@Override
	public void setParent(Car parent) {
		this.setCar(parent);
	}
	
	@Override
	public int compareTo(@NotNull CarProperty o) {
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
		CarProperty that = (CarProperty) o;
		return Objects.equal(key, that.key) && Objects.equal(value, that.value) && Objects.equal(type, that.type);
	}
}
