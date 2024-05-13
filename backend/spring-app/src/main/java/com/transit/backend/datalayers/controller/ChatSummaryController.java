package com.transit.backend.datalayers.controller;

import com.transit.backend.datalayers.controller.assembler.ChatEntryAssembler;
import com.transit.backend.datalayers.controller.dto.ChatEntryDTO;
import com.transit.backend.datalayers.service.ChatEntryService;
import com.transit.backend.security.preauthaccess.GetFilterExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/orders/chat")
public class ChatSummaryController {
	
	@Autowired
	private GetFilterExpression getFilterExpression;
	
	@Autowired
	private ChatEntryService chatEntryService;
	
	@Autowired
	private PagedResourcesAssembler pagedResourcesAssembler;
	
	@Autowired
	private ChatEntryAssembler assembler;
	
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	@GetMapping
	public ResponseEntity<PagedModel<ChatEntryDTO>> read(Pageable pageable) {
		var newQuery = getFilterExpression.overwriteQueryWithEntityIdWithURINumber("", null, 2, false);
		var page = chatEntryService.readFilter(pageable, newQuery);
		PagedModel<ChatEntryDTO> pages;
		
		if (page.hasContent()) {
			pages = pagedResourcesAssembler.toModel(page, assembler);
		} else {
			pages = (PagedModel<ChatEntryDTO>) pagedResourcesAssembler.toEmptyModel(page, ChatEntryDTO.class);
		}
		return new ResponseEntity<>(pages, HttpStatus.OK);
		
	}
}
