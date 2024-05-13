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

import javax.persistence.*;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Company")
@Table(name = "companies")
@Audited(withModifiedFlag = true)
@Cache(region = "companyCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Company extends AbstractParentEntity<CompanyProperty> implements Serializable {
	
	@Type(type = "text")
	private String name;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "company")
	@OrderBy("address.id ASC")
	//@Transient
	private SortedSet<CompanyAddress> companyAddresses;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "company")
	@OrderBy("id ASC")
	//@Transient
	
	private SortedSet<User> companyUsers;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "company")
	@OrderBy("key ASC")
	private SortedSet<CompanyProperty> companyProperties;
	
	
	@OneToOne(mappedBy = "company", optional = false)
	private CompanyDeliveryArea companyDeliveryArea;
	
	public void addCompanyUser(User companyUser) {
		if (this.companyUsers == null) {
			this.companyUsers = new TreeSet<>();
			
		}
		this.companyUsers.add(companyUser);
		companyUser.setCompany(this);
	}
	
	public void addCompanyAddress(CompanyAddress companyAddress) {
		if (this.companyAddresses == null) {
			this.companyAddresses = new TreeSet<>();
		}
		this.companyAddresses.add(companyAddress);
		companyAddress.setCompany(this);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof Company company)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(name, company.name) && Objects.equal(companyAddresses, company.companyAddresses) && Objects.equal(companyProperties, company.companyProperties);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), name, companyAddresses, companyProperties);
	}
	
	@Override
	public SortedSet<CompanyProperty> getProperties() {
		return this.companyProperties;
	}
	
	@Override
	public void setProperties(SortedSet<CompanyProperty> companyProperties) {
		this.setCompanyProperties(companyProperties);
		
	}
	
	public void setCompanyProperties(SortedSet<CompanyProperty> companyProperties) {
		if (this.companyProperties == null) {
			this.companyProperties = new TreeSet<>();
		}
		if (!this.companyProperties.equals(companyProperties)) {
			this.companyProperties.clear();
			if (companyProperties != null) {
				this.companyProperties.addAll(companyProperties);
			}
		}
	}
	
	@Override
	public void addProperty(CompanyProperty companyProperty) {
		this.addCompanyProperty(companyProperty);
	}
	
	public void addCompanyProperty(CompanyProperty companyProperty) {
		if (this.companyProperties == null) {
			this.companyProperties = new TreeSet<>();
		}
		this.companyProperties.add(companyProperty);
		companyProperty.setCompany(this);
	}
	
	@PreUpdate
	@PrePersist
	private void save() {
		if (this.companyAddresses == null) {
			this.companyAddresses = new TreeSet<>();
		}
		if (this.companyUsers == null) {
			this.companyUsers = new TreeSet<>();
		}
		if (this.companyProperties == null) {
			this.companyProperties = new TreeSet<>();
		}
	}
}
