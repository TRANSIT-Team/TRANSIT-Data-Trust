package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerExtendAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerExtend;
import com.transit.backend.datalayers.controller.assembler.AddressAssembler;
import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.service.AddressService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.AddressMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.AddressFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/addresses")
public class AddressController extends CrudControllerExtendAbstract<Address, UUID, AddressDTO> implements CrudControllerExtend<AddressDTO, UUID> {
	@Autowired
	private AddressFilter addressFilter;
	@Autowired
	private AddressService service;
	@Autowired
	private AddressMapper mapper;
	@Autowired
	private AddressAssembler addressAssembler;
	@Autowired
	private PagedResourcesAssembler<Address> pagedResourcesAssembler;

	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<AddressDTO> create(@RequestBody @Validated(ValidationGroups.Post.class) AddressDTO dto) {
		
		return super.create(dto);
	}

	@Override
	public AbstractMapper<Address, AddressDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceExtend<Address, UUID> getService() {
		return this.service;
	}
	
	@Override
	public RepresentationModelAssemblerSupport<Address, AddressDTO> getAssemblerSupport() {
		return this.addressAssembler;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<AddressDTO> update(@PathVariable("id") UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) AddressDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<Address, Address> getFilterHelper() {
		return this.addressFilter;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<AddressDTO> partialUpdate(@PathVariable("id") UUID primaryKey, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	@Override
	public Class<AddressDTO> getClazz() {
		return AddressDTO.class;
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

//	@PreAuthorize(
//			"@securityEntityService.hasAnyAuthority("+
//					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
//					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue,"+
//					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
//					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue"+
//					")"
//	)
	public ResponseEntity<AddressDTO> readOne(@PathVariable("id") UUID primaryKey) {
		return super.readOne(primaryKey);
		
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue" +
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
/*	@PreAuthorize(
			"@securityEntityServiceSimple.hasAnyAuthority("+
			"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
			"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue,"+
			"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
			"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue"+
			")"
	)*/
	public ResponseEntity<PagedModel<AddressDTO>> read(Pageable pageable,
	                                                   @RequestParam(name = "filter", defaultValue = "deleted==false") String query,
	                                                   @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany,
	                                                   @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                   @RequestParam(name = "take", defaultValue = "0") int take,
	                                                   @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters) {
		
		return super.read(GetFilterExtra.getPageableExtras(pageable, skip, take, extraFilterParameters), query, createdByMyCompany);
		
	}
	
}