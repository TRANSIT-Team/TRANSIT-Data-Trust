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
@Entity(name = "PaymentProperty")
@Table(name = "paymentProperties")
@Audited(withModifiedFlag = true)
@Cache(region = "paymentPropertyCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class PaymentProperty extends AbstractPropertyEntity<Payment> implements Serializable, Comparable<PaymentProperty> {
	
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
	private Payment payment;
	
	
	@Override
	
	public UUID getParentId() {
		return payment.getId();
	}
	
	@Override
	
	public Payment getParent() {
		return payment;
	}
	
	@Override
	public void setParent(Payment parent) {
		this.payment = parent;
	}
	
	
	@Override
	public int compareTo(@NotNull PaymentProperty o) {
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
		PaymentProperty that = (PaymentProperty) o;
		return Objects.equal(key, that.key) && Objects.equal(value, that.value) && Objects.equal(type, that.type);
	}
}
