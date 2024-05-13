package com.transit.backend.security.filterresponse.implementations;


import com.transit.backend.datalayers.domain.*;
import com.transit.backend.datalayers.repository.CompanyIdToCompanyOIDRepository;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractParentEntityFilter;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractPropertyEntityFilter;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

;

@Component
public class OrderFilter extends AbstractParentEntityFilter<Order, Order, OrderProperty, OrderProperty> implements EntityFilterHelper<Order, Order> {
	
	
	@Autowired
	private OrderPropertyFilter orderPropertyFilter;
	
	@Autowired
	private AddressFilter addressFilter;
	
	@Autowired
	private CompanyFilter companyFilter;
	
	@Autowired
	private DeliveryMethodFilter deliveryMethodFilter;
	
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	
	@Autowired
	private CompanyIdToCompanyOIDRepository companyIDToCompanyOIDRepository;
	
	@Autowired
	private ContactPersonFilter contactPersonFilter;
	
	@Autowired
	private PaymentFilter paymentFilter;
	
	@Autowired
	private CostFilter costFilter;
	
	@Override
	public Order transformToTransfer(Order entity) {
		return entity;
	}
	
	@Override
	public Order transformToEntity(Order entity) {
		return entity;
	}
	
	@Override
	public Order transformToTransfer(Order entity, Order entityOld) {
		return entity;
	}
	
	@Override
	public Class<Order> getClazz() {
		return Order.class;
	}
	
	@Override
	public String getPathToEntity(Order entity, Order entity2) {
		return "/orders/" + entity.getId();
	}
	
	@Override
	public Order filterEntities(Order entity, UUID companyId, StorageRights storageRights) {
		entity = super.filterEntities(entity, companyId, storageRights);
		if (entity != null) {
			if (entity.getParentOrder() != null) {
				entity.setParentOrder(super.filterEntities(entity.getParentOrder(), companyId, storageRights));
			}
			if (entity.getAddressFrom() != null) {
				entity.setAddressFrom(addressFilter.filterEntities(entity.getAddressFrom(), companyId, storageRights));
			}
			if (entity.getAddressTo() != null) {
				entity.setAddressTo(addressFilter.filterEntities(entity.getAddressTo(), companyId, storageRights));
			}
			if (entity.getAddressBilling() != null) {
				entity.setAddressBilling(addressFilter.filterEntities(entity.getAddressBilling(), companyId, storageRights));
			}
			if (entity.getSuborders() != null && !entity.getSuborders().isEmpty()) {
				List<UUID> ids = new ArrayList<>();
				List<UUID> removeIds = new ArrayList<>();
				entity.getSuborders().forEach(prop -> ids.add(prop.getId()));
				getUUIDsToDelete(ids, removeIds, storageRights);
				var proper = entity
						.getSuborders()
						.stream()
						.filter(prop -> !removeIds.contains(prop.getId()))
						.map(prop -> prop)//Bisher kein weiteres Filtern nötig, da nur die Id der jeweiligen SubOrder mitgegeben wird
						.toList();
				if (proper.isEmpty()) {
					entity.setSuborders(null);
				} else {
					List<Order> properSort = new ArrayList<>(proper);
					entity.setSuborders(properSort);
				}
			}
			if (entity.getCompany() != null) {
				entity.setCompany(companyFilter.filterEntities(entity.getCompany(), companyId, storageRights));
			}
			
			if (entity.getOrderTypes() != null && !entity.getOrderTypes().isEmpty()) {
				List<UUID> ids = new ArrayList<>();
				List<UUID> removeIds = new ArrayList<>();
				entity.getOrderTypes().forEach(prop -> ids.add(prop.getId()));
				getUUIDsToDelete(ids, removeIds, storageRights);
				var proper = entity
						.getOrderTypes()
						.stream()
						.filter(prop -> !removeIds.contains(prop.getId()))
						.map(prop -> prop)//Bisher kein weiteres Filtern nötig, da nur die Id der jeweiligen SubOrder mitgegeben wird
						.collect(Collectors.toSet());
				if (proper.isEmpty()) {
					entity.setOrderTypes(null);
				} else {
					SortedSet<OrderType> properSort = new TreeSet<>(proper);
					entity.setOrderTypes(properSort);
				}
			}
			if (entity.getOrderRoutes() != null && !entity.getOrderRoutes().isEmpty()) {
				List<UUID> ids = new ArrayList<>();
				List<UUID> removeIds = new ArrayList<>();
				entity.getOrderRoutes().forEach(prop -> ids.add(prop.getId()));
				getUUIDsToDelete(ids, removeIds, storageRights);
				var proper = entity
						.getOrderRoutes()
						.stream()
						.filter(prop -> !removeIds.contains(prop.getId()))
						.map(prop -> prop)//Bisher kein weiteres Filtern nötig, da nur die Id der jeweiligen SubOrder mitgegeben wird
						.toList();
				if (proper.isEmpty()) {
					entity.setOrderRoutes(null);
				} else {
					List<OrderRoute> properSort = new ArrayList<>(proper);
					entity.setOrderRoutes(properSort);
				}
			}
			if (entity.getPayment() != null) {
				entity.setPayment(paymentFilter.filterEntities(entity.getPayment(), companyId, storageRights));
			}
			if (entity.getCost() != null) {
				entity.setCost(costFilter.filterEntities(entity.getCost(), companyId, storageRights));
			}
			
			if (entity.getPackageItems() != null && !entity.getPackageItems().isEmpty()) {
				List<UUID> ids = new ArrayList<>();
				List<UUID> removeIds = new ArrayList<>();
				entity.getPackageItems().forEach(prop -> ids.add(prop.getId()));
				getUUIDsToDelete(ids, removeIds, storageRights);
				var proper = entity
						.getPackageItems()
						.stream()
						.filter(prop -> !removeIds.contains(prop.getId()))
						.map(prop -> prop)//Bisher kein weiteres Filtern nötig, da nur die Id der jeweiligen SubOrder mitgegeben wird
						.collect(Collectors.toSet());
				if (proper.isEmpty()) {
					entity.setPackageItems(null);
				} else {
					SortedSet<PackageItem> properSort = new TreeSet<>(proper);
					entity.setPackageItems(properSort);
				}
			}
			if (entity.getDeliveryMethod() != null) {
				entity.setDeliveryMethod(deliveryMethodFilter.filterEntities(entity.getDeliveryMethod(), companyId, storageRights));
			}
			if (entity.getContactPerson() != null) {
				entity.setContactPerson(contactPersonFilter.filterEntities(entity.getContactPerson(), companyId, storageRights));
			}
			
		}
		
		
		return entity;
	}
	
	@Override
	public AbstractPropertyEntityFilter<OrderProperty, OrderProperty, Order, Order> getPropertyFilter() {
		return this.orderPropertyFilter;
	}
	
	public Set<UUID> collectIDs(Order entityTransfer) {
		var entity = this.transformToEntity(entityTransfer);
		var uuids = super.collectIDs(entity);
		if (entity.getParentOrder() != null) {
			uuids.add(entity.getParentOrder().getId());
		}
		if (entity.getAddressFrom() != null) {
			uuids.add(entity.getAddressFrom().getId());
		}
		if (entity.getAddressTo() != null) {
			uuids.add(entity.getAddressTo().getId());
		}
		if (entity.getAddressBilling() != null) {
			uuids.add(entity.getAddressBilling().getId());
		}
		if (entity.getSuborders() != null && !entity.getSuborders().isEmpty()) {
			entity.getSuborders().forEach(prop -> uuids.add(prop.getId()));
		}
		if (entity.getCompany() != null) {
			var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById(entity.getCompany().getId()), QCompanyIDToCompanyOID.companyIDToCompanyOID);
			var comp = companyIDToCompanyOIDRepository.findOne(spec);
			if (comp.isPresent()) {
				uuids.add(comp.get().getCompanyOID());
			}
		}
		if (entity.getOrderTypes() != null && !entity.getOrderTypes().isEmpty()) {
			entity.getOrderTypes().forEach(prop -> uuids.add(prop.getId()));
		}
		if (entity.getOrderRoutes() != null && !entity.getOrderRoutes().isEmpty()) {
			entity.getOrderRoutes().forEach(prop -> uuids.add(prop.getId()));
		}
		if (entity.getPayment() != null) {
			uuids.add(entity.getPayment().getId());
		}
		if (entity.getCost() != null) {
			uuids.add(entity.getCost().getId());
		}
		if (entity.getPackageItems() != null && !entity.getPackageItems().isEmpty()) {
			entity.getPackageItems().forEach(prop -> uuids.add(prop.getId()));
		}
		if (entity.getDeliveryMethod() != null) {
			uuids.add(entity.getDeliveryMethod().getId());
		}
		if (entity.getContactPerson() != null) {
			uuids.add(entity.getContactPerson().getId());
		}
		return uuids;
	}
	
	
}
