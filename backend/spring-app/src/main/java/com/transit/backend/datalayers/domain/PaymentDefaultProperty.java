package com.transit.backend.datalayers.domain;

import com.google.common.base.Objects;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "PaymentDefaultProperty")
@Table(name = "paymentDefaultProperties")
@Audited(withModifiedFlag = true)
public class PaymentDefaultProperty extends AbstractEntity implements Serializable {
	
	@Column(name = "key")
	@Type(type = "text")
	private String key;
	
	
	@Column(name = "defaultValue")
	@Type(type = "text")
	private String defaultValue;
	
	@Column(name = "type")
	@Type(type = "text")
	private String type;
	
	@ManyToOne
	private Company company;
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), key, defaultValue, type, company);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof PaymentDefaultProperty)) return false;
		if (!super.equals(o)) return false;
		PaymentDefaultProperty that = (PaymentDefaultProperty) o;
		return Objects.equal(key, that.key) && Objects.equal(defaultValue, that.defaultValue) && Objects.equal(type, that.type) && Objects.equal(company, that.company);
	}
}
