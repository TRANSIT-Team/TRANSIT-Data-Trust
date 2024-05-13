package com.transit.backend.datalayers.domain;

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
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "DeliveryMethod")
@Table(name = "deliveryMethods")
@Audited(withModifiedFlag = true)
@Cache(region = "deliveryMethodCache", usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class DeliveryMethod extends AbstractEntity implements Serializable {
	
	
	@Column(name = "deliveryMethodName")
	@Type(type = "text")
	private String deliveryMethodName;
	
	@Override
	public int hashCode() {
		return Objects.hash(deliveryMethodName);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof DeliveryMethod)) return false;
		DeliveryMethod that = (DeliveryMethod) o;
		return Objects.equals(deliveryMethodName, that.deliveryMethodName);
	}
}
