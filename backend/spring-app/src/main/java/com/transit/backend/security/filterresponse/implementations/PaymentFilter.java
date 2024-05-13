package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.Payment;
import com.transit.backend.datalayers.domain.PaymentProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractParentEntityFilter;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentFilter extends AbstractParentEntityFilter<Payment, Payment, PaymentProperty, PaymentProperty> implements EntityFilterHelper<Payment, Payment> {
	@Autowired
	private PaymentPropertyFilter paymentPropertyFilter;
	
	@Override
	public Payment transformToTransfer(Payment entity) {
		return entity;
	}
	
	@Override
	public Payment transformToEntity(Payment entity) {
		return entity;
	}
	
	@Override
	public Payment transformToTransfer(Payment entity, Payment entityOld) {
		return entity;
	}
	
	@Override
	public Class<Payment> getClazz() {
		return Payment.class;
	}
	
	@Override
	public String getPathToEntity(Payment entity, Payment entity2) {
		{
			return "/payments/" + entity.getId();
		}
	}
	
	@Override
	public AbstractPropertyEntityFilter<PaymentProperty, PaymentProperty, Payment, Payment> getPropertyFilter() {
		return paymentPropertyFilter;
	}
}
