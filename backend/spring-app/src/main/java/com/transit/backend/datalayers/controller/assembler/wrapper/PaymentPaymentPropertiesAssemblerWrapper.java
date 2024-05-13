package com.transit.backend.datalayers.controller.assembler.wrapper;

import com.transit.backend.datalayers.controller.PaymentController;
import com.transit.backend.datalayers.controller.PaymentPaymentPropertyController;
import com.transit.backend.datalayers.controller.assembler.PaymentPropertiesAssembler;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.PaymentDTO;
import com.transit.backend.datalayers.controller.dto.PaymentPropertyDTO;
import com.transit.backend.datalayers.domain.Payment;
import com.transit.backend.datalayers.domain.PaymentProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentPaymentPropertiesAssemblerWrapper extends AssemblerWrapperSubAbstract<PaymentProperty, PaymentPropertyDTO, Payment, PaymentDTO, PaymentController, PaymentPaymentPropertyController> {
	
	@Autowired
	private PaymentPropertiesAssembler paymentPropertiesAssembler;
	
	public PaymentPropertyDTO toModel(PaymentProperty entity, UUID packageItemId, boolean backwardLink) {
		return super.toModel(entity, packageItemId, backwardLink);
	}
	
	@Override
	public RepresentationModelAssemblerSupport<PaymentProperty, PaymentPropertyDTO> getAssemblerSupport() {
		return paymentPropertiesAssembler;
	}
	
	public PaymentPropertyDTO addLinks(PaymentPropertyDTO dto, UUID packageItemId, boolean backwardLink) {
		return super.addLinks(dto, packageItemId, backwardLink);
	}
	
	@Override
	public Class<PaymentPaymentPropertyController> getNestedControllerClazz() {
		return PaymentPaymentPropertyController.class;
	}
	
	@Override
	public Class<PaymentController> getRootControllerClazz() {
		return PaymentController.class;
	}
	
	@Override
	public Class<Payment> getDomainNameClazz() {
		return Payment.class;
	}
	
	public CollectionModel<PaymentPropertyDTO> toCollectionModel(Iterable<? extends PaymentProperty> entities, UUID packageItemId, boolean backwardLink) {
		return super.toCollectionModel(entities, packageItemId, backwardLink);
	}
}
