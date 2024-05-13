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
@Entity(name = "CompanyProperty")
@Table(name = "companyProperties")
@Audited(withModifiedFlag = true)
@Cache(region = "companyPropertyCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CompanyProperty extends AbstractPropertyEntity<Company> implements Serializable, Comparable<CompanyProperty> {
	@Type(type = "text")
	private String key;
	@Type(type = "text")
	private String value;
	@Type(type = "text")
	private String type;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	
	private Company company;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), key, value, type);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof CompanyProperty that)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(key, that.key) && Objects.equal(value, that.value) && Objects.equal(type, that.type);
	}
	
	@Override
	
	public UUID getParentId() {
		return this.company.getId();
	}
	
	@Override
	
	public Company getParent() {
		return this.company;
	}
	
	@Override
	public void setParent(Company parent) {
		this.setCompany(parent);
	}
	
	
	@Override
	public int compareTo(@NotNull CompanyProperty o) {
		if (this.key == null || o.getKey() == null) {
			if (this.getId() == null || o.getId() == null) {
				return -1;
			}
			return this.getId().compareTo(o.getId());
		}
		return this.key.compareTo(o.getKey());
		
	}
}
