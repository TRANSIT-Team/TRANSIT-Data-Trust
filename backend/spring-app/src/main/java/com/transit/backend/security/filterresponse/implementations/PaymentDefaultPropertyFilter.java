package com.transit.backend.security.filterresponse.implementations;


import com.transit.backend.datalayers.domain.PaymentDefaultProperty;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentDefaultPropertyFilter extends AbstractEntityFilter<PaymentDefaultProperty, PaymentDefaultProperty> implements EntityFilterHelper<PaymentDefaultProperty, PaymentDefaultProperty> {
	
	@Override
	public PaymentDefaultProperty transformToTransfer(PaymentDefaultProperty entity) {
		return entity;
	}
	
	@Override
	public PaymentDefaultProperty filterEntities(PaymentDefaultProperty entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		//Company wird nicht mit übertragen
		return entity;
	}
	
	@Override
	public PaymentDefaultProperty transformToEntity(PaymentDefaultProperty entity) {
		return entity;
	}
	
	@Override
	public PaymentDefaultProperty transformToTransfer(PaymentDefaultProperty entity, PaymentDefaultProperty entityOld) {
		return entity;
	}
	
	@Override
	public Class<PaymentDefaultProperty> getClazz() {
		return PaymentDefaultProperty.class;
	}
	
	@Override
	public String getPathToEntity(PaymentDefaultProperty entity, PaymentDefaultProperty entity2) {
		return "/paymentproperties/" + entity.getId();
	}
}
