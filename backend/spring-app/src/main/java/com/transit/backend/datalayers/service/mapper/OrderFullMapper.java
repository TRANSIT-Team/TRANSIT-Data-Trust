package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.IdentifierDTO;
import com.transit.backend.datalayers.controller.dto.OrderFullDTO;
import com.transit.backend.datalayers.controller.dto.OrderPartDTO;
import com.transit.backend.datalayers.controller.dto.PackageItemDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.OrderRoute;
import com.transit.backend.datalayers.domain.OrderType;
import com.transit.backend.datalayers.domain.PackageItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = {PackageItemMapper.class, AddressMapper.class, OrderPartMapper.class})
public interface OrderFullMapper extends AbstractMapper<Order, OrderFullDTO> {
	
	
	@Named("dtoToOrder")
	public static Order dtoToSuborder(IdentifierDTO dto) {
		if (dto == null) {
			return null;
		}
		var entity = new Order();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("dtoToSubOrder")
	public static Order dtoToSuborder(OrderPartDTO dto) {
		if (dto == null) {
			return null;
		}
		var entity = new Order();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("dtoToOrderType")
	public static OrderType dtoToOrderType(IdentifierDTO dto) {
		if (dto == null) {
			return null;
		}
		var suborder = new OrderType();
		suborder.setId(dto.getId());
		return suborder;
	}
	
	@Named("dtoToOrderRoute")
	public static OrderRoute dtoToOrderRoute(IdentifierDTO dto) {
		if (dto == null) {
			return null;
		}
		var entity = new OrderRoute();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("dtoToPackageItem")
	public static PackageItem dtoToPackageItem(PackageItemDTO dto) {
		var entity = new PackageItem();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("orderToDTO")
	public static IdentifierDTO orderToDTO(Order dto) {
		if (dto == null) {
			return null;
		}
		var entity = new IdentifierDTO();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("orderTypeToDTO")
	public static IdentifierDTO orderTypeToDTO(OrderType dto) {
		if (dto == null) {
			return null;
		}
		var entity = new IdentifierDTO();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("orderRouteToDTO")
	public static IdentifierDTO orderRouteToDTO(OrderRoute dto) {
		if (dto == null) {
			return null;
		}
		var entity = new IdentifierDTO();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Mapping(source = "addressFrom", target = "addressFrom")
	@Mapping(source = "addressTo", target = "addressTo")
	@Mapping(source = "addressBilling", target = "addressBilling")
	@Mapping(source = "suborders", target = "suborders")
	@Mapping(source = "companyId.id", target = "company.id")
	@Mapping(source = "orderTypeIds", target = "orderTypes", qualifiedByName = "dtoToOrderType")
	@Mapping(source = "orderRouteIds", target = "orderRoutes", qualifiedByName = "dtoToOrderRoute")
	@Mapping(source = "orderStatus", target = "orderStatus")
	@Mapping(source = "paymentId.id", target = "payment.id")
	@Mapping(source = "costId.id", target = "cost.id")
	@Mapping(source = "packageItems", target = "packageItems")
	@Mapping(source = "orderProperties", target = "orderProperties")
	@Mapping(source = "deliveryMethodId.id", target = "deliveryMethod.id")
	@Mapping(source = "parentOrderId", target = "parentOrder", qualifiedByName = "dtoToOrder")
	@Mapping(source = "internalComment", target = "internalComment")
	@Mapping(source = "price", target = "price")
	@Mapping(source = "contactPerson", target = "contactPerson")
	@Mapping(source = "packagesPrice", target = "packagesPrice")
	
	@Mapping(source = "deliveryPerson", target = "deliveryPerson")
	@Mapping(source = "outsourceCost", target = "outsourceCost")
	@Mapping(source = "customerId.id", target = "customerId")
	@Mapping(source = "id", target = "id")
	Order toEntity(OrderFullDTO dto);
	
	@Mapping(target = "orderProperties.order", ignore = true)
	@Mapping(target = "addressFrom", source = "addressFrom")
	@Mapping(target = "addressTo", source = "addressTo")
	@Mapping(target = "addressBilling", source = "addressBilling")
	@Mapping(target = "suborders", source = "suborders")
	@Mapping(target = "companyId.id", source = "company.id")
	@Mapping(target = "orderTypeIds", source = "orderTypes", qualifiedByName = "orderTypeToDTO")
	@Mapping(target = "orderRouteIds", source = "orderRoutes", qualifiedByName = "orderRouteToDTO")
	@Mapping(target = "orderStatus", source = "orderStatus")
	@Mapping(target = "paymentId.id", source = "payment.id")
	@Mapping(target = "costId.id", source = "cost.id")
	@Mapping(target = "packageItems", source = "packageItems")
	@Mapping(target = "orderProperties", source = "orderProperties")
	@Mapping(target = "deliveryMethodId.id", source = "deliveryMethod.id")
	@Mapping(target = "parentOrderId", source = "parentOrder", qualifiedByName = "orderToDTO")
	@Mapping(source = "internalComment", target = "internalComment")
	@Mapping(source = "price", target = "price")
	@Mapping(target = "contactPerson", source = "contactPerson")
	@Mapping(source = "packagesPrice", target = "packagesPrice")
	@Mapping(source = "deliveryPerson", target = "deliveryPerson")
	@Mapping(source = "outsourceCost", target = "outsourceCost")
	@Mapping(source = "customerId", target = "customerId.id")
	@Mapping(source = "id", target = "shortId")
	@Mapping(source = "id", target = "id")
	OrderFullDTO toDto(Order entity);
	
	
}