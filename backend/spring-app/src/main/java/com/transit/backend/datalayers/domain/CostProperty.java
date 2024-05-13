package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractPropertyEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Entity(name = "CostProperty")
@Table(name = "costProperties")
@Audited(withModifiedFlag = true)
public class CostProperty extends AbstractPropertyEntity<Cost> implements Serializable, Comparable<CostProperty> {
	
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
	private Cost cost;
	
	
	@Override
	
	public UUID getParentId() {
		return cost.getId();
	}
	
	@Override
	
	public Cost getParent() {
		return cost;
	}
	
	@Override
	public void setParent(Cost parent) {
		this.cost = parent;
	}
	
	
	@Override
	public int compareTo(@NotNull CostProperty o) {
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
		CostProperty that = (CostProperty) o;
		return Objects.equal(key, that.key) && Objects.equal(value, that.value) && Objects.equal(type, that.type);
	}
}
