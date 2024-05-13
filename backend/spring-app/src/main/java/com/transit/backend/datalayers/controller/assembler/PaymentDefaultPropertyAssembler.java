package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.PaymentDefaultPropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssembler;
import com.transit.backend.datalayers.controller.dto.PaymentDefaultPropertyDTO;
import com.transit.backend.datalayers.domain.PaymentDefaultProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PaymentDefaultPropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

@Component
public class PaymentDefaultPropertyAssembler extends AbstractAssembler<PaymentDefaultProperty, PaymentDefaultPropertyDTO, PaymentDefaultPropertyController> {
	@Autowired
	private PaymentDefaultPropertyMapper packagePropertyMapper;
	
	public PaymentDefaultPropertyAssembler() {
		super(PaymentDefaultPropertyController.class, PaymentDefaultPropertyDTO.class);
	}
	
	@Override
	public PaymentDefaultPropertyDTO toModel(PaymentDefaultProperty entity) {
		return super.toModel(entity);
	}
	
	@Override
	public AbstractMapper<PaymentDefaultProperty, PaymentDefaultPropertyDTO> getMapper() {
		return this.packagePropertyMapper;
	}
	
	@Override
	public CollectionModel<PaymentDefaultPropertyDTO> toCollectionModel(Iterable<? extends PaymentDefaultProperty> entities) {
		return super.toCollectionModel(entities);
		
	}
	
	@Override
	public Class<PaymentDefaultPropertyController> getControllerClass() {
		return PaymentDefaultPropertyController.class;
	}
	
}