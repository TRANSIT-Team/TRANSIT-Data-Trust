package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.PaymentPropertyDTO;
import com.transit.backend.datalayers.domain.Payment;
import com.transit.backend.datalayers.domain.PaymentProperty;
import com.transit.backend.datalayers.domain.QPaymentProperty;
import com.transit.backend.datalayers.repository.PaymentPropertyRepository;
import com.transit.backend.datalayers.repository.PaymentRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.PaymentPaymentPropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceSubRessourceAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PaymentPropertyMapper;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentPaymentPropertiesServiceBean extends CrudServiceSubRessourceAbstract<PaymentProperty, PaymentPropertyDTO, Payment> implements PaymentPaymentPropertyService {
	
	@Autowired
	private PaymentPropertyRepository paymentPropertyRepository;
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private PaymentPropertyMapper paymentPropertyMapper;
	
	@Override
	public AbstractRepository<PaymentProperty> getPropertyRepository() {
		return paymentPropertyRepository;
	}
	
	@Override
	public AbstractRepository<Payment> getParentRepository() {
		return paymentRepository;
	}
	
	@Override
	public AbstractMapper<PaymentProperty, PaymentPropertyDTO> getPropertyMapper() {
		return paymentPropertyMapper;
	}
	
	@Override
	public Class<PaymentProperty> getPropertyClazz() {
		return PaymentProperty.class;
	}
	
	@Override
	public Class<PaymentPropertyDTO> getPropertyDTOClazz() {
		return PaymentPropertyDTO.class;
	}
	
	@Override
	public Class<Payment> getParentClass() {
		return Payment.class;
	}
	
	@Override
	public Path<PaymentProperty> getPropertyQClazz() {
		return QPaymentProperty.paymentProperty;
	}
	
	@Override
	public String getParentString() {
		return "payment";
	}
	
	@Override
	public PaymentProperty create(UUID uuid, PaymentProperty entity) {
		return super.createInternal(uuid, entity);
	}
	
	@Override
	public PaymentProperty update(UUID uuid, UUID uuid2, PaymentProperty entity) {
		return super.updateInternal(uuid, uuid2, entity);
	}
	
	@Override
	public PaymentProperty partialUpdate(UUID uuid, UUID uuid2, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdateInternal(uuid, uuid2, patch);
	}
	
	@Override
	public Collection<PaymentProperty> read(UUID uuid, String query, FilterExtra FilterExtra) {
		return super.readInternal(uuid, query, FilterExtra);
	}
	
	@Override
	public Optional<PaymentProperty> readOne(UUID uuid, UUID uuid2) {
		return super.readOneInternal(uuid, uuid2);
	}
	
	@Override
	public void delete(UUID uuid, UUID uuid2) {
		super.deleteInternal(uuid, uuid2);
	}
}
