package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.PaymentPaymentPropertiesAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperSubAbstract;
import com.transit.backend.datalayers.controller.dto.PaymentDTO;
import com.transit.backend.datalayers.controller.dto.PaymentPropertyDTO;
import com.transit.backend.datalayers.domain.Payment;
import com.transit.backend.datalayers.domain.PaymentProperty;
import com.transit.backend.datalayers.service.PaymentPaymentPropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.PaymentPropertyMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.PaymentPropertyFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments/{paymentId}/paymentproperties")
public class PaymentPaymentPropertyController extends CrudControllerSubRessourceAbstract<PaymentProperty, PaymentPropertyDTO, Payment, PaymentDTO, PaymentController, PaymentPaymentPropertyController> implements CrudControllerSubResource<PaymentPropertyDTO, UUID, UUID> {
	@Autowired
	private PaymentPropertyFilter paymentPropertyFilter;
	
	@Autowired
	private PaymentPaymentPropertiesAssemblerWrapper paymentPaymentPropertiesAssemblerWrapper;
	
	@Autowired
	private PaymentPaymentPropertyService paymentPaymentPropertyService;
	
	@Autowired
	private PaymentPropertyMapper mapper;
	
	@PostMapping
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					
					")"
	)
	public ResponseEntity<PaymentPropertyDTO> create(@PathVariable UUID paymentId, @RequestBody @Validated(ValidationGroups.Post.class) PaymentPropertyDTO dto) {
		return super.create(paymentId, dto);
	}
	
	@Override
	public AbstractMapper<PaymentProperty, PaymentPropertyDTO> getMapper() {
		return mapper;
	}
	
	@Override
	public CrudServiceSubRessource<PaymentProperty, UUID, UUID> getService() {
		return paymentPaymentPropertyService;
	}
	
	@Override
	public AssemblerWrapperSubAbstract<PaymentProperty, PaymentPropertyDTO, Payment, PaymentDTO, PaymentController, PaymentPaymentPropertyController> getAssemblerWrapper() {
		return paymentPaymentPropertiesAssemblerWrapper;
	}
	
	@PutMapping("/{paymentPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<PaymentPropertyDTO> update(
			@PathVariable UUID paymentId, @PathVariable UUID paymentPropertyId, @RequestBody @Validated(ValidationGroups.Put.class) PaymentPropertyDTO dto) throws ClassNotFoundException {
		return super.update(paymentId, paymentPropertyId, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<PaymentProperty, ?> getFilterHelper() {
		return paymentPropertyFilter;
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{paymentPropertyId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<PaymentPropertyDTO> partialUpdate(@PathVariable UUID paymentId, @PathVariable UUID paymentPropertyId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(paymentId, paymentPropertyId, patch);
	}
	
	@GetMapping("/{paymentPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).MANAGER_WAREHOUSE.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).WORKER_WAREHOUSE.stringValue" +
					")"
	)
	public ResponseEntity<PaymentPropertyDTO> readOne(@PathVariable UUID paymentId, @PathVariable UUID paymentPropertyId) {
		return super.readOne(paymentId, paymentPropertyId);
	}
	
	@DeleteMapping("/{paymentPropertyId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority()"
	)
	public ResponseEntity delete(@PathVariable UUID paymentId, @PathVariable UUID paymentPropertyId) {
		return super.delete(paymentId, paymentPropertyId);
	}
	
	@GetMapping
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).MANAGER_WAREHOUSE.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).WORKER_WAREHOUSE.stringValue" +
					")"
	)
	public ResponseEntity<CollectionModel<PaymentPropertyDTO>> read(@PathVariable UUID paymentId, @RequestParam(name = "filter", defaultValue = "deleted==false") String query, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                                @RequestParam(name = "take", defaultValue = "0") int take,
	                                                                @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                                @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany) {
		return super.read(paymentId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
}
