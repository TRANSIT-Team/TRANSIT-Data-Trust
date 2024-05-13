package com.transit.backend.datalayers.service.helper;

import com.transit.backend.datalayers.domain.*;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.repository.*;
import com.transit.backend.datalayers.service.helper.abstractclasses.GetById;
import com.transit.backend.datalayers.service.helper.abstractclasses.GetCollectionByIds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateFieldsCollectionOrder {
	
	@Autowired
	private AddressRepository addressRepository;
	
	
	@Autowired
	private OrderTypeRepository orderTypeRepository;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private OrderRouteRepository orderRouteRepository;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private PackageItemRepository packageItemRepository;
	
	@Autowired
	private DeliveryMethodRepository deliveryMethodRepository;
	
	@Autowired
	private GetById<Address> getAddress;
	
	@Autowired
	private GetById<Company> getCompany;
	
	@Autowired
	private GetById<DeliveryMethod> getDeliveryMethod;
	
	@Autowired
	private OrderRepository suborderRepository;
	@Autowired
	private GetCollectionByIds<Order, OrderRepository> getSubOrderCollectionByIds;
	
	@Autowired
	private GetById<Order> getSuborder;
	
	
	@Autowired
	private GetCollectionByIds<OrderType, OrderTypeRepository> getOrderTypeCollectionByIds;
	
	@Autowired
	private GetById<OrderType> getOrderType;
	
	@Autowired
	private GetCollectionByIds<OrderRoute, OrderRouteRepository> getOrderRouteCollectionByIds;
	
	@Autowired
	private GetById<OrderRoute> getOrderRoute;
	
	@Autowired
	private GetCollectionByIds<Payment, PaymentRepository> getPaymentCollectionByIds;
	
	@Autowired
	private GetById<Payment> getPayment;
	
	@Autowired
	private GetCollectionByIds<PackageItem, PackageItemRepository> getPackageItemCollectionByIds;
	
	@Autowired
	private GetById<PackageItem> getPackageItem;
	
	
	public Order updateSubOrders(Order order, Order orderOld) {
		if (order.getSuborders() != null && !order.getSuborders().isEmpty() && orderOld != null) {
			if (orderOld.getSuborders() != null) {
				order.setSuborders(getSubOrderCollectionByIds.updateList(
						orderOld.getSuborders().stream().map(AbstractEntity::getId).toList(),
						order.getSuborders().stream().map(AbstractEntity::getId).toList(),
						suborderRepository,
						Order.class.getSimpleName(),
						getSuborder
				));
			} else {
				order.setSuborders(getSubOrderCollectionByIds.updateList(
						null,
						order.getSuborders().stream().map(AbstractEntity::getId).toList(),
						suborderRepository,
						Order.class.getSimpleName(),
						getSuborder
				));
			}
		} else {
			if (orderOld != null && orderOld.getSuborders() != null) {
				order.setSuborders(getSubOrderCollectionByIds.updateList(
						orderOld.getSuborders().stream().map(AbstractEntity::getId).toList(),
						null,
						suborderRepository,
						Order.class.getSimpleName(),
						getSuborder
				));
			} else {
				order.setSuborders(getSubOrderCollectionByIds.updateList(
						null,
						null,
						suborderRepository,
						Order.class.getSimpleName(),
						getSuborder
				));
			}
		}
		return order;
	}
	
	
	public Order updateOrderTypes(Order order, Order orderOld) {
		if (order.getOrderTypes() != null && !order.getOrderTypes().isEmpty() && orderOld != null) {
			if (orderOld.getOrderTypes() != null) {
				order.setOrderTypes(getOrderTypeCollectionByIds.updateSet(
						orderOld.getOrderTypes().stream().map(AbstractEntity::getId).toList(),
						order.getOrderTypes().stream().map(AbstractEntity::getId).toList(),
						orderTypeRepository,
						Order.class.getSimpleName(),
						getOrderType
				));
			} else {
				order.setOrderTypes(getOrderTypeCollectionByIds.updateSet(
						null,
						order.getOrderTypes().stream().map(AbstractEntity::getId).toList(),
						orderTypeRepository,
						Order.class.getSimpleName(),
						getOrderType
				));
			}
		} else {
			if (orderOld != null && orderOld.getOrderTypes() != null) {
				order.setOrderTypes(getOrderTypeCollectionByIds.updateSet(
						orderOld.getOrderTypes().stream().map(AbstractEntity::getId).toList(),
						null,
						orderTypeRepository,
						Order.class.getSimpleName(),
						getOrderType
				));
			} else {
				order.setOrderTypes(getOrderTypeCollectionByIds.updateSet(
						null,
						null,
						orderTypeRepository,
						OrderType.class.getSimpleName(),
						getOrderType
				));
			}
		}
		return order;
	}
	
	public Order updateOrderRoutes(Order order, Order orderOld) {
		if (order.getOrderRoutes() != null && !order.getOrderRoutes().isEmpty() && orderOld != null) {
			if (orderOld.getOrderRoutes() != null) {
				order.setOrderRoutes(getOrderRouteCollectionByIds.updateList(
						orderOld.getOrderRoutes().stream().map(AbstractEntity::getId).toList(),
						order.getOrderRoutes().stream().map(AbstractEntity::getId).toList(),
						orderRouteRepository,
						Order.class.getSimpleName(),
						getOrderRoute
				));
			} else {
				order.setOrderRoutes(getOrderRouteCollectionByIds.updateList(
						null,
						order.getOrderRoutes().stream().map(AbstractEntity::getId).toList(),
						orderRouteRepository,
						Order.class.getSimpleName(),
						getOrderRoute
				));
			}
		} else {
			if (orderOld != null && orderOld.getOrderRoutes() != null) {
				order.setOrderRoutes(getOrderRouteCollectionByIds.updateList(
						orderOld.getOrderRoutes().stream().map(AbstractEntity::getId).toList(),
						null,
						orderRouteRepository,
						Order.class.getSimpleName(),
						getOrderRoute
				));
			} else {
				order.setOrderRoutes(getOrderRouteCollectionByIds.updateList(
						null,
						null,
						orderRouteRepository,
						OrderRoute.class.getSimpleName(),
						getOrderRoute
				));
			}
		}
		return order;
	}
	
	
	public Order updatePackageItems(Order order, Order orderOld) {
		if (order.getPackageItems() != null && !order.getPackageItems().isEmpty()) {
			if (orderOld != null && orderOld.getPackageItems() != null) {
				order.setPackageItems(getPackageItemCollectionByIds.updateSet(
						orderOld.getPackageItems().stream().map(AbstractEntity::getId).toList(),
						order.getPackageItems().stream().map(AbstractEntity::getId).toList(),
						packageItemRepository,
						Order.class.getSimpleName(),
						getPackageItem
				));
			} else {
				order.setPackageItems(getPackageItemCollectionByIds.updateSet(
						null,
						order.getPackageItems().stream().map(AbstractEntity::getId).toList(),
						packageItemRepository,
						Order.class.getSimpleName(),
						getPackageItem
				));
			}
		} else {
			if (orderOld != null && orderOld.getPackageItems() != null) {
				order.setPackageItems(getPackageItemCollectionByIds.updateSet(
						orderOld.getPackageItems().stream().map(AbstractEntity::getId).toList(),
						null,
						packageItemRepository,
						Order.class.getSimpleName(),
						getPackageItem
				));
			} else {
				order.setPackageItems(getPackageItemCollectionByIds.updateSet(
						null,
						null,
						packageItemRepository,
						PackageItem.class.getSimpleName(),
						getPackageItem
				));
			}
		}
		return order;
	}
	
	
}
