package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerExtendAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerExtend;
import com.transit.backend.datalayers.controller.assembler.CostDefaultPropertyAssembler;
import com.transit.backend.datalayers.controller.dto.CostDefaultPropertyDTO;
import com.transit.backend.datalayers.domain.CostDefaultProperty;
import com.transit.backend.datalayers.service.CostDefaultPropertyService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.CostDefaultPropertyMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.CostDefaultPropertyFilter;
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
@RequestMapping("/costdefaultproperties")
public class CostDefaultPropertyController extends CrudControllerExtendAbstract<CostDefaultProperty, UUID, CostDefaultPropertyDTO> implements CrudControllerExtend<CostDefaultPropertyDTO, UUID> {
	@Autowired
	private CostDefaultPropertyFilter costdefaultPropertyFilter;
	@Autowired
	private CostDefaultPropertyService service;
	@Autowired
	private CostDefaultPropertyMapper mapper;
	@Autowired
	private CostDefaultPropertyAssembler costdefaultPropertyAssembler;
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<CostDefaultPropertyDTO> create(@RequestBody @Validated(ValidationGroups.Post.class) CostDefaultPropertyDTO dto) {
		return super.create(dto);
	}
	
	@Override
	public AbstractMapper<CostDefaultProperty, CostDefaultPropertyDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceExtend<CostDefaultProperty, UUID> getService() {
		return this.service;
	}
	
	@Override
	public RepresentationModelAssemblerSupport<CostDefaultProperty, CostDefaultPropertyDTO> getAssemblerSupport() {
		return this.costdefaultPropertyAssembler;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<CostDefaultPropertyDTO> update(UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) CostDefaultPropertyDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<CostDefaultProperty, CostDefaultProperty> getFilterHelper() {
		return this.costdefaultPropertyFilter;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<CostDefaultPropertyDTO> partialUpdate(UUID primaryKey, JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	@Override
	public Class<CostDefaultPropertyDTO> getClazz() {
		return CostDefaultPropertyDTO.class;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<CostDefaultPropertyDTO> readOne(@PathVariable("id") UUID primaryKey) {
		return super.readOne(primaryKey);
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity delete(UUID primaryKey) {
		return super.delete(primaryKey);
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<PagedModel<CostDefaultPropertyDTO>> read(Pageable pageable, @RequestParam(name = "filter", defaultValue = "deleted==false") String query,
	                                                               @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                               @RequestParam(name = "take", defaultValue = "0") int take,
	                                                               @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters) {
		return super.read(GetFilterExtra.getPageableExtras(pageable, skip, take, extraFilterParameters), query, createdByMyCompany);
	}
}
