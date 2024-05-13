package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.enums.ShowOverview;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Address")
@Table(name = "addresses")
@Audited(withModifiedFlag = true)
@Cache(region = "addressCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Address extends Location implements Serializable, Cloneable {
	
	
	private String street;
	private String zip;
	private String city;
	private String state;
	
	private String country;
	
	private String isoCode;
	private String addressExtra;
	
	private String companyName;
	
	private String clientName;
	
	private String phoneNumber;
	
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(255) DEFAULT 'SHOW'")
	private ShowOverview showOverviewFilter;
	
	@PrePersist
	public void prePersist() {
		if (showOverviewFilter == null) {
			this.showOverviewFilter = ShowOverview.SHOW;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof Address address)) return false;
		if (!super.equals(o)) return false;
		return Objects.equal(street, address.street) && Objects.equal(zip, address.zip) && Objects.equal(city, address.city) && Objects.equal(state, address.state) && Objects.equal(country, address.country) && Objects.equal(isoCode, address.isoCode) && Objects.equal(addressExtra, address.addressExtra) && Objects.equal(companyName, address.companyName) && Objects.equal(clientName, address.clientName);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), street, zip, city, state, country, isoCode, addressExtra, companyName, clientName);
	}
	
	@Override
	public Address clone() {
		Address clone = new Address();
		clone.setStreet(this.street);
		clone.setZip(this.zip);
		clone.setCity(this.city);
		clone.setState(this.state);
		clone.setCountry(this.country);
		clone.setIsoCode(this.isoCode);
		clone.setAddressExtra(this.addressExtra);
		clone.setCompanyName(this.companyName);
		clone.setClientName(this.clientName);
		clone.setPhoneNumber(this.phoneNumber);
		clone.setLocationPoint(this.getLocationPoint());
		clone.setCreatedBy(this.getCreatedBy());
		clone.setLastModifiedBy(this.getLastModifiedBy());
		return clone;
	}
}
