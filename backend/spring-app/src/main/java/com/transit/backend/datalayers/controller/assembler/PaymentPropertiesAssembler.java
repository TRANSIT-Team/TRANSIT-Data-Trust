package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.PaymentPaymentPropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerWithoutLink;
import com.transit.backend.datalayers.controller.dto.PaymentPropertyDTO;
import com.transit.backend.datalayers.domain.PaymentProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PaymentPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

@Component
public class PaymentPropertiesAssembler extends AbstractAssemblerWithoutLink<PaymentProperty, PaymentPropertyDTO, PaymentPaymentPropertyController> {
	@Autowired
	private PaymentPropertyMapper mapper;
	
	public PaymentPropertiesAssembler() {
		super(PaymentPaymentPropertyController.class, PaymentPropertyDTO.class);
	}
	
	@Override
	public PaymentPropertyDTO toModel(PaymentProperty entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<PaymentProperty, PaymentPropertyDTO> getMapper() {
		return mapper;
	}
	
	@Override
	public CollectionModel<PaymentPropertyDTO> toCollectionModel(Iterable<? extends PaymentProperty> entities) {
		return super.toCollectionModel(entities);
	}
}
