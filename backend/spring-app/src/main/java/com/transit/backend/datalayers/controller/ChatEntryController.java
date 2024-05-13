package com.transit.backend.datalayers.controller;

import com.transit.backend.datalayers.controller.assembler.ChatEntryAssembler;
import com.transit.backend.datalayers.controller.dto.ChatEntryDTO;
import com.transit.backend.datalayers.service.ChatEntryService;
import com.transit.backend.datalayers.service.mapper.ChatEntryMapper;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.helper.verification.ValidationGroups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/orders/{orderId}/chat")
public class ChatEntryController {
	
	@Autowired
	private ChatEntryService chatEntryService;
	
	@Autowired
	private ChatEntryMapper chatEntryMapper;
	
	@Autowired
	private ChatEntryAssembler assembler;
	
	@PreAuthorize(
			"@securityChatService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	@PostMapping
	public ResponseEntity<ChatEntryDTO> create(@RequestBody @Validated(ValidationGroups.Post.class) ChatEntryDTO dto) {
		var entity = chatEntryService.create(chatEntryMapper.toEntity(dto));
		var dtoNew = assembler.toModel(entity);
		return ResponseEntity
				.created(dtoNew.getRequiredLink(IanaLinkRelations.SELF).toUri())
				.body(dtoNew);
	}
	
	@PreAuthorize(
			"@securityChatService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	@GetMapping
	public ResponseEntity<CollectionModel<ChatEntryDTO>> read(@RequestParam(name = "filter", defaultValue = "") String query,
	                                                          @PathVariable UUID orderId) {
		return new ResponseEntity<>(CollectionModel.of(assembler
				.toCollectionModel(chatEntryService.read(orderId, QueryRewrite.queryRewriteAll(query))).getContent()
				.stream()
				.map(testDTO -> assembler.toModel(chatEntryMapper.toEntity(testDTO))).collect(Collectors.toList())), HttpStatus.OK);
	}
	
	@PreAuthorize(
			"@securityUpdateChatService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	@PostMapping("/read")
	public ResponseEntity readUpdate(
			@PathVariable UUID orderId) {
		chatEntryService.readUpdate(orderId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
