package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.assembler.OrderCommentChatAssembler;
import com.transit.backend.datalayers.controller.dto.OrderCommentChatDTO;
import com.transit.backend.datalayers.service.OrderCommentChatService;
import com.transit.backend.datalayers.service.mapper.OrderCommentChatMapper;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.rightlayers.service.RightsManageService;
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
@RequestMapping("/orders/{orderId}/comment")
public class OrderCommentChatController {
	
	@Autowired
	private OrderCommentChatService orderCommentService;
	
	@Autowired
	private OrderCommentChatMapper orderCommentMapper;
	
	@Autowired
	private OrderCommentChatAssembler assembler;
	@Autowired
	private RightsManageService rightsManageService;
	
	@PreAuthorize(
			"@securityChatService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	@PostMapping
	public ResponseEntity<OrderCommentChatDTO> create(@PathVariable UUID orderId,
	                                                  @RequestBody @Validated(ValidationGroups.Post.class) OrderCommentChatDTO dto) {
		var preEntity = orderCommentMapper.toEntity(dto);
		preEntity.setOrderId(orderId);
		var entity = orderCommentService.create(preEntity);
		
		rightsManageService.createEntityAndConnectIt(entity.getId(), entity.getClass().getSimpleName(), entity.getClass());
		var dtoNew = assembler.toModel(entity);
		return new ResponseEntity<>(dtoNew, HttpStatus.CREATED);
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
	public ResponseEntity<CollectionModel<OrderCommentChatDTO>> read(@PathVariable UUID orderId) {
		return new ResponseEntity<>(CollectionModel.of(assembler
				.toCollectionModel(orderCommentService.read(orderId)).getContent()
				.stream()
				.map(testDTO -> assembler.toModel(orderCommentMapper.toEntity(testDTO))).collect(Collectors.toList())), HttpStatus.OK);
	}
	
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	@PutMapping("/{id}")
	public ResponseEntity update(
			@PathVariable UUID orderId,
			@PathVariable UUID id,
			@RequestBody @Validated(ValidationGroups.Put.class) OrderCommentChatDTO dto) {
		var entity = orderCommentService.update(orderId, id, orderCommentMapper.toEntity(dto));
		var dtoNew = assembler.toModel(entity);
		return new ResponseEntity<>(dtoNew, HttpStatus.OK);
	}
	
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	@PatchMapping("/{id}")
	public ResponseEntity patch(
			@PathVariable UUID orderId,
			@PathVariable UUID id,
			@RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		var entity = orderCommentService.patch(orderId, id, patch);
		var dtoNew = assembler.toModel(entity);
		return new ResponseEntity<>(dtoNew, HttpStatus.OK);
	}
	
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	@DeleteMapping("/{id}")
	public ResponseEntity delete(
			@PathVariable UUID orderId,
			@PathVariable UUID id) throws JsonPatchException, JsonProcessingException {
		orderCommentService.delete(orderId, id);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
