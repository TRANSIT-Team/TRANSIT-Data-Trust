package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerExtendAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerExtend;
import com.transit.backend.datalayers.controller.assembler.PaymentAssembler;
import com.transit.backend.datalayers.controller.dto.PaymentDTO;
import com.transit.backend.datalayers.domain.Payment;
import com.transit.backend.datalayers.service.PaymentService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PaymentMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.PaymentFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController extends CrudControllerExtendAbstract<Payment, UUID, PaymentDTO> implements CrudControllerExtend<PaymentDTO, UUID> {
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private PaymentAssembler paymentAssembler;
	
	@Autowired
	private PaymentMapper mapper;
	@Autowired
	private PaymentFilter paymentFilter;
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<PaymentDTO> create(@RequestBody @Validated(ValidationGroups.Post.class) PaymentDTO dto) {
		return super.create(dto);
	}
	
	@Override
	public AbstractMapper<Payment, PaymentDTO> getMapper() {
		return mapper;
	}
	
	@Override
	public CrudServiceExtend<Payment, UUID> getService() {
		return paymentService;
	}
	
	@Override
	public RepresentationModelAssemblerSupport<Payment, PaymentDTO> getAssemblerSupport() {
		return paymentAssembler;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<PaymentDTO> update(@PathVariable("id") UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) PaymentDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<Payment, ?> getFilterHelper() {
		return paymentFilter;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<PaymentDTO> partialUpdate(@PathVariable("id") UUID primaryKey, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	@Override
	public Class<PaymentDTO> getClazz() {
		return PaymentDTO.class;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<PaymentDTO> readOne(@PathVariable("id") UUID primaryKey) {
		return super.readOne(primaryKey);
		
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity delete(@PathVariable("id") UUID primaryKey) {
		return super.delete(primaryKey);
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<PagedModel<PaymentDTO>> read(Pageable pageable, @RequestParam(name = "filter", defaultValue = "deleted==false;paymentProperties.deleted==false") String query,
	                                                   @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                   @RequestParam(name = "take", defaultValue = "0") int take,
	                                                   @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters) {
		return super.read(GetFilterExtra.getPageableExtras(pageable, skip, take, extraFilterParameters), query, createdByMyCompany);
	}
	
	
}
