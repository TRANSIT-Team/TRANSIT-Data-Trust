package com.transit.backend.datalayers.service;

import com.transit.backend.datalayers.domain.PaymentProperty;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;

import java.util.UUID;

public interface PaymentPaymentPropertyService extends CrudServiceSubRessource<PaymentProperty, UUID, UUID> {
}
