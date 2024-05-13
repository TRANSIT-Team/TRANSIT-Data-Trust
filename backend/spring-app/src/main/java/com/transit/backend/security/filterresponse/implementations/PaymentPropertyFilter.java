package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Payment;
import com.transit.backend.datalayers.domain.PaymentProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

@Component
public class PaymentPropertyFilter extends AbstractPropertyEntityFilter<PaymentProperty, PaymentProperty, Payment, Payment> implements EntityFilterHelper<PaymentProperty, PaymentProperty> {
	
	@Override
	public PaymentProperty transformToTransfer(PaymentProperty entity) {
		return entity;
	}
	
	@Override
	public PaymentProperty transformToEntity(PaymentProperty entity) {
		return entity;
	}
	
	@Override
	public PaymentProperty transformToTransfer(PaymentProperty entity, PaymentProperty entityOld) {
		return entity;
	}
	
	@Override
	public Class<PaymentProperty> getClazz() {
		return PaymentProperty.class;
	}
	
	@Override
	public String getPathToEntity(PaymentProperty entity, PaymentProperty entity2) {
		return "/payments/" + entity.getPayment().getId() + "/paymentproperties/" + entity.getId();
	}
}
