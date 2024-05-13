package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerSubRessourceNToMAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.CompanyCompanyCustomerAssemblerWrapper;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.CompanyDTO;
import com.transit.backend.datalayers.controller.dto.CustomerDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.Customer;
import com.transit.backend.datalayers.service.CompanyCustomerService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CustomerMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.CustomerFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/customers")
public class CompanyCustomerController extends CrudControllerSubRessourceNToMAbstract<Customer, CustomerDTO, Company, CompanyDTO, CompanyController, CompanyCustomerController> implements CrudControllerSubResource<CustomerDTO, UUID, UUID> {
	
	@Autowired
	private CompanyCustomerService service;
	@Autowired
	private CustomerMapper mapper;
	@Autowired
	private CompanyCompanyCustomerAssemblerWrapper companyCustomerAssemblerWrapper;
	@Autowired
	private CustomerFilter customerFilter;
	
	@PostMapping
	@PreAuthorize(
			"@securityCompanyServiceOneLess.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<CustomerDTO> create(@PathVariable UUID companyId, @RequestBody @Validated(ValidationGroups.Post.class) CustomerDTO dto) {
		return super.create(companyId, dto);
	}
	
	@Override
	public AbstractMapper<Customer, CustomerDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceSubRessource<Customer, UUID, UUID> getService() {
		return this.service;
	}
	
	@Override
	public AssemblerWrapperAbstract<Customer, CustomerDTO, Company, CompanyDTO, CompanyController, CompanyCustomerController> getAssemblerWrapper() {
		return this.companyCustomerAssemblerWrapper;
	}
	
	@PutMapping("/{customerId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	
	public ResponseEntity<CustomerDTO> update(
			@PathVariable UUID companyId, @PathVariable UUID customerId, @RequestBody @Validated(ValidationGroups.Put.class) CustomerDTO dto) throws ClassNotFoundException {
		return super.update(companyId, customerId, dto);
	}
	
	@Override
	public boolean getFilter() {
		//Will be automatically filtered with query in read method
		return false;
	}
	
	@Override
	
	public EntityFilterHelper<Customer, ?> getFilterHelper() {
		return this.customerFilter;
		
	}
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{customerId}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	
	public ResponseEntity<CustomerDTO> partialUpdate(@PathVariable UUID companyId, @PathVariable UUID customerId, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(companyId, customerId, patch);
	}
	
	@Override
	public Class<Customer> getCLazz() {
		return Customer.class;
	}
	
	@GetMapping("/{customerId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	
	public ResponseEntity<CustomerDTO> readOne(@PathVariable UUID companyId, @PathVariable UUID customerId) {
		return super.readOne(companyId, customerId);
	}
	
	@DeleteMapping("/{customerId}")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity delete(@PathVariable UUID companyId, @PathVariable UUID customerId) {
		return super.delete(companyId, customerId);
	}
	
	@GetMapping
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<CollectionModel<CustomerDTO>> read(@PathVariable UUID companyId,
	                                                         @RequestParam(name = "filter", defaultValue = "deleted==false") String query,
	                                                         @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                         @RequestParam(name = "take", defaultValue = "0") int take,
	                                                         @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters,
	                                                         @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany
	) {
		return super.read(companyId, query, GetFilterExtra.getCollectionFilterExtra(skip, take, extraFilterParameters, createdByMyCompany));
	}
	
	
}
		