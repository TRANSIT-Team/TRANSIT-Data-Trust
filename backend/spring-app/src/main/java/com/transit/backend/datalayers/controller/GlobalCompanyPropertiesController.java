package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerExtendAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerExtend;
import com.transit.backend.datalayers.controller.assembler.GlobalCompanyPropertiesAssembler;
import com.transit.backend.datalayers.controller.dto.GlobalCompanyPropertiesDTO;
import com.transit.backend.datalayers.domain.GlobalCompanyProperties;
import com.transit.backend.datalayers.service.GlobalCompanyPropertiesService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.GlobalCompanyPropertiesMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.security.filterresponse.implementations.GlobalCompanyPropertiesFilter;
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
@RequestMapping("/globalcompanyproperties")
public class GlobalCompanyPropertiesController extends CrudControllerExtendAbstract<GlobalCompanyProperties, UUID, GlobalCompanyPropertiesDTO> implements CrudControllerExtend<GlobalCompanyPropertiesDTO, UUID> {
	
	@Autowired
	private GlobalCompanyPropertiesService globalCompanyPropertiesService;
	
	@Autowired
	private GlobalCompanyPropertiesAssembler globalCompanyPropertiesAssembler;
	
	@Autowired
	private GlobalCompanyPropertiesMapper globalCompanyPropertiesMapper;
	
	@Autowired
	private GlobalCompanyPropertiesFilter globalCompanyPropertiesFilter;
	
	@Override
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue" +
					")"
	)
	public ResponseEntity<GlobalCompanyPropertiesDTO> create(@RequestBody @Validated(ValidationGroups.Post.class) GlobalCompanyPropertiesDTO dto) {
		return super.create(dto);
	}
	
	@Override
	public AbstractMapper<GlobalCompanyProperties, GlobalCompanyPropertiesDTO> getMapper() {
		return this.globalCompanyPropertiesMapper;
	}
	
	@Override
	public CrudServiceExtend<GlobalCompanyProperties, UUID> getService() {
		return this.globalCompanyPropertiesService;
	}
	
	@Override
	public RepresentationModelAssemblerSupport<GlobalCompanyProperties, GlobalCompanyPropertiesDTO> getAssemblerSupport() {
		return this.globalCompanyPropertiesAssembler;
	}
	
	@Override
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue" +
					")"
	)
	public ResponseEntity<GlobalCompanyPropertiesDTO> update(@PathVariable("id") UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) GlobalCompanyPropertiesDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public boolean getFilter() {
		return false;
	}
	
	@Override
	public EntityFilterHelper<GlobalCompanyProperties, ?> getFilterHelper() {
		return globalCompanyPropertiesFilter;
	}
	
	@Override
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue" +
					")"
	)
	public ResponseEntity<GlobalCompanyPropertiesDTO> partialUpdate(@PathVariable("id") UUID primaryKey, JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	@Override
	public Class<GlobalCompanyPropertiesDTO> getClazz() {
		return GlobalCompanyPropertiesDTO.class;
	}
	
	@Override
	public ResponseEntity<GlobalCompanyPropertiesDTO> readOne(@PathVariable("id") UUID primaryKey) {
		return super.readOne(primaryKey);
	}
	
	@Override
	@PreAuthorize(
			"hasAnyAuthority()"
	)
	public ResponseEntity delete(@PathVariable("id") UUID primaryKey) {
		return super.delete(primaryKey);
	}
	
	@Override
	public ResponseEntity<PagedModel<GlobalCompanyPropertiesDTO>> read(Pageable pageable, @RequestParam(name = "filter", defaultValue = "deleted==false") String query,
	                                                                   @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                                   @RequestParam(name = "take", defaultValue = "0") int take,
	                                                                   @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters) {
		return super.read(GetFilterExtra.getPageableExtras(pageable, skip, take, extraFilterParameters), query, createdByMyCompany);
	}
	
	
}
