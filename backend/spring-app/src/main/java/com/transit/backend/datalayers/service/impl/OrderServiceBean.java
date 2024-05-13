package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.config.mail.SendMail;
import com.transit.backend.datalayers.controller.dto.OrderDTO;
import com.transit.backend.datalayers.controller.dto.OrderPropertyDTO;
import com.transit.backend.datalayers.controller.dto.OrderStatusCountDTO;
import com.transit.backend.datalayers.controller.dto.OrderStatusCountsDTO;
import com.transit.backend.datalayers.controller.dto.OrdersDaysDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.OrderProperty;
import com.transit.backend.datalayers.domain.PackageItem;
import com.transit.backend.datalayers.domain.QOrder;
import com.transit.backend.transferentities.OrderDaily;
import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.datalayers.repository.CompanyAddressRepository;
import com.transit.backend.datalayers.repository.OrderPropertyRepository;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.OrderOrderPropertyService;
import com.transit.backend.datalayers.service.OrderService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendPropertyAbstract;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.helper.OrderServiceBeanHelper;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.OrderMapper;
import com.transit.backend.datalayers.service.mapper.OrderPropertyMapper;
import com.transit.backend.exeptions.exeption.BadRequestException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.RightsNodeService;
import com.transit.backend.rightlayers.service.helper.RemoveRightsCompany;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import com.transit.backend.transferentities.FilterExtra;
import com.transit.backend.transferentities.OrderStatusProjection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.transit.backend.datalayers.domain.enums.OrderStatus.*;
import static com.transit.backend.rightlayers.service.helper.DefaultListHelper.getEmptyList;
import static com.transit.backend.rightlayers.service.helper.DefaultListHelper.getListIdAndOrderStatus;

@Service
@Slf4j
public class OrderServiceBean extends CrudServiceExtendPropertyAbstract<Order, OrderDTO, OrderProperty, OrderPropertyDTO> implements OrderService {
	
	@Autowired
	private OrderOrderPropertyService orderOrderPropertyService;
	@Autowired
	private OrderPropertyRepository orderPropertyRepository;
	@Autowired
	private OrderPropertyMapper orderPropertyMapper;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderServiceBeanHelper orderServiceBeanHelper;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private AccessService rightsService;
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	@Autowired
	private RightsNodeService rightsNodeService;
	@Autowired
	private CompanyAddressRepository companyAddressRepository;
	@Autowired
	private SendMail sendMail;
	@Autowired
	private RemoveRightsCompany removeRightsCompany;
	@Autowired
	private PackageItemServiceBean packageItemServiceBean;
	
	@Override
	public Class<Order> getEntityClazz() {
		return Order.class;
	}
	
	@Override
	public Class<OrderDTO> getEntityDTOClazz() {
		return OrderDTO.class;
	}
	
	@Override
	public Path<Order> getQClazz() {
		return QOrder.order;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	@Override
	public CrudServiceSubRessource<OrderProperty, UUID, UUID> getPropertySubService() {
		return this.orderOrderPropertyService;
	}
	
	@Override
	public AbstractRepository<OrderProperty> getPropertyRepository() {
		return this.orderPropertyRepository;
	}
	
	@Override
	public AbstractMapper<OrderProperty, OrderPropertyDTO> getPropertyMapper() {
		return this.orderPropertyMapper;
	}
	
	@Override
	public AbstractRepository<Order> getRepository() {
		return this.orderRepository;
	}
	
	@Override
	public AbstractMapper<Order, OrderDTO> getMapper() {
		return orderMapper;
	}
	
	@Override
	public String getPropertyDeletedString() {
		return "orderProperties.deleted==false";
	}
	
	@Override
	public String getQueryRewritedString(Matcher m) {
		return QueryRewrite.queryRewriteOrderToOrderProperties(m);
	}
	
	@Override
	public Order create(Order entity) {
		this.isAddressAvailable(entity);
		var order = this.createInternal(entity, true, null);
		return this.saveOrder(order);
	}
	
	@Override
	public Order update(UUID primaryKey, Order entity) {
		this.isAddressAvailable(entity);
		var entityOld = orderRepository.findById(primaryKey);
		if (entityOld.isEmpty()) {
			throw new NoSuchElementFoundException(Order.class.getSimpleName(), primaryKey);
			
		} else checkParentOrderId(entityOld, entity);
		checkOrderUpdateOfOrderStatus(entityOld.get(), entity, HttpMethod.PUT.name());
		entity = super.updateInternal(primaryKey, entity);
		//entity.setParentOrderInformations(updatePropertiesInternal(entityOld.get().getParentOrderInformations(), entity.getParentOrderInformations(), entity));
		entity = orderServiceBeanHelper.findFields(entity, entityOld.get());
		var order = filterExtend(this.readPackageItems(super.filterPUTPATCHInternal(super.saveInternal(entity))));
		var savedOrder = this.saveOrder(order);
		updateParentOrder(savedOrder.getId());
		
		return savedOrder;
	}
	
	@Override
	public Order partialUpdate(UUID primaryKey, JsonMergePatch patch) {
		var oldSubOrder = orderRepository.findById(primaryKey);
		if (oldSubOrder.isEmpty()) {
			throw new NoSuchElementFoundException(Order.class.getSimpleName(), primaryKey);
		}
		var dto = orderMapper.toDto(oldSubOrder.get());
		try {
			JsonNode original = objectMapper.valueToTree(dto);
			JsonNode patched = patch.apply(original);
			var patchedDTO = objectMapper.treeToValue(patched, OrderDTO.class);
			var entity = orderMapper.toEntity(patchedDTO);
			
			checkParentOrderId(oldSubOrder, entity);
			checkOrderUpdateOfOrderStatus(oldSubOrder.get(), entity, HttpMethod.PATCH.name());
			
			var order = filterExtend(this.readPackageItems(super.filterPUTPATCHInternal(partialUpdateIntern(primaryKey, patch))));
			var savedOrder = this.saveOrder(order);
			updateParentOrder(savedOrder.getId());
			return savedOrder;
			
		} catch (Exception e) {
			throw new UnprocessableEntityExeption(e.getMessage());
		}
		
		
	}
	
	@Override
	public Page<Order> read(FilterExtra pageable, String query) {
		return super.readInternal(pageable, query).map(this::filterExtend);
	}
	
	@Override
	public Optional<Order> readOne(UUID primaryKey) {
		var value = super.readOneInternal(primaryKey).map(this::filterExtend);
		return value;
	}
	
	@Override
	public void delete(UUID primaryKey) {
		
		this.deleteOrderInternal(primaryKey);
	}
	
	//Address cannot be referenced inside a Company and Order, because the Order-Address is not updatable and the company address is updatable
	private void isAddressAvailable(Order order) {
//		if (order.getAddressBilling() != null && order.getAddressBilling().getId() != null && companyAddressRepository.existsByAddress_Id(order.getAddressBilling().getId())) {
//			throw new UnprocessableEntityExeption("The addressBilling is also an company address so it can not use as order address.");
		
		if (order.getAddressFrom() != null && order.getAddressFrom().getId() != null && companyAddressRepository.existsByAddress_Id(order.getAddressFrom().getId())) {
			throw new UnprocessableEntityExeption("The addressFrom is also an company address so it can not use as order address.");
		} else if (order.getAddressTo() != null && order.getAddressTo().getId() != null && companyAddressRepository.existsByAddress_Id(order.getAddressTo().getId())) {
			throw new UnprocessableEntityExeption("The addressTo is also an company address so it can not use as order address.");
		}
	}
	
	public void checkOrderUpdateOfOrderStatus(Order oldOrder, Order order, String method) {
		if (method.equals(HttpMethod.POST.name()) && oldOrder == null) {
			if (order.getOrderStatus() != OPEN &&
					order.getOrderStatus() != OrderStatus.REQUESTED &&
					order.getOrderStatus() != OrderStatus.CREATED) {
				throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + "can only be "
						+ OPEN.toString() + "," +
						OrderStatus.CREATED.toString() + " or "
						+ OrderStatus.REQUESTED.toString());
			}
		} else {
			if (!order.getOrderStatus().equals(oldOrder.getOrderStatus())) {
				throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + " cannot be updated over Order endpoint");
			}
		}
	}
	
	
	public Order filterExtend(Order order) {
		if (order.getSuborders() != null) {
			order.setSuborders(order
					.getSuborders()
					.stream()
					.filter(value -> !value.isDeleted())
					.toList());
		}
		if (order.getOrderTypes() != null) {
			order.setOrderTypes(order
					.getOrderTypes()
					.stream()
					.filter(value -> !value.isDeleted())
					.collect(Collectors.toCollection(TreeSet::new)));
		}
		if (order.getOrderRoutes() != null) {
			order.setOrderRoutes(order
					.getOrderRoutes()
					.stream()
					.filter(value -> !value.isDeleted())
					.toList());
		}
		if (order.getPackageItems() != null) {
			order.setPackageItems(order
					.getPackageItems()
					.stream()
					.filter(value -> !value.isDeleted())
					.collect(Collectors.toSet()));
		}
		
		return order;
	}
	
	public Order createInternal(Order entity, boolean root, UUID parentId) {
		if (root) {
			entity.setParentOrder(null);
		} else if (!(parentId.equals(entity.getParentOrder().getId()))) {
			throw new UnprocessableEntityExeption("Parent Order Id and Path Id are not the same");
		}
		checkOrderUpdateOfOrderStatus(null, entity, HttpMethod.POST.name());
		entity = super.createInternal(entity);


//		if (entity.getParentOrderInformations() != null && !entity.getParentOrderInformations().isEmpty()) {
//			//check if duplicate key exists
//			duplicateKeyInternalParentOrderInformation(entity.getParentOrderInformations());
//			Order finalEntity = entity;
//			entity.getParentOrderInformations().forEach(testProperties -> testProperties.setOrder(finalEntity));
//		}
		
		entity = orderServiceBeanHelper.findFields(entity, null);
		//entity = this.updatePackageItems(entity,entity.getPackageItems().stream().map(PackageItem::getId).collect(Collectors.toSet()),true);
		return super.saveInternal(entity);
	}
	
	@Override
	public String updateOrderStatus(UUID orderId, String status) throws MessagingException, ClassNotFoundException, InterruptedException {
		AccessResponseDTO rights;
		
		//Is checked at access filter that it exists
		rights = rightsService.getAccess(orderId).get();
		
		
		var entityOld = super.readOneInternal(orderId).get();
		var oldStatus = entityOld.getOrderStatus().toString();
		var newOrderStatus = OrderStatus.valueOf(status);
		var orderOnlyWithOrderStatus = new Order();
		orderOnlyWithOrderStatus.setOrderStatus(newOrderStatus);
		//Is checked at access filter that it exists
		checkUpdateOrderStatus(entityOld, orderOnlyWithOrderStatus, rights);
		
		entityOld.setOrderStatus(newOrderStatus);
		entityOld = super.updateInternal(orderId, entityOld);
		entityOld = super.saveInternal(entityOld);
		var orderIDTemp = UUID.fromString(orderId.toString());
		if (entityOld.isSuborderType()) {
			while (true) {
				var subOrder = orderRepository.findById(orderIDTemp).get();
				if (!subOrder.isSuborderType()) {
					break;
				}
				var parentOrder = orderRepository.findById(subOrder.getParentOrder().getId()).get();
				if (OrderStatus.valueOf(status).equals(PROCESSING)) {
					parentOrder.setOrderStatus(PROCESSING);
					super.saveInternal(parentOrder);
					orderIDTemp = parentOrder.getId();
				} else {
					break;
				}
			}
			if (OrderStatus.valueOf(status).equals(OrderStatus.REVOKED) ||
					OrderStatus.valueOf(status).equals(OrderStatus.REJECTED)) {
				removeRightsCompany.removeRightsOfCompany(entityOld, new ArrayList<>(), entityOld.getCompany().getId(), getListIdAndOrderStatus(), getEmptyList(), OrderStatus.valueOf(status), orderRepository.findById(entityOld.getParentOrder().getId()).get().getCompany().getId());
//				var subRights = rightsService.readOne(orderId, entityOld.getCompany().getId()).get();
//				var newRigthsList = new ArrayList<NodePropertiesDTOInternal>();
//				for (var entry : subRights.getProperties()) {
//					newRigthsList.add(new NodePropertiesDTOInternal(entry.getKey(), ""));
//				}
//				subRights.setProperties(newRigthsList);
//				rightsService.update(orderId, subRights, entityOld.getCompany().getId());
				if (OrderStatus.valueOf(status).equals(OrderStatus.REVOKED)) {
					sendMail.sendMailForRevoked(orderId);
					
				}
			}
			if (oldStatus.equals(ACCEPTED.toString()) && OrderStatus.valueOf(status).equals(OPEN)) {
				sendMail.sendMailForAcceptedToOpen(entityOld.getId());
			}
			if (OrderStatus.valueOf(status).equals(CANCELED)) {
				sendMail.sendMailForCanceledSuborder(orderId);
			}
		}
		
		
		return entityOld.getOrderStatus().name();
		
	}
	
	@Override
	public String readOrderStatus(UUID orderId) {
		var valueOpt = super.readOneInternal(orderId);
		if (valueOpt.isEmpty()) {
			throw new NoSuchElementFoundOrDeleted(Order.class.getSimpleName(), orderId);
		}
		var value = valueOpt.get();
		return value.getOrderStatus().name();
		
	}
	
	@Override
	public Order saveOrder(Order order) {
		if (order.getId() != null) {
			order.setIdString(order.getId().toString());
		}
		return saveInternal(order);
	}
	/*public Order updatePackageItems(Order order,Set<UUID> packageItemsToRemove,boolean actualOrder){

		if(order.getParentOrder()!=null){
			this.updatePackageItems(order.getParentOrder(),packageItemsToRemove,false);
		}
		if(!actualOrder &&order.getPackageItems() != null && !order.getPackageItems().isEmpty()) {
				var packageItemsIds = order.getPackageItems().stream().map(PackageItem::getId).collect(Collectors.toSet());
				var packageItemsToRemoveHere = new HashSet<>();
				for(UUID packageItemId:packageItemsIds){
					if(packageItemsToRemove.contains(packageItemId)){
						packageItemsToRemoveHere.add(packageItemId);
					}
				}
				order.setPackageItems(order.getPackageItems().stream().filter(packageItem->{
					for(var id: packageItemsToRemoveHere){
						if(packageItem.getId().equals(id)){
							return false;
						}
					}
					return true;
				}).collect(Collectors.toSet()));

				orderRepository.saveAndFlush(order);
			}
	
		return order;
	}*/
	
	@Override
	public Order copyCanceledOrder(UUID id) {
		var orderTemp = orderRepository.findByIdAndDeleted(id, false);
		if (orderTemp.isEmpty()) {
			throw new NoSuchElementFoundException("Order with id does not exists.");
		}
		if (!orderTemp.get().getOrderStatus().equals(CANCELED)) {
			throw new UnprocessableEntityExeption("Order Status is not canceled.");
		}
		var oldOlder = orderTemp.get();
		var newOrder = oldOlder.copyOrder();
		var oldOrderProperties = oldOlder.getOrderProperties();
		var newOrderProperties = new TreeSet<OrderProperty>();
		oldOrderProperties.forEach(p -> {
			var newP = p.copyOrderProperty();
			newOrderProperties.add(newP);
			
		});
		newOrder.setOrderProperties(newOrderProperties);
		
		newOrder = this.saveInternal(newOrder);
		oldOlder.setNewOrderId(newOrder.getId());
		orderRepository.saveAndFlush(oldOlder);
		
		var oldPackages = oldOlder.getPackageItems();
		var newPackages = new HashSet<PackageItem>();
		oldPackages.forEach(p -> {
			var newP = p.copyPackageItem();
			newP = packageItemServiceBean.create(newP);
			newPackages.add(newP);
			
		});
		newOrder.setPackageItems(newPackages);
		return orderRepository.saveAndFlush(newOrder);
	}
	
	
	public void checkUpdateOrderStatus(Order oldOrder, Order orderOnlyStatus, AccessResponseDTO rights) {
		checkUpdateOrderStatus(oldOrder, orderOnlyStatus, rights, null);
	}
	
	public void checkUpdateOrderStatus(Order oldOrder, Order orderOnlyStatus, AccessResponseDTO rights, UUID companyId) {
		boolean canChangeOrderStatus = false;
		if (companyId == null) {
			companyId = userHelperFunctions.getCompanyId();
			if (rightsNodeService.nodeMultipleOutgoingEdges(oldOrder.getId()) && userHelperFunctions.getCompanyId().equals(companyId)) {
				canChangeOrderStatus = true;
			} else if (rights.getObjectProperties().getReadProperties().contains("orderStatus")) {
				canChangeOrderStatus = true;
			}
		} else {
			if (rightsNodeService.nodeMultipleOutgoingEdges(oldOrder.getId())) {
				canChangeOrderStatus = true;
			} else if (rights.getObjectProperties().getReadProperties().contains("orderStatus")) {
				canChangeOrderStatus = true;
			}
		}
		if (!canChangeOrderStatus) {
			throw new UnprocessableEntityExeption("Your Company cannot change the Order Status");
		}
		if (oldOrder != null && oldOrder.getOrderStatus() == ACCEPTED) {
			if (orderOnlyStatus.getOrderStatus() != ACCEPTED &&
					orderOnlyStatus.getOrderStatus() != OrderStatus.REVOKED &&
					orderOnlyStatus.getOrderStatus() != OPEN) {
				throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + "can only be "
						+ ACCEPTED.toString() + " , "
						+ OrderStatus.REVOKED.toString() + " or "
						+ OPEN.toString());
			}
		}
		if (oldOrder != null && oldOrder.getOrderStatus() == OPEN) {
			if (orderOnlyStatus.getOrderStatus() != OPEN &&
					orderOnlyStatus.getOrderStatus() != PROCESSING &&
					orderOnlyStatus.getOrderStatus() != OrderStatus.CANCELED) {
				throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + "can only be "
						+ OPEN.toString() + " , "
						+ PROCESSING.toString() + " or "
						+ OrderStatus.CANCELED.toString());
			}
			
		}
		if (oldOrder != null && oldOrder.getOrderStatus() == PROCESSING) {
			if (orderOnlyStatus.getOrderStatus() != PROCESSING &&
					orderOnlyStatus.getOrderStatus() != OrderStatus.COMPLETE &&
					orderOnlyStatus.getOrderStatus() != OrderStatus.CANCELED) {
				throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + "can only be "
						+ PROCESSING.toString() + " , "
						+ OrderStatus.COMPLETE.toString() + " or "
						+ OrderStatus.CANCELED.toString());
				
			}
			
		}
		if (oldOrder != null && oldOrder.getOrderStatus() == OrderStatus.COMPLETE) {
			if (orderOnlyStatus.getOrderStatus() != OrderStatus.COMPLETE) {
				throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + "can only be "
						+ OrderStatus.COMPLETE.toString());
			}
			
		}
		if (oldOrder != null && oldOrder.getOrderStatus() == OrderStatus.CANCELED) {
			if (orderOnlyStatus.getOrderStatus() != OrderStatus.CANCELED) {
				throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + "can only be "
						+ OrderStatus.CANCELED.toString());
			}
			
		}
		if (oldOrder != null && oldOrder.getOrderStatus() == OrderStatus.REJECTED) {
			if (orderOnlyStatus.getOrderStatus() != OrderStatus.REJECTED) {
				throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + "can only be "
						+ OrderStatus.REJECTED.toString());
			}
			
		}
		if (oldOrder != null && oldOrder.getOrderStatus() == OrderStatus.REQUESTED) {
			if (orderOnlyStatus.getOrderStatus() != OrderStatus.REQUESTED &&
					orderOnlyStatus.getOrderStatus() != ACCEPTED &&
					orderOnlyStatus.getOrderStatus() != OrderStatus.REJECTED &&
					orderOnlyStatus.getOrderStatus() != OrderStatus.REVOKED) {
				throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + "can only be "
						+ OrderStatus.REQUESTED.toString() + " , "
						+ OrderStatus.REVOKED.toString() + " , "
						+ ACCEPTED.toString() + " or "
						+ OrderStatus.REJECTED.toString());
			}
			
		}
		if (oldOrder != null && oldOrder.getOrderStatus() == OrderStatus.REVOKED) {
			if (orderOnlyStatus.getOrderStatus() != OrderStatus.REVOKED) {
				throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + "can only be "
						+ OrderStatus.REVOKED.toString());
			}
			
		}
		
		if (oldOrder != null && oldOrder.getOrderStatus() == OrderStatus.CREATED) {
			if (orderOnlyStatus.getOrderStatus() != OrderStatus.CREATED &&
					orderOnlyStatus.getOrderStatus() != OPEN &&
					orderOnlyStatus.getOrderStatus() != CANCELED) {
				throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + " can only be "
						+ OrderStatus.CREATED.toString() + " or " +
						OPEN.toString() + ", " +
						CANCELED.toString()
				);
			}
			
		}
		
		if (oldOrder.getOrderStatus() != orderOnlyStatus.getOrderStatus()) {
			
			if (oldOrder != null && (oldOrder.getSuborders() != null || !oldOrder.getSuborders().isEmpty())) {
				if (oldOrder.getOrderStatus() == OPEN ||
						oldOrder.getOrderStatus() == PROCESSING) {
					for (var suborder : oldOrder.getSuborders()) {
						if (suborder.getOrderStatus() == OrderStatus.REQUESTED ||
								suborder.getOrderStatus() == ACCEPTED ||
								suborder.getOrderStatus() == OPEN ||
								suborder.getOrderStatus() == PROCESSING) {
							throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + "cannot change, because there exists suborder with OrderStatus "
									+ OrderStatus.REQUESTED.toString() + " , "
									+ ACCEPTED.toString() + " , "
									+ OPEN.toString() + " or "
									+ PROCESSING.toString());
						}
					}
				}
			}
			if (oldOrder != null && oldOrder.isSuborderType() &&
					oldOrder.getOrderStatus() == ACCEPTED &&
					orderOnlyStatus.getOrderStatus() == OPEN) {
				for (var suborder : oldOrder.getParentOrder().getSuborders()) {
					if (!suborder.getId().equals(oldOrder.getId())) {
						if (suborder.getOrderStatus() == OPEN ||
								suborder.getOrderStatus() == PROCESSING ||
								suborder.getOrderStatus() == OrderStatus.CANCELED ||
								suborder.getOrderStatus() == OrderStatus.COMPLETE) {
							throw new UnprocessableEntityExeption(OrderStatus.class.getSimpleName() + "cannot change, because there exists another suborder  with OrderStatus "
									+ OPEN.toString() + " , "
									+ PROCESSING.toString() + " , "
									+ OrderStatus.CANCELED.toString() + " or "
									+ OrderStatus.COMPLETE.toString());
						}
					}
				}
			}
		}
		
	}
	
	private void updateParentOrder(UUID subId) {
		var order = orderRepository.findById(subId);
		if (order.isPresent() && order.get().isSuborderType() && order.get().getParentOrder() != null) {
			var parentOrderOpt = orderRepository.findById(order.get().getParentOrder().getId());
			if (parentOrderOpt.isPresent()) {
				var parentOrder = parentOrderOpt.get();
				if (order.get().getDeliveryTimestampArrive() != null) {
					parentOrder.setDeliveryTimestampArrive(order.get().getDeliveryTimestampArrive());
				}
				if (order.get().getDeliveryTimestampLeave() != null) {
					parentOrder.setDeliveryTimestampLeave(order.get().getDeliveryTimestampLeave());
				}
				if (order.get().getDeliveryPerson() != null) {
					parentOrder.setDeliveryPerson(order.get().getDeliveryPerson());
				}
				if (order.get().getPickUpPerson() != null) {
					parentOrder.setPickUpPerson(order.get().getPickUpPerson());
				}
				if (order.get().getPickUpTimestampArrive() != null) {
					parentOrder.setPickUpTimestampArrive(order.get().getPickUpTimestampArrive());
				}
				if (order.get().getPickUpTimestampLeave() != null) {
					parentOrder.setPickUpTimestampLeave(order.get().getPickUpTimestampLeave());
				}
				this.saveOrder(parentOrder);
				updateParentOrder(parentOrder.getId());
			}
		}
	}
	
	private void checkParentOrderId(Optional<Order> oldSubOrder, Order entity) {
		if (oldSubOrder.get().getParentOrder() != null && entity.getParentOrder() != null &&
				!oldSubOrder.get().getParentOrder().getId().equals(entity.getParentOrder().getId())) {
			throw new UnprocessableEntityExeption("Not Same ParentId");
		} else if (oldSubOrder.get().getParentOrder() != null && entity.getParentOrder() == null) {
			throw new UnprocessableEntityExeption("Cannot update ParentId");
		} else if (oldSubOrder.get().getParentOrder() == null && entity.getParentOrder() != null) {
			throw new UnprocessableEntityExeption("ParentId was null and should be null");
		}
	}
	
	//NO Update Of ParentOrderInformations
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Order partialUpdateIntern(UUID primaryKey, JsonMergePatch patch) {
		var entityOld = orderRepository.findById(primaryKey);
		//for validation of the addresses
		try {
			OrderDTO testDTO = getMapper().toDto(entityOld.get());
			JsonNode original = objectMapper.valueToTree(testDTO);
			JsonNode patched = patch.apply(original);
			var response = objectMapper.treeToValue(patched, getEntityDTOClazz());
			var forValidation = getMapper().toEntity(response);
			this.isAddressAvailable(forValidation);
		} catch (JsonPatchException | JsonProcessingException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		var orderDTOPatched = super.partialUpdateInternal(primaryKey, patch);
		
		var patchedOrder = super.checkviolationsInternal(primaryKey, super.partialUpdateSavePropertiesInternal(orderDTOPatched));
		patchedOrder = orderServiceBeanHelper.findFields(patchedOrder, entityOld.get());
		//patchedOrder = this.updatePackageItems(patchedOrder,patchedOrder.getPackageItems().stream().map(PackageItem::getId).collect(Collectors.toSet()),true);
		return super.saveInternal(patchedOrder);
	}
	
	public Order deleteOrderInternal(UUID primaryKey) {
		var oldEntity = orderRepository.findById(primaryKey).get();
		
		
		if (oldEntity.getOrderStatus().equals(CREATED)) {
			var entity = super.deleteInternal(primaryKey);
			if (entity.getSuborders() != null && !entity.getSuborders().isEmpty()) {
				entity.setSuborders(entity.getSuborders().stream().map(suborder -> this.deleteOrderInternal(suborder.getId())).toList());
			}
			if (entity.getParentOrder() != null) {
				entity.setParentOrder(super.readOneInternal(entity.getParentOrder().getId()).get());
			}
			entity.setOrderStatus(CANCELED);
			return readPackageItems(super.saveInternal(entity));
			
		} else {
			throw new UnprocessableEntityExeption("Cannot delete Order because it has orderStatus: " + oldEntity.getOrderStatus().name());
			
		}
		
	}
	
	public Order readPackageItems(Order order) {
	
	
	/*	Set<PackageItem> packageItems = new HashSet<>();
		if(order.getSuborders()!=null &&!order.getSuborders().isEmpty()){
			for(Order suborder: order.getSuborders()) {
				suborder = this.readPackageItems(suborder);
				if (!suborder.getPackageItems().isEmpty()) {
					packageItems.addAll(suborder.getPackageItems());
				}
			}
		}
		if (!order.isDeleted()&&order.getPackageItems() != null && !order.getPackageItems().isEmpty()) {

					packageItems.addAll(order.getPackageItems());
			}
		
		order.setPackageItems(packageItems);*/
		return order;
	}
	
	
	@Override
	public Optional<Order> resetOutsource(UUID orderId) {
		var orderOpt = orderRepository.findById(orderId);
		
		
		if (orderOpt.isEmpty()) {
			throw new NoSuchElementFoundException("No Order with id " + orderId + " exists.");
		}
		var order = orderOpt.get();
		var suborders = order.getSuborders();
		if (suborders == null) {
			throw new BadRequestException("No Suborders exists.");
		}
		if (order.getOrderStatus().equals(PROCESSING) || order.getOrderStatus().equals(OPEN)) {
			var optSub = suborders.stream().filter(sub -> sub.getOrderStatus().equals(CANCELED)).findAny();
			if (optSub.isEmpty()) {
				throw new BadRequestException("No Suborder with Status CANCELED exists.");
			}
			if (suborders.stream().anyMatch(sub -> !sub.getOrderStatus().equals(CANCELED) && !sub.getOrderStatus().equals(REJECTED) && !sub.getOrderStatus().equals(REVOKED))) {
				throw new BadRequestException("There is still an Combination of Suborderstatus, which block the executing of this method.");
			}
			order.setOrderStatus(OPEN);
			order = this.saveOrder(order);
			var companyIds = new HashSet<UUID>();
			var suborderIdsSelect = new HashSet<UUID>();
			suborders.forEach(sub -> companyIds.add(sub.getCompany().getId()));
			AtomicReference<Map<UUID, OffsetDateTime>> subOrderIds = new AtomicReference<>(new HashMap<>());
			companyIds.forEach(companyId -> {
				subOrderIds.set(new HashMap<>());
				suborders.forEach(sub -> {
					if (sub.getCompany().getId().equals(companyId)) {
						subOrderIds.get().put(sub.getId(), sub.getCreateDate());
					}
				});
				Map.Entry<UUID, OffsetDateTime> newest = null;
				for (var entry : subOrderIds.get().entrySet()) {
					if (newest == null) {
						newest = entry;
					} else if (newest.getValue().isBefore(entry.getValue())) {
						newest = entry;
					}
				}
				suborderIdsSelect.add(newest.getKey());
			});
			
			suborders.forEach(sub -> {
				if (sub.getOrderStatus().equals(REVOKED) && suborderIdsSelect.contains(sub.getId())) {
					sub.setOrderStatus(REQUESTED);
					this.saveOrder(sub);
				}
			});
			return Optional.of(order);
		} else {
			if (order.getOrderStatus().equals(OPEN) && suborders.stream().anyMatch(sub -> sub.getOrderStatus().equals(REQUESTED)) && suborders.stream().anyMatch(sub -> sub.getOrderStatus().equals(CANCELED))) {
				return Optional.of(order);
			}
			return Optional.empty();
		}
		
		
	}
	
}
