package com.transit.backend.datalayers.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerExtendAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerExtend;
import com.transit.backend.datalayers.controller.assembler.*;
import com.transit.backend.datalayers.controller.dto.*;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.service.OrderService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.OrderMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.rightlayers.controller.RightsController;
import com.transit.backend.rightlayers.controller.dto.RIghtsDtoCoreProperties;
import com.transit.backend.rightlayers.controller.dto.RightsDtoCore;
import com.transit.backend.rightlayers.controller.dto.RightsDtoCoreList;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.PingService;
import com.transit.backend.rightlayers.service.RightsManageService;
import com.transit.backend.security.filterresponse.implementations.OrderFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import com.transit.backend.security.preauthaccess.GetFilterExpression;
import com.transit.backend.transferentities.OrderStatusTransferObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.*;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController extends CrudControllerExtendAbstract<Order, UUID, OrderDTO> implements CrudControllerExtend<OrderDTO, UUID> {
	
	@Autowired
	private OrderFilter orderFilter;
	@Autowired
	private OrderService service;
	@Autowired
	private OrderMapper mapper;
	@Autowired
	private OrderAssembler orderAssembler;
	@Autowired
	private PagedResourcesAssembler<Order> pagedResourcesAssembler;
	@Autowired
	private OrderStatusAssembler orderStatusAssembler;
	@Autowired
	private AccessService rightsService;
	@Autowired
	private GetFilterExpression getFilterExpression;
	@Autowired
	private OrderStatusCountAssembler orderStatusCountAssembler;
	@Autowired
	private OrderFullAssembler orderFullAssembler;
	@Autowired
	private OrderPartAssembler orderPartAssembler;
	@Autowired
	private OrderOverviewAssembler orderOverviewAssembler;
	@Autowired
	private RightsManageService rightsManageService;
	@Autowired
	private PingService pingService;
	
	@Autowired
	private RightsController rightsController;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<OrderDTO> create(@RequestBody @Validated(ValidationGroups.Post.class) OrderDTO dto) {
		return super.create(dto);
	}
	
	@Override
	public AbstractMapper<Order, OrderDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceExtend<Order, UUID> getService() {
		return this.service;
	}
	
	//@Override
	public RepresentationModelAssemblerSupport<Order, OrderDTO> getAssemblerSupport() {
		return this.orderAssembler;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<OrderDTO> update(@PathVariable("id") UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) OrderDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<Order, Order> getFilterHelper() {
		return this.orderFilter;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<OrderDTO> partialUpdate(@PathVariable("id") UUID primaryKey, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	@Override
	public Class<OrderDTO> getClazz() {
		return OrderDTO.class;
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
	public ResponseEntity<OrderDTO> readOne(@PathVariable("id") UUID primaryKey) {
		return super.readOne(primaryKey);
		
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
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
	public ResponseEntity<PagedModel<OrderDTO>> read(Pageable pageable, @RequestParam(name = "filter", defaultValue = "deleted==false;orderProperties.deleted==false") String query,
	                                                 @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                 @RequestParam(name = "take", defaultValue = "0") int take,
	                                                 @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters) {
		return super.read(GetFilterExtra.getPageableExtras(pageable, skip, take, extraFilterParameters), query, createdByMyCompany);
	}
	
	@GetMapping("/pageview")
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<PagedModel<OrderOverviewDTO>> readFull(Pageable pageable, @RequestParam(name = "filter", defaultValue = "deleted==false;orderProperties.deleted==false") String query,
	                                                             @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                             @RequestParam(name = "take", defaultValue = "0") int take,
	                                                             @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters) {
		pingService.available();
		PagedModel<OrderOverviewDTO> pages;
		if (getFilter()) {
			//log.error("Before get filter expression" + random + "   " + formatter.format(System.currentTimeMillis()));
			var newQuery = getFilterExpression.overwriteQueryWithEntityIdWithURINumber(query, null, 2, createdByMyCompany);
			if (newQuery == null) {
				pages = (PagedModel<OrderOverviewDTO>) pagedResourcesAssembler.toEmptyModel(Page.empty(), getClazz());
			} else {
				//	log.error("Before get Data" + random + "   " + formatter.format(System.currentTimeMillis()));
				Page<Order> page = getService().read(GetFilterExtra.getPageableExtras(pageable, skip, take, extraFilterParameters), QueryRewrite.queryRewriteAll(newQuery));
				//	log.error("Before get Ids" + random + "   " + formatter.format(System.currentTimeMillis()));
				Set<UUID> ids = new HashSet<>();
				page.forEach(entity -> ids.addAll(getFilterHelper().collectIDs(entity)));
				//	log.error("Before Get Entities" + random + "   " + formatter.format(System.currentTimeMillis()));
				var rights = rightsService.getAccessList(ids);
				//	log.error("Before Filter Entities" + random + "   " + formatter.format(System.currentTimeMillis()));
				page = page.map(entity -> getFilterHelper().filterEntities(entity, getFilterHelper().filterEntitiesCompanyId(), rights));
				//	log.error("Before Rewrite to Page" + random + "   " + formatter.format(System.currentTimeMillis()));
				if (page.hasContent()) {
					pages = pagedResourcesAssembler.toModel(page, orderOverviewAssembler);
					
				} else {
					pages = (PagedModel<OrderOverviewDTO>) pagedResourcesAssembler.toEmptyModel(page, getClazz());
					
				}
			}
		} else {
			Page<Order> page = getService().read(GetFilterExtra.getPageableExtras(pageable, skip, take, extraFilterParameters), QueryRewrite.queryRewriteAll(query));
			if (page.hasContent()) {
				pages = pagedResourcesAssembler.toModel(page, orderOverviewAssembler);
			} else {
				pages = (PagedModel<OrderOverviewDTO>) pagedResourcesAssembler.toEmptyModel(page, getClazz());
			}
		}
		//Response
		return new ResponseEntity<>(pages, HttpStatus.OK);
		
	}
	
	
	@PostMapping("/{id}/copy")
	@PreAuthorize(
			"@securityChatService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<OrderDTO> copyOrder(@PathVariable("id") UUID primaryKey) {
		
		pingService.available();
		var entity = this.service.copyCanceledOrder(primaryKey);
		
		rightsManageService.createEntityAndConnectIt(entity.getId(), entity.getClass().getSimpleName(), entity.getClass());
		entity.getProperties().forEach(prop -> rightsManageService.createEntityAndConnectIt(prop.getId(), prop.getClass().getSimpleName(), prop.getClass()));
		entity.getPackageItems().forEach(pack -> {
			rightsManageService.createEntityAndConnectIt(pack.getId(), pack.getClass().getSimpleName(), pack.getClass());
			pack.getProperties().forEach(prop -> rightsManageService.createEntityAndConnectIt(prop.getId(), prop.getClass().getSimpleName(), prop.getClass()));
		});
		
		return new ResponseEntity<>(orderAssembler.toModel(entity), HttpStatus.CREATED);
		
	}
	
	@GetMapping("/{id}/full")
	@PreAuthorize(
			"@securityChatService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<OrderFullDTO> readOneFull(@PathVariable("id") UUID primaryKey) {
		
		pingService.available();
		Optional<Order> test = getService().readOne(primaryKey);
		if (getFilter() && test.isPresent()) {
			test = Optional.of(getFilterHelper().filterEntities(test.get(), getFilterHelper().filterEntitiesCompanyId(), rightsService.getAccessList(getFilterHelper().collectIDs(test.get()))));
		}
		
		if (test.isPresent()) {
			var testDTO = orderFullAssembler.toModel(test.get());
			return new ResponseEntity<>(testDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/{id}/part")
	@PreAuthorize(
			"@securityChatService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<OrderPartDTO> readOnePart(@PathVariable("id") UUID primaryKey) {
		pingService.available();
		Optional<Order> test = getService().readOne(primaryKey);
		if (getFilter() && test.isPresent()) {
			test = Optional.of(getFilterHelper().filterEntities(test.get(), getFilterHelper().filterEntitiesCompanyId(), rightsService.getAccessList(getFilterHelper().collectIDs(test.get()))));
		}
		
		if (test.isPresent()) {
			var testDTO = orderPartAssembler.toModel(test.get());
			return new ResponseEntity<>(testDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/{id}/orderstatus")
	@PreAuthorize(
			"@securityServiceOrderStatus.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<OrderStatusDTO> updateOrderStatus(@PathVariable("id") UUID primaryKey, @RequestBody @Validated OrderStatusDTO dto) throws MessagingException, ClassNotFoundException, InterruptedException {
		pingService.available();
		var orderStatus = this.service.updateOrderStatus(primaryKey, dto.getOrderStatus());
		var entity = new OrderStatusTransferObject(orderStatus, primaryKey);
		var entityDTO = this.orderStatusAssembler.toModel(entity);
		
		return new ResponseEntity<>(entityDTO, HttpStatus.OK);
	}
	
	@GetMapping("/{id}/orderstatus")
	@PreAuthorize(
			"@securityServiceOrderStatus.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue" +
					")"
	)
	public ResponseEntity<OrderStatusDTO> readOrderStatus(@PathVariable("id") UUID primaryKey) {
		pingService.available();
		var orderStatus = this.service.readOrderStatus(primaryKey);
		var entity = new OrderStatusTransferObject(orderStatus, primaryKey);
		var entityDTO = this.orderStatusAssembler.toModel(entity);
		return new ResponseEntity<>(entityDTO, HttpStatus.OK);
	}
	
	
	@PutMapping("/{id}/resetoutsource")
	@PreAuthorize(
			"@securityChatService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue" +
					")"
	)
	public ResponseEntity<OrderDTO> resetoutsource(@PathVariable("id") UUID primaryKey, @RequestBody ResetOutSourceListDTO dtos) throws MessagingException, ClassNotFoundException, InterruptedException {
		pingService.available();
		Optional<Order> test = service.resetOutsource(primaryKey);
		if (getFilter() && test.isPresent()) {
			test = Optional.of(getFilterHelper().filterEntities(test.get(), getFilterHelper().filterEntitiesCompanyId(), rightsService.getAccessList(getFilterHelper().collectIDs(test.get()))));
		}
		
		
		if (test.isPresent()) {
			rightsController.processingRightsDtoCoreList(generateList(primaryKey, dtos), true);
			var testDTO = orderAssembler.toModel(test.get());
			return new ResponseEntity<>(testDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	private RightsDtoCoreList generateList(UUID parentOrderId, ResetOutSourceListDTO dtos) {
		RightsDtoCoreList list = new RightsDtoCoreList();
		List<RightsDtoCore> rights = new ArrayList<>();
		var parentOrderOpt = orderRepository.findById(parentOrderId);
		if (parentOrderOpt.isPresent() && parentOrderOpt.get().getSuborders() != null && !parentOrderOpt.get().getSuborders().isEmpty()) {
			var subOrders = parentOrderOpt.get().getSuborders();
			for (var suborder : subOrders) {
				if (suborder.getOrderStatus().equals(OrderStatus.REQUESTED)) {
					var orderRights = new RightsDtoCore();
					orderRights.setOrderId(suborder.getId());
					orderRights.setCompanyId(suborder.getCompany().getId());
					orderRights.setEntityId(suborder.getId());
					orderRights.setProperties(parseProperties("order", dtos));
					rights.add(orderRights);
					if (suborder.getOrderProperties() != null && !suborder.getOrderProperties().isEmpty()) {
						suborder.getOrderProperties().forEach(prop -> {
							var propRights = new RightsDtoCore();
							propRights.setOrderId(suborder.getId());
							propRights.setCompanyId(suborder.getCompany().getId());
							propRights.setEntityId(prop.getId());
							propRights.setProperties(setProperties(false, "key", "value", "type"));
							rights.add(propRights);
						});
					}
					if (suborder.getPackageItems() != null && !suborder.getPackageItems().isEmpty()) {
						suborder.getPackageItems().forEach(prop -> {
							var propRights = new RightsDtoCore();
							propRights.setOrderId(suborder.getId());
							propRights.setCompanyId(suborder.getCompany().getId());
							propRights.setEntityId(prop.getId());
							propRights.setProperties(parseProperties("package", dtos));
							rights.add(propRights);
							if (prop.getProperties() != null && !prop.getProperties().isEmpty()) {
								prop.getPackagePackageProperties().forEach(packProp -> {
									var packPropRights = new RightsDtoCore();
									packPropRights.setOrderId(suborder.getId());
									packPropRights.setCompanyId(suborder.getCompany().getId());
									packPropRights.setEntityId(packProp.getId());
									packPropRights.setProperties(parseProperties("packageProperty", dtos));
									rights.add(packPropRights);
								});
							}
						});
					}
					if (suborder.getContactPerson() != null) {
						var orderContactRights = new RightsDtoCore();
						orderContactRights.setOrderId(suborder.getId());
						orderContactRights.setCompanyId(suborder.getCompany().getId());
						orderContactRights.setEntityId(suborder.getContactPerson().getId());
						orderContactRights.setProperties(parseProperties("contactPerson", dtos));
						rights.add(orderContactRights);
					}
					if (suborder.getAddressTo() != null) {
						var orderAddressRights = new RightsDtoCore();
						orderAddressRights.setOrderId(suborder.getId());
						orderAddressRights.setCompanyId(suborder.getCompany().getId());
						orderAddressRights.setEntityId(suborder.getAddressTo().getId());
						orderAddressRights.setProperties(parseProperties("address", dtos));
						rights.add(orderAddressRights);
					}
					if (suborder.getAddressFrom() != null) {
						var orderAddressRights = new RightsDtoCore();
						orderAddressRights.setOrderId(suborder.getId());
						orderAddressRights.setCompanyId(suborder.getCompany().getId());
						orderAddressRights.setEntityId(suborder.getAddressFrom().getId());
						orderAddressRights.setProperties(parseProperties("address", dtos));
						rights.add(orderAddressRights);
					}
					if (suborder.getAddressBilling() != null) {
						var orderAddressRights = new RightsDtoCore();
						orderAddressRights.setOrderId(suborder.getId());
						orderAddressRights.setCompanyId(suborder.getCompany().getId());
						orderAddressRights.setEntityId(suborder.getAddressBilling().getId());
						orderAddressRights.setProperties(parseProperties("address", dtos));
						rights.add(orderAddressRights);
					}
				}
				
			}
			
			
			list.setEntries(rights);
			return list;
		} else {
			list.setEntries(rights);
			return list;
		}
	}
	
	private RIghtsDtoCoreProperties parseProperties(String clazz, ResetOutSourceListDTO dtos) {
		var props = new RIghtsDtoCoreProperties();
		
		List<String> readProperties = new ArrayList<>();
		List<String> writeProperties = new ArrayList<>();
		
		dtos.getDefaultSharingRights().forEach(entry -> {
			if (entry.getEntity().equals(clazz)) {
				entry.getCompanyProperties().forEach(prop -> {
					if (prop.isRead()) {
						readProperties.add(prop.getProperty());
					}
					if (prop.isWrite()) {
						writeProperties.add(prop.getProperty());
					}
				});
			}
		});
		
		
		props.setReadProperties(readProperties);
		props.setWriteProperties(writeProperties);
		return props;
	}
	
	private RIghtsDtoCoreProperties setProperties(boolean write, String... propList) {
		var props = new RIghtsDtoCoreProperties();
		
		List<String> readProperties = new ArrayList<>();
		List<String> writeProperties = new ArrayList<>();
		
		for (String prop : propList) {
			readProperties.add(prop);
			
			if (write) {
				writeProperties.add(prop);
			}
		}
		
		
		props.setReadProperties(readProperties);
		props.setWriteProperties(writeProperties);
		return props;
	}
}
