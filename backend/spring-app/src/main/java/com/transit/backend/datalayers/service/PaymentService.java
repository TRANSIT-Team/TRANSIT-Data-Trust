package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.Payment;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;

import java.util.UUID;

public interface PaymentService extends CrudServiceExtend<Payment, UUID> {
}
