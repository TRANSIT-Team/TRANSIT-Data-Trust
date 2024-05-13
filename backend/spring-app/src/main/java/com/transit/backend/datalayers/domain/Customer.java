package com.transit.backend.datalayers.domain;

import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Customer")
@Table(name = "customers")
@Audited(withModifiedFlag = true)
@Cache(region = "customerCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Customer extends AbstractEntity implements Comparable<Customer>, Serializable, Cloneable {
	
	@NotBlank
	private String name;
	@NotBlank
	private String email;
	@NotBlank
	private String tel;
	@NotNull
	private UUID companyId;
	@NotNull
	private UUID addressId;
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Customer customer)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(name, customer.name) && Objects.equals(email, customer.email) && Objects.equals(tel, customer.tel) && Objects.equals(companyId, customer.companyId) && Objects.equals(addressId, customer.addressId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), name, email, tel, companyId, addressId);
	}
	
	@Override
	public Customer clone() {
		Customer clone = new Customer();
		clone.setName(this.name);
		clone.setTel(this.tel);
		clone.setEmail(this.email);
		clone.setCompanyId(this.companyId);
		clone.setAddressId(this.addressId);
		clone.setCreatedBy(this.getCreatedBy());
		clone.setLastModifiedBy(this.getLastModifiedBy());
		return clone;
	}
	
	@Override
	public int compareTo(@NotNull Customer o) {
		return this.getId().compareTo(o.getId());
	}
}
