package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntityRelation;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "CompanyAddress")
@Table(name = "companyAddresses")
@Audited(withModifiedFlag = true)
@Cache(region = "companyAddressCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CompanyAddress extends AbstractEntityRelation implements Serializable, Comparable<CompanyAddress> {
	
	@EmbeddedId
	private CompanyAddressId id;
	
	@ManyToOne
	@MapsId("addressId")
	private Address address;
	
	@ManyToOne
	@MapsId("companyId")
	
	private Company company;
	
	
	private String addressType;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), id, address, addressType);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		CompanyAddress that = (CompanyAddress) o;
		return Objects.equal(id, that.id) && Objects.equal(address, that.address) && Objects.equal(addressType, that.addressType);
	}
	
	@Override
	public int compareTo(@NotNull CompanyAddress o) {
		return this.id.compareTo(o.getId());
	}
}
