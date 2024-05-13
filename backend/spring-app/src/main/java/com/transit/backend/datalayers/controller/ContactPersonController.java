package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerExtendAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerExtend;
import com.transit.backend.datalayers.controller.assembler.ContactPersonAssembler;
import com.transit.backend.datalayers.controller.dto.ContactPersonDTO;
import com.transit.backend.datalayers.domain.ContactPerson;
import com.transit.backend.datalayers.service.ContactPersonService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.ContactPersonMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.ContactPersonFilter;
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
@RequestMapping("/contactpersons")
public class ContactPersonController extends CrudControllerExtendAbstract<ContactPerson, UUID, ContactPersonDTO> implements CrudControllerExtend<ContactPersonDTO, UUID> {
	@Autowired
	private ContactPersonFilter contactPersonFilter;
	@Autowired
	private ContactPersonService service;
	@Autowired
	private ContactPersonMapper mapper;
	@Autowired
	private ContactPersonAssembler contactPersonAssembler;
	@Autowired
	private PagedResourcesAssembler<ContactPerson> pagedResourcesAssembler;

	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<ContactPersonDTO> create(@RequestBody @Validated(ValidationGroups.Post.class) ContactPersonDTO dto) {
		
		return super.create(dto);
	}

	@Override
	public AbstractMapper<ContactPerson, ContactPersonDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceExtend<ContactPerson, UUID> getService() {
		return this.service;
	}
	
	@Override
	public RepresentationModelAssemblerSupport<ContactPerson, ContactPersonDTO> getAssemblerSupport() {
		return this.contactPersonAssembler;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<ContactPersonDTO> update(@PathVariable("id") UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) ContactPersonDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<ContactPerson, ContactPerson> getFilterHelper() {
		return this.contactPersonFilter;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<ContactPersonDTO> partialUpdate(@PathVariable("id") UUID primaryKey, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	@Override
	public Class<ContactPersonDTO> getClazz() {
		return ContactPersonDTO.class;
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
	public ResponseEntity<ContactPersonDTO> readOne(@PathVariable("id") UUID primaryKey) {
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
	public ResponseEntity<PagedModel<ContactPersonDTO>> read(Pageable pageable,
	                                                         @RequestParam(name = "filter", defaultValue = "deleted==false") String query,
	                                                         @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                         @RequestParam(name = "take", defaultValue = "0") int take,
	                                                         @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters) {
		return super.read(GetFilterExtra.getPageableExtras(pageable, skip, take, extraFilterParameters), query, createdByMyCompany);
		
	}
	
}