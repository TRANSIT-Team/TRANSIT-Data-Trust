package com.transit.backend.datalayers.service.helper;

import com.transit.backend.datalayers.domain.*;
import com.transit.backend.datalayers.repository.*;
import com.transit.backend.datalayers.service.helper.abstractclasses.GetById;
import com.transit.backend.datalayers.service.helper.abstractclasses.GetCollectionByIds;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class OrderServiceBeanHelper {
	
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
	
	@Autowired
	private UpdateFieldsCollectionOrder updateFieldsCollectionOrder;
	
	@Autowired
	private GetById<Order> getOrder;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private GetById<ContactPerson> getContactPerson;
	@Autowired
	private ContactPersonRepository contactPersonRepository;
	
	@Autowired
	private CostRepository costRepository;
	
	@Autowired
	private GetById<Cost> getCost;
	
	public Order findFields(Order order, Order orderOld) {
		//not copy only check by addresses
		
		if (order.getParentOrder() != null) {
			order.setParentOrder(getOrder.getEntity(order.getParentOrder().getId(), this.orderRepository, Order.class.getSimpleName()));
		}
		
		if (order.getAddressFrom() != null) {
			order.setAddressFrom(getAddress.getEntity(order.getAddressFrom(), this.addressRepository, Address.class.getSimpleName()));
		}
		if (order.getAddressTo() != null) {
			order.setAddressTo(getAddress.getEntity(order.getAddressTo(), this.addressRepository, Address.class.getSimpleName()));
		}
		if (order.getAddressBilling() != null) {
			order.setAddressBilling(getAddress.getEntity(order.getAddressBilling(), this.addressRepository, Address.class.getSimpleName()));
		}
		order = updateFieldsCollectionOrder.updateSubOrders(order, orderOld);
		order = updateFieldsCollectionOrder.updateOrderTypes(order, orderOld);
		if (order.getCompany() != null) {
			if (orderOld != null && !(orderOld.getCompany().getId().equals(order.getCompany().getId()))) {
				throw new UnprocessableEntityExeption("Cannot change Main Company of Order");
			}
			order.setCompany(getCompany.getEntity(order.getCompany(), this.companyRepository, Company.class.getSimpleName()));
		}
		order = updateFieldsCollectionOrder.updateOrderRoutes(order, orderOld);
		if (order.getPayment() != null) {
			order.setPayment(getPayment.getEntity(order.getPayment(), this.paymentRepository, Payment.class.getSimpleName()));
		}
		if (order.getCost() != null) {
			order.setCost(getCost.getEntity(order.getCost(), this.costRepository, Cost.class.getSimpleName()));
		}
		order = updateFieldsCollectionOrder.updatePackageItems(order, orderOld);
		if (order.getDeliveryMethod() != null) {
			order.setDeliveryMethod(getDeliveryMethod.getEntity(order.getDeliveryMethod(), this.deliveryMethodRepository, DeliveryMethod.class.getSimpleName()));
		}
		if (order.getContactPerson() != null) {
			order.setContactPerson(getContactPerson.getEntity(order.getContactPerson(), this.contactPersonRepository, ContactPerson.class.getSimpleName()));
		}
		order = updateFieldsCollectionOrder.updateOrderTypes(order, orderOld);
		if (orderOld != null) {
			order.setMessageCounter(orderOld.getMessageCounter());
		}
		
		return order;
	}
	
	
}
