package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.PaymentDefaultPropertyDTO;
import com.transit.backend.datalayers.domain.PaymentDefaultProperty;
import com.transit.backend.datalayers.domain.QPaymentDefaultProperty;
import com.transit.backend.datalayers.repository.PaymentDefaultPropertyRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.PaymentDefaultPropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendAbstract;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PaymentDefaultPropertyMapper;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.Validator;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentDefaultPropertyServiceBean extends CrudServiceExtendAbstract<PaymentDefaultProperty, PaymentDefaultPropertyDTO> implements PaymentDefaultPropertyService {
	@Inject
	Validator validator;
	@Autowired
	private PaymentDefaultPropertyRepository repository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PaymentDefaultPropertyMapper mapper;

	@Override
	public PaymentDefaultProperty create(PaymentDefaultProperty entity) {
		return super.saveInternal(super.createInternal(entity));
	}
	
	@Override
	public PaymentDefaultProperty update(UUID primaryKey, PaymentDefaultProperty entity) {
		return super.saveInternal(super.updateInternal(primaryKey, entity));
	}
	
	@Override
	public PaymentDefaultProperty partialUpdate(UUID primaryKey, JsonMergePatch patch) {
		return super.saveInternal(super.checkviolationsInternal(primaryKey, super.partialUpdateInternal(primaryKey, patch)));
		
		
	}
	
	@Override
	public Page<PaymentDefaultProperty> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<PaymentDefaultProperty> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(super.deleteInternal(primaryKey));
	}
	
	@Override
	public AbstractRepository<PaymentDefaultProperty> getRepository() {
		return this.repository;
	}
	
	@Override
	public AbstractMapper<PaymentDefaultProperty, PaymentDefaultPropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public Class<PaymentDefaultProperty> getEntityClazz() {
		return PaymentDefaultProperty.class;
	}
	
	@Override
	public Class<PaymentDefaultPropertyDTO> getEntityDTOClazz() {
		return PaymentDefaultPropertyDTO.class;
	}
	
	@Override
	public Path<PaymentDefaultProperty> getQClazz() {
		return QPaymentDefaultProperty.paymentDefaultProperty;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	
}