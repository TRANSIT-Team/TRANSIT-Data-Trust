package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "PackageClass")
@Table(name = "packageClasses")
@Audited(withModifiedFlag = true)
@Cache(region = "packageClassCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class PackageClass extends AbstractEntity implements Serializable {
	
	
	@Column(name = "className")
	@Type(type = "text")
	private String name;
	
	@OneToMany(mappedBy = "packageClass")
	
	private Set<PackageItem> packageItems;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), name);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof PackageClass that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(name, that.name);
	}
}
