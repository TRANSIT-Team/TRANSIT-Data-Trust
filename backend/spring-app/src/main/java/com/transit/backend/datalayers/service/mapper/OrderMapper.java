package com.transit.backend.datalayers.service.mapper;

import com.transit.backend.datalayers.controller.dto.IdentifierDTO;
import com.transit.backend.datalayers.controller.dto.OrderDTO;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.OrderRoute;
import com.transit.backend.datalayers.domain.OrderType;
import com.transit.backend.datalayers.domain.PackageItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OrderMapper extends AbstractMapper<Order, OrderDTO> {
	
	@Named("dtoToOrder")
	public static Order dtoToSuborder(IdentifierDTO dto) {
		if (dto == null) {
			return null;
		}
		var entity = new Order();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("dtoToOrderType")
	public static OrderType dtoToOrderType(IdentifierDTO dto) {
		var suborder = new OrderType();
		suborder.setId(dto.getId());
		return suborder;
	}
	
	@Named("dtoToOrderRoute")
	public static OrderRoute dtoToOrderRoute(IdentifierDTO dto) {
		var entity = new OrderRoute();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("dtoToPackageItem")
	public static PackageItem dtoToPackageItem(IdentifierDTO dto) {
		var entity = new PackageItem();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("dtoToDouble")
	public static double dtoToDouble(Double d) {
		if (d == null || Double.isNaN(d) || d == 0.0) {
			return Double.NaN;
		} else {
			return d;
		}
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
		var entity = new IdentifierDTO();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("orderRouteToDTO")
	public static IdentifierDTO orderRouteToDTO(OrderRoute dto) {
		var entity = new IdentifierDTO();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("packageItemToDTO")
	public static IdentifierDTO packageItemToDTO(PackageItem dto) {
		var entity = new IdentifierDTO();
		entity.setId(dto.getId());
		return entity;
	}
	
	@Named("doubleToDTO")
	public static Double getComplexDouble(double d) {
		if (Double.isNaN(d) || d == 0.0) {
			return Double.NaN;
		}
		return d;
	}
	
	@Mapping(source = "addressIdFrom.id", target = "addressFrom.id")
	@Mapping(source = "addressIdTo.id", target = "addressTo.id")
	@Mapping(source = "addressIdBilling.id", target = "addressBilling.id")
	@Mapping(source = "suborderIds", target = "suborders", qualifiedByName = "dtoToOrder")
	@Mapping(source = "companyId.id", target = "company.id")
	@Mapping(source = "orderTypeIds", target = "orderTypes", qualifiedByName = "dtoToOrderType")
	@Mapping(source = "orderRouteIds", target = "orderRoutes", qualifiedByName = "dtoToOrderRoute")
	@Mapping(source = "orderStatus", target = "orderStatus")
	@Mapping(source = "paymentId.id", target = "payment.id")
	@Mapping(source = "costId.id", target = "cost.id")
	@Mapping(source = "packageItemIds", target = "packageItems", qualifiedByName = "dtoToPackageItem")
	@Mapping(source = "orderProperties", target = "orderProperties")
	@Mapping(source = "deliveryMethodId.id", target = "deliveryMethod.id")
	@Mapping(source = "parentOrderId", target = "parentOrder", qualifiedByName = "dtoToOrder")
	@Mapping(source = "internalComment", target = "internalComment")
	@Mapping(source = "contactPersonId.id", target = "contactPerson.id")
	
	@Mapping(source = "deliveryPerson", target = "deliveryPerson")
	@Mapping(source = "packagesPrice", target = "packagesPrice", qualifiedByName = "dtoToDouble")
	@Mapping(source = "outsourceCost", target = "outsourceCost", qualifiedByName = "dtoToDouble")
	@Mapping(source = "orderAltPrice", target = "orderAltPrice", qualifiedByName = "dtoToDouble")
	@Mapping(source = "price", target = "price", qualifiedByName = "dtoToDouble")
	@Mapping(source = "reasonForCancel", target = "reasonForCancel")
	@Mapping(source = "customerId.id", target = "customerId")
	@Mapping(source = "id", target = "id")
	Order toEntity(OrderDTO dto);
	
	@Mapping(target = "orderProperties.order", ignore = true)
	@Mapping(target = "addressIdFrom.id", source = "addressFrom.id")
	@Mapping(target = "addressIdTo.id", source = "addressTo.id")
	@Mapping(target = "addressIdBilling.id", source = "addressBilling.id")
	@Mapping(target = "suborderIds", source = "suborders", qualifiedByName = "orderToDTO")
	@Mapping(target = "companyId.id", source = "company.id")
	@Mapping(target = "orderTypeIds", source = "orderTypes", qualifiedByName = "orderTypeToDTO")
	@Mapping(target = "orderRouteIds", source = "orderRoutes", qualifiedByName = "orderRouteToDTO")
	@Mapping(target = "orderStatus", source = "orderStatus")
	@Mapping(target = "paymentId.id", source = "payment.id")
	@Mapping(target = "costId.id", source = "cost.id")
	@Mapping(target = "packageItemIds", source = "packageItems", qualifiedByName = "packageItemToDTO")
	@Mapping(target = "orderProperties", source = "orderProperties")
	@Mapping(target = "deliveryMethodId.id", source = "deliveryMethod.id")
	@Mapping(target = "parentOrderId", source = "parentOrder", qualifiedByName = "orderToDTO")
	@Mapping(source = "internalComment", target = "internalComment")
	@Mapping(target = "contactPersonId.id", source = "contactPerson.id")
	@Mapping(source = "deliveryPerson", target = "deliveryPerson")
	@Mapping(source = "reasonForCancel", target = "reasonForCancel")
	@Mapping(source = "packagesPrice", target = "packagesPrice", qualifiedByName = "doubleToDTO")
	@Mapping(source = "outsourceCost", target = "outsourceCost", qualifiedByName = "doubleToDTO")
	@Mapping(source = "orderAltPrice", target = "orderAltPrice", qualifiedByName = "doubleToDTO")
	@Mapping(source = "price", target = "price", qualifiedByName = "doubleToDTO")
	@Mapping(source = "customerId", target = "customerId.id")
	@Mapping(source = "id", target = "shortId")
	@Mapping(source = "id", target = "id")
	OrderDTO toDto(Order entity);
	
	
}