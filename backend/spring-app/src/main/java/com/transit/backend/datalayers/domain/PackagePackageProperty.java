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
@Entity(name = "PackagePackageProperty")
@Table(name = "packagePackageProperties")
@Audited(withModifiedFlag = true)
@Cache(region = "packagePackagePropertyCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class PackagePackageProperty extends AbstractPropertyEntity<PackageItem> implements Serializable, Comparable<PackagePackageProperty> {
	
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
	
	private PackageItem packageItem;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), key, value, type);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof PackagePackageProperty that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(key, that.key) && Objects.equal(value, that.value) && Objects.equal(type, that.type);
	}
	
	@Override
	public int compareTo(@NotNull PackagePackageProperty o) {
		if (this.key == null || o.getKey() == null) {
			if (this.getId() == null || o.getId() == null) {
				return -1;
			}
			return this.getId().compareTo(o.getId());
		}
		return this.key.compareTo(o.getKey());
	}
	
	@Override
	
	public UUID getParentId() {
		return packageItem.getId();
	}
	
	@Override
	
	public PackageItem getParent() {
		return this.packageItem;
	}
	
	
	@Override
	public void setParent(PackageItem parent) {
		this.setPackageItem(parent);
	}
	
	public PackagePackageProperty copyProperty() {
		var property = new PackagePackageProperty();
		property.setKey(this.getKey());
		property.setType(this.getType());
		property.setValue(this.getValue());
		return property;
	}
}
