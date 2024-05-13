package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import com.transit.backend.datalayers.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Payment")
@Table(name = "payments")
@Audited(withModifiedFlag = true)
@Cache(region = "paymentCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Payment extends AbstractParentEntity<PaymentProperty> implements Serializable {
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "paymentStatus")
	private PaymentStatus paymentStatus;
	
	private Double amount;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "payment")
	//@Where(clause = "deleted = false")
	@OrderBy("key ASC")
	private SortedSet<PaymentProperty> paymentProperties;
	
	
	@Override
	public boolean equals(Object o) {
		
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Payment payment = (Payment) o;
		return Objects.equal(paymentStatus, payment.paymentStatus) && Objects.equal(paymentProperties, payment.paymentProperties);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), paymentStatus, paymentProperties);
	}
	
	@Override
	public SortedSet<PaymentProperty> getProperties() {
		return this.paymentProperties;
	}
	
	@Override
	public void setProperties(SortedSet<PaymentProperty> paymentProperties) {
		this.setPaymentProperties(paymentProperties);
	}
	
	public void setPaymentProperties(SortedSet<PaymentProperty> paymentProperties) {
		if (this.paymentProperties == null) {
			this.paymentProperties = new TreeSet<>();
		}
		if (!this.paymentProperties.equals(paymentProperties)) {
			this.paymentProperties.clear();
			if (paymentProperties != null) {
				this.paymentProperties.addAll(paymentProperties);
			}
		}
	}
	
	@Override
	public void addProperty(PaymentProperty paymentProperty) {
		this.addPaymentProperties(paymentProperty);
	}
	
	
	public void addPaymentProperties(PaymentProperty paymentProperty) {
		if (this.paymentProperties == null) {
			this.paymentProperties = new TreeSet<>();
		}
		this.paymentProperties.add(paymentProperty);
		paymentProperty.setPayment(this);
	}
}
