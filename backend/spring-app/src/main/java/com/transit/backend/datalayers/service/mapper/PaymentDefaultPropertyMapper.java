package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.PaymentDefaultPropertyDTO;
import com.transit.backend.datalayers.domain.PaymentDefaultProperty;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentDefaultPropertyMapper extends AbstractMapper<PaymentDefaultProperty, PaymentDefaultPropertyDTO> {

}