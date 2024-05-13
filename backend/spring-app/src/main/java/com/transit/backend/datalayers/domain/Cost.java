package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Cost")
@Table(name = "cost")
@Audited(withModifiedFlag = true)
public class Cost extends AbstractParentEntity<CostProperty> implements Serializable {
	
	
	private Double costSum;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "cost")
	//@Where(clause = "deleted = false")
	@OrderBy("key ASC")
	private SortedSet<CostProperty> costProperties;
	
	
	@Override
	public boolean equals(Object o) {
		
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Cost cost = (Cost) o;
		return Objects.equal(costProperties, cost.costProperties);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), costProperties);
	}
	
	@Override
	public SortedSet<CostProperty> getProperties() {
		return this.costProperties;
	}
	
	@Override
	public void setProperties(SortedSet<CostProperty> costProperties) {
		this.setCostProperties(costProperties);
	}
	
	public void setCostProperties(SortedSet<CostProperty> costProperties) {
		if (this.costProperties == null) {
			this.costProperties = new TreeSet<>();
		}
		if (!this.costProperties.equals(costProperties)) {
			this.costProperties.clear();
			if (costProperties != null) {
				this.costProperties.addAll(costProperties);
			}
		}
	}
	
	@Override
	public void addProperty(CostProperty costProperty) {
		this.addCostProperties(costProperty);
	}
	
	
	public void addCostProperties(CostProperty costProperty) {
		if (this.costProperties == null) {
			this.costProperties = new TreeSet<>();
		}
		this.costProperties.add(costProperty);
		costProperty.setCost(this);
	}
}
