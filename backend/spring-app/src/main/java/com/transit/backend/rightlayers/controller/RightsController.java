package com.transit.backend.rightlayers.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.config.mail.SendMail;
import com.transit.backend.config.parallel.SpringUtils;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.rightlayers.controller.dto.RIghtsDtoCoreProperties;
import com.transit.backend.rightlayers.controller.dto.RightsDtoCore;
import com.transit.backend.rightlayers.controller.dto.RightsDtoCoreList;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.PingService;
import com.transit.backend.rightlayers.service.RightsManageService;
import com.transit.backend.rightlayers.service.RightsNodeService;
import com.transit.backend.rightlayers.service.helper.MethodXAddress;
import com.transit.backend.rightlayers.service.helper.UpdateRightsOrderAssigment;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Slf4j
@RestController
@RequestMapping("/entityrights")
public class RightsController {
	
	@Autowired
	private RightsManageService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UpdateRightsOrderAssigment updateRightsForOrderAssignment;
	
	@Autowired
	private RightsNodeService rightsNodeService;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private MethodXAddress methodXAddress;
	
	@Autowired
	private AccessService accessService;
	
	@Autowired
	private PingService pingService;
	
	@Autowired
	private SendMail sendMail;
	
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	
	@PutMapping("/{id}")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity update(@PathVariable("id") UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) RightsDtoCoreList dtos) throws ClassNotFoundException, MessagingException, InterruptedException {
		pingService.available();
		RightsDtoCoreList response = new RightsDtoCoreList();
		var rightsList = new ArrayList<RightsDtoCore>();
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (var dto : dtos.getEntries()) {
			
			var readProperties = dto.getProperties().getReadProperties();
			var writeProperties = dto.getProperties().getWriteProperties();
			
			boolean isEmpty = true;
			try {
				isEmpty = accessService.getAccess(primaryKey, dto.getCompanyId()).isEmpty();
			} catch (Exception ignored) {
				log.error(ignored.getMessage());
				ignored.printStackTrace();
			}
			
			try {
				if (isEmpty) {
					service.connectEntityToNewCompany(primaryKey, dto.getCompanyId(), dto.getOrderId(), readProperties, writeProperties);
				} else {
					service.updateEntityConnectionByPUT(primaryKey, dto.getCompanyId(), dto.getOrderId(), readProperties, writeProperties);
				}
				
				
				dto.setEntityId(primaryKey);
				rightsList.add(dto);
			} catch (Exception ex) {
				var typeClazz = rightsNodeService.readEntityCLass(primaryKey);
				if (typeClazz.equals(Address.class.getSimpleName())) {
					if (dto.getOrderId() == null) {
						throw new UnprocessableEntityExeption("Do not know for which Order the Address have to be change");
					} else {
						var orderToAddress = orderRepository.findById(dto.getOrderId());
						if (orderToAddress.isEmpty()) {
							throw new UnprocessableEntityExeption("Cannot found Order with Id " + dto.getOrderId() + " under the Address rights Entry");
						}
						var newAddress = methodXAddress.methodXAddress(primaryKey, dto.getOrderId());
						var newId = newAddress.getId();
						service.connectEntityToNewCompany(newId, dto.getCompanyId(), dto.getOrderId(), readProperties, writeProperties);
						dto.setEntityId(newId);
						rightsList.add(dto);
					}
				}
			}
			
		}
		
		response.setEntries(rightsList);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping()
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity createFast(@RequestBody @Validated(ValidationGroups.Post.class) RightsDtoCoreList dtos) throws ClassNotFoundException, MessagingException, InterruptedException {
		pingService.available();
		final RightsDtoCoreList response = processingRightsDtoCoreList(dtos);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@NotNull
	private RightsDtoCoreList processingRightsDtoCoreList(RightsDtoCoreList dtos) throws ClassNotFoundException, MessagingException, InterruptedException {
		return this.processingRightsDtoCoreList(dtos, false);
	}
	
	
	@NotNull
	public RightsDtoCoreList processingRightsDtoCoreList(RightsDtoCoreList dtos, boolean alwaysSendMail) throws ClassNotFoundException, MessagingException, InterruptedException {
		RightsDtoCoreList response = new RightsDtoCoreList();
		var rightsList = new ArrayList<RightsDtoCore>();
		List<CompletableFuture<RightsDtoCore>> futures = new ArrayList<>();
		var requestCompanyId = userHelperFunctions.getCompanyId();
		var servletBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
		var requestOpt = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
				.filter(ServletRequestAttributes.class::isInstance)
				.map(ServletRequestAttributes.class::cast)
				.map(ServletRequestAttributes::getRequest);
		if (requestOpt.isEmpty()) {
			throw new MessagingException("Cannot send Email.");
		}
		var request = requestOpt.get();
		
		var uriComponents = servletBuilder.build();
		
		
		for (var dto : dtos.getEntries()) {
			CompletableFuture<RightsDtoCore> updateRightsEntry = CompletableFuture.supplyAsync(() -> {
				var readProperties = dto.getProperties().getReadProperties();
				var writeProperties = dto.getProperties().getWriteProperties();
				
				boolean isEmpty = true;
				try {
					isEmpty = accessService.getAccess(dto.getEntityId(), dto.getCompanyId()).isEmpty();
				} catch (Exception ignored) {
					log.error(ignored.getMessage());
					ignored.printStackTrace();
				}
				
				try {
					if (isEmpty) {
						service.connectEntityToNewCompany(dto.getEntityId(), dto.getCompanyId(), dto.getOrderId(), readProperties, writeProperties);
					} else {
						service.updateEntityConnectionByPUT(dto.getEntityId(), dto.getCompanyId(), dto.getOrderId(), readProperties, writeProperties);
						if (rightsNodeService.readEntityCLass(dto.getEntityId()).equals(Order.class.getSimpleName())) {
							sendMail.sendMailForResendQuestion(dto.getOrderId(), uriComponents);
						}
					}
				} catch (Exception ignored) {
					log.error(ignored.getMessage());
					ignored.printStackTrace();
				}
				try {
					var access = accessService.getAccess(dto.getEntityId(), dto.getCompanyId());
					if (access.isPresent()) {
						if (access.get().getObjectProperties().getReadProperties() != null && !access.get().getObjectProperties().getReadProperties().isEmpty()) {
							dto.getProperties().setReadProperties(new ArrayList<>(access.get().getObjectProperties().getReadProperties()));
							
						} else {
							dto.getProperties().setReadProperties(new ArrayList<>());
						}
						if (access.get().getObjectProperties().getWriteProperties() != null && !access.get().getObjectProperties().getWriteProperties().isEmpty()) {
							dto.getProperties().setWriteProperties(new ArrayList<>(access.get().getObjectProperties().getWriteProperties()));
						} else {
							dto.getProperties().setWriteProperties(new ArrayList<>());
						}
					}
				} catch (Exception ignored) {
					log.error(ignored.getMessage());
					ignored.printStackTrace();
					dto.getProperties().setReadProperties(new ArrayList<>());
					dto.getProperties().setWriteProperties(new ArrayList<>());
				}
				
				return dto;
			}, SpringUtils.getBean("taskExecutor", Executor.class));
			futures.add(updateRightsEntry);
			//noch zu verändern
			//	rightsList.add(dto);
		}
		executeFutures(futures, rightsList);
		
		
		response.setEntries(rightsList);
		return response;
	}
	
	public static void executeFutures(List<CompletableFuture<RightsDtoCore>> futures, ArrayList<RightsDtoCore> rightsList) throws InterruptedException {
		CompletableFuture<Void> allCompletedRights = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
		
		try {
			allCompletedRights.get();
			futures.forEach(entry -> {
				try {
					rightsList.add(entry.get());
					
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	@PatchMapping(path = "/{id}", consumes = "application/merge-patch+json")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity partialUpdate(@PathVariable("id") UUID primaryKey, @RequestBody JsonNode node) throws JsonPatchException, JsonProcessingException, ClassNotFoundException, MessagingException, InterruptedException {
		pingService.available();
		RightsDtoCoreList response = new RightsDtoCoreList();
		var rightsList = new ArrayList<RightsDtoCore>();
		if (node.has("entries")) {
			if (node.get("entries").isArray()) {
				for (JsonNode item : node.get("entries")) {
					var rightsDto = objectMapper.treeToValue(item, RightsDtoCore.class);
					service.updateEntityConnection(primaryKey, rightsDto.getCompanyId(), rightsDto.getOrderId(), JsonMergePatch.fromJson(item));
					rightsList.add(rightsDto);
				}
			}
			response.setEntries(rightsList);
			return new ResponseEntity<>(response, HttpStatus.OK);
			
		}
		throw new UnprocessableEntityExeption("False Format");
	}
	
	
	@GetMapping(path = "/{id}")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<RightsDtoCoreList> readOne(@PathVariable("id") UUID primaryKey, @RequestParam("companyid") UUID companyid) {
		pingService.available();
		var variable = accessService.getAccess(primaryKey, companyid);
		if (variable.isPresent()) {
			var rightsDtoCoreList = new RightsDtoCoreList();
			var rightsDtoCore = new RightsDtoCore();
			rightsDtoCore.setCompanyId(companyid);
			rightsDtoCore.setEntityId(primaryKey);
			var rightstoPropeties = new RIghtsDtoCoreProperties();
			rightstoPropeties.setReadProperties(new ArrayList<>(variable.get().getObjectProperties().getReadProperties()));
			rightstoPropeties.setWriteProperties(new ArrayList<>(variable.get().getObjectProperties().getWriteProperties()));
			rightsDtoCore.setProperties(rightstoPropeties);
			rightsDtoCoreList.setEntries(new ArrayList<>(List.of(rightsDtoCore)));
			return new ResponseEntity<>(rightsDtoCoreList, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
	}
	
	@PostMapping("/orders/{id}/suborders/{idSub}")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<RightsDtoCoreList> updateRightsByOrderAssigment(@PathVariable("id") UUID parentOrderId, @PathVariable("idSub") UUID subordersId, @RequestBody RightsDtoCoreList targetDtos) throws ClassNotFoundException, MessagingException, InterruptedException {
		pingService.available();
		
		return new ResponseEntity<>(updateRightsForOrderAssignment.updateRightsForOrderAssignment(parentOrderId, subordersId, targetDtos), HttpStatus.OK);
	}
	
	
}
