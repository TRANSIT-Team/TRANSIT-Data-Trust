package com.transit.backend.datalayers.controller.assembler;

import com.transit.backend.datalayers.controller.PaymentController;
import com.transit.backend.datalayers.controller.PaymentPaymentPropertyController;
import com.transit.backend.datalayers.controller.assembler.abstractclasses.AbstractAssemblerExtendProperties;
import com.transit.backend.datalayers.controller.assembler.wrapper.PaymentPaymentPropertiesAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.PaymentDTO;
import com.transit.backend.datalayers.controller.dto.PaymentPropertyDTO;
import com.transit.backend.datalayers.domain.Payment;
import com.transit.backend.datalayers.domain.PaymentProperty;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentAssembler extends AbstractAssemblerExtendProperties<Payment, PaymentDTO, PaymentProperty, PaymentPropertyDTO, PaymentPaymentPropertyController, PaymentController> {
	@Autowired
	private PaymentMapper mapper;
	
	@Autowired
	private PaymentPaymentPropertiesAssemblerWrapper paymentPaymentPropertiesAssemblerWrapper;
	
	public PaymentAssembler() {
		super(PaymentController.class, PaymentDTO.class);
	}
	
	@Override
	public AbstractMapper<Payment, PaymentDTO> getMapper() {
		return mapper;
	}
	
	@Override
	public Class<PaymentController> getControllerClass() {
		return PaymentController.class;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<PaymentProperty, PaymentPropertyDTO, Payment, PaymentDTO, PaymentController, PaymentPaymentPropertyController> getSubAssemblerWrapper() {
		return paymentPaymentPropertiesAssemblerWrapper;
	}
}
