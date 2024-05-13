package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.dto.PaymentDTO;
import com.transit.backend.datalayers.controller.dto.PaymentPropertyDTO;
import com.transit.backend.datalayers.domain.Payment;
import com.transit.backend.datalayers.domain.PaymentProperty;
import com.transit.backend.datalayers.domain.QPayment;
import com.transit.backend.datalayers.repository.PaymentPropertyRepository;
import com.transit.backend.datalayers.repository.PaymentRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.PaymentPaymentPropertyService;
import com.transit.backend.datalayers.service.PaymentService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendPropertyAbstract;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PaymentMapper;
import com.transit.backend.datalayers.service.mapper.PaymentPropertyMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;

@Service
public class PaymentServiceBean extends CrudServiceExtendPropertyAbstract<Payment, PaymentDTO, PaymentProperty, PaymentPropertyDTO> implements PaymentService {
	
	@Autowired
	private PaymentPaymentPropertyService paymentPaymentPropertyService;
	@Autowired
	private PaymentPropertyRepository paymentPropertyRepository;
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private PaymentMapper paymentMapper;
	@Autowired
	private PaymentPropertyMapper paymentPropertyMapper;
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public Class<Payment> getEntityClazz() {
		return Payment.class;
	}
	
	@Override
	public Class<PaymentDTO> getEntityDTOClazz() {
		return PaymentDTO.class;
	}
	
	@Override
	public Path<Payment> getQClazz() {
		return QPayment.payment;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	@Override
	public CrudServiceSubRessource<PaymentProperty, UUID, UUID> getPropertySubService() {
		return paymentPaymentPropertyService;
	}
	
	@Override
	public AbstractRepository<PaymentProperty> getPropertyRepository() {
		return this.paymentPropertyRepository;
	}
	
	@Override
	public AbstractMapper<PaymentProperty, PaymentPropertyDTO> getPropertyMapper() {
		return this.paymentPropertyMapper;
	}
	
	@Override
	public AbstractRepository<Payment> getRepository() {
		return paymentRepository;
	}
	
	@Override
	public AbstractMapper<Payment, PaymentDTO> getMapper() {
		return this.paymentMapper;
	}
	
	@Override
	public String getPropertyDeletedString() {
		return "paymentProperties.deleted==false";
	}
	
	@Override
	public String getQueryRewritedString(Matcher m) {
		return QueryRewrite.queryRewritePaymentToPaymentProperties(m);
	}
	
	
	@Override
	public Payment create(Payment entity) {
		entity = super.createInternal(entity);
		return super.saveInternal(entity);
	}
	
	@Override
	public Payment update(UUID primaryKey, Payment entity) {
		var entityOld = paymentRepository.findById(primaryKey);
		if (entityOld.isEmpty()) {
			throw new NoSuchElementFoundException(Payment.class.getSimpleName(), primaryKey);
		}
		entity = super.updateInternal(primaryKey, entity);
		return super.filterPUTPATCHInternal(super.saveInternal(entity));
		
	}
	
	@Override
	public Payment partialUpdate(UUID primaryKey, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		var oldSubOrder = paymentRepository.findById(primaryKey);
		if (oldSubOrder.isEmpty()) {
			throw new NoSuchElementFoundException(Payment.class.getSimpleName(), primaryKey);
		}
		try {
			return super.filterPUTPATCHInternal(partialUpdateIntern(primaryKey, patch));
			
		} catch (Exception e) {
			throw new UnprocessableEntityExeption(e.getMessage());
		}
		
	}
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Payment partialUpdateIntern(UUID primaryKey, JsonMergePatch patch) {
		var entityOld = paymentRepository.findById(primaryKey);
		var orderDTOPatched = super.partialUpdateInternal(primaryKey, patch);
		var patchedOrder = super.checkviolationsInternal(primaryKey, super.partialUpdateSavePropertiesInternal(orderDTOPatched));
		return super.saveInternal(patchedOrder);
	}
	
	@Override
	public Page<Payment> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query);
	}
	
	@Override
	public Optional<Payment> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey);
	}
	
	@Override
	public void delete(UUID primaryKey) {
		super.saveInternal(super.deleteInternal(primaryKey));
	}
}

