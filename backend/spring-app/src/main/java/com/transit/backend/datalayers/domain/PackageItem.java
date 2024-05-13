package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
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
import java.util.SortedSet;
import java.util.TreeSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "PackageItem")
@Table(name = "packageItems")
@Audited(withModifiedFlag = true)
@Cache(region = "packageItemCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class PackageItem extends AbstractParentEntity<PackagePackageProperty> implements Serializable, Comparable<PackageItem> {
	
	
	private double weightKg;
	private double heightCm;
	private double widthCm;
	private double deepCm;
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean frost;
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean explosive;
	
	@Type(type = "text")
	private String comment;
	
	@Column(nullable = false, columnDefinition = "double precision ")
	private double packagePrice;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "packageItem")
	//@Where(clause = "deleted = false")
	@OrderBy("key ASC")
	private SortedSet<PackagePackageProperty> packagePackageProperties;
	
	@ManyToOne
	private PackageClass packageClass;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), weightKg, heightCm, widthCm, deepCm, frost, explosive, comment, packagePackageProperties, packageClass);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof PackageItem that)) return false;
		if (!super.equals(o)) return false;
		return Double.compare(that.weightKg, weightKg) == 0 && Double.compare(that.heightCm, heightCm) == 0 && Double.compare(that.widthCm, widthCm) == 0 && Double.compare(that.deepCm, deepCm) == 0 && frost == that.frost && explosive == that.explosive && Objects.equal(comment, that.comment) && Objects.equal(packagePackageProperties, that.packagePackageProperties) && Objects.equal(packageClass, that.packageClass);
	}
	
	@Override
	public SortedSet<PackagePackageProperty> getProperties() {
		return this.packagePackageProperties;
	}
	
	@Override
	public void setProperties(SortedSet<PackagePackageProperty> packagePackageProperties) {
		this.setPackagePackageProperties(packagePackageProperties);
	}
	
	public void setPackagePackageProperties(SortedSet<PackagePackageProperty> packagePackageProperties) {
		if (this.packagePackageProperties == null) {
			this.packagePackageProperties = new TreeSet<>();
		}
		if (!this.packagePackageProperties.equals(packagePackageProperties)) {
			this.packagePackageProperties.clear();
			if (packagePackageProperties != null) {
				this.packagePackageProperties.addAll(packagePackageProperties);
			}
		}
	}
	
	@Override
	public void addProperty(PackagePackageProperty packagePackageProperty) {
		this.addPackagePackageProperties(packagePackageProperty);
	}
	
	public void addPackagePackageProperties(PackagePackageProperty packagePackageProperty) {
		if (this.packagePackageProperties == null) {
			this.packagePackageProperties = new TreeSet<>();
		}
		this.packagePackageProperties.add(packagePackageProperty);
		packagePackageProperty.setPackageItem(this);
	}
	
	@PrePersist
	@PreUpdate
	private void save() {
		if (this.packagePackageProperties == null) {
			this.packagePackageProperties = new TreeSet<>();
		}
	}
	
	@Override
	public int compareTo(@NotNull PackageItem o) {
		return this.getId().compareTo(o.getId());
	}
	
	public PackageItem copyPackageItem() {
		var packageItem = new PackageItem();
		packageItem.setPackageClass(this.packageClass);
		packageItem.setWeightKg(this.weightKg);
		packageItem.setHeightCm(this.heightCm);
		packageItem.setDeepCm(this.deepCm);
		packageItem.setWidthCm(this.widthCm);
		packageItem.setFrost(this.isFrost());
		packageItem.setExplosive(this.isExplosive());
		packageItem.setComment(this.comment);
		packageItem.setPackagePrice(this.packagePrice);
		
		var packageProperties = new TreeSet<PackagePackageProperty>();
		this.packagePackageProperties.forEach(ppp -> packageProperties.add(ppp.copyProperty()));
		packageItem.setPackagePackageProperties(packageProperties);
		return packageItem;
		
	}
}
