package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.PaymentDTO;
import com.transit.backend.datalayers.domain.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper extends AbstractMapper<Payment, PaymentDTO> {
	
	Payment toEntity(PaymentDTO dto);
	
	PaymentDTO toDto(Payment dto);
}
