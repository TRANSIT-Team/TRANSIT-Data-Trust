package com.transit.backend.config;

import com.transit.backend.datalayers.domain.*;
import lombok.Getter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Getter
public class EndpointsByPath {
	HashMap<Pair<String, String>, Pair<String, Boolean>> endpointPathes;
	HashMap<Pair<String, String>, Pair<String, Boolean>> endpointPathesExtra;
	HashMap<String, Class<?>> classes;
	
	public EndpointsByPath() {
		this.endpointPathes = new HashMap<>();
		this.endpointPathesExtra = new HashMap<>();
		this.classes = new HashMap<>();
		endpointPathes.put(Pair.of("Address", ""), Pair.of("/addresses", false));
		classes.put(Address.class.getSimpleName(), Address.class);
		endpointPathes.put(Pair.of("Car", ""), Pair.of("/cars", false));
		classes.put(Car.class.getSimpleName(), Car.class);
		endpointPathes.put(Pair.of("Car", "Location"), Pair.of("/cars/{id}/carlocations", true));
		classes.put(Location.class.getSimpleName(), Location.class);
		endpointPathes.put(Pair.of("Car", "CarProperty"), Pair.of("/cars/{id}/carproperties", true));
		classes.put(CarProperty.class.getSimpleName(), CarProperty.class);
		endpointPathes.put(Pair.of("Company", ""), Pair.of("/companies", false));
		classes.put(Company.class.getSimpleName(), Company.class);
		endpointPathes.put(Pair.of("Company", "CompanyProperty"), Pair.of("/companies/{id}/companyproperties", true));
		classes.put(CompanyProperty.class.getSimpleName(), CompanyProperty.class);
		endpointPathes.put(Pair.of("Company", "CompanyAddress"), Pair.of("/companies/{id}/companyaddresses", true));
		classes.put(CompanyAddress.class.getSimpleName(), CompanyAddress.class);
		endpointPathes.put(Pair.of("Company", "CompanyDeliveryArea"), Pair.of("/companies/{id}/deliveryarea", true));
		classes.put(CompanyDeliveryArea.class.getSimpleName(), CompanyDeliveryArea.class);
		endpointPathes.put(Pair.of("Location", ""), Pair.of("/locations", false));
		endpointPathes.put(Pair.of("Order", ""), Pair.of("/orders", false));
		classes.put(Order.class.getSimpleName(), Order.class);
		endpointPathes.put(Pair.of("Order", "OrderProperty"), Pair.of("/orders/{id}/orderproperties", true));
		classes.put(OrderProperty.class.getSimpleName(), OrderProperty.class);
		endpointPathes.put(Pair.of("Order", "Order"), Pair.of("/orders/{id}/suborders", true));
		endpointPathes.put(Pair.of("PackageClass", ""), Pair.of("/packageclasses", false));
		classes.put(PackageClass.class.getSimpleName(), PackageClass.class);
		endpointPathes.put(Pair.of("PackageItem", ""), Pair.of("/packageitems", false));
		classes.put(PackageItem.class.getSimpleName(), PackageItem.class);
		endpointPathes.put(Pair.of("PackageItem", "PackageClass"), Pair.of("/packageitems/{id}/packageclasses", true));
		endpointPathes.put(Pair.of("PackageItem", "PackagePackageProperty"), Pair.of("/packageitems/{id}/packagepackageproperties", true));
		classes.put(PackagePackageProperty.class.getSimpleName(), PackagePackageProperty.class);
		endpointPathes.put(Pair.of("PackageProperty", ""), Pair.of("/packageproperties", false));
		classes.put(PackageProperty.class.getSimpleName(), PackageProperty.class);
		endpointPathes.put(Pair.of("User", ""), Pair.of("/users", false));
		classes.put(PackageProperty.class.getSimpleName(), PackageProperty.class);
		endpointPathes.put(Pair.of("User", "UserProperty"), Pair.of("/users/{id}/userproperties", true));
		classes.put(User.class.getSimpleName(), User.class);
		endpointPathes.put(Pair.of("Warehouse", ""), Pair.of("/warehouses", false));
		classes.put(Warehouse.class.getSimpleName(), Warehouse.class);
		endpointPathes.put(Pair.of("Warehouse", "Address"), Pair.of("/warehouses/{id}/warehouseaddresses", true));
		classes.put(WarehouseProperty.class.getSimpleName(), WarehouseProperty.class);
		endpointPathes.put(Pair.of("Warehouse", "WarehouseProperty"), Pair.of("/warehouses/{id}/warehouseproperties", true));
		
		endpointPathes.put(Pair.of("Payment", ""), Pair.of("/payments", false));
		classes.put(Payment.class.getSimpleName(), Payment.class);
		
		endpointPathes.put(Pair.of("Payment", "PaymentProperty"), Pair.of("/payments/{id}/paymentproperties", true));
		classes.put(PaymentProperty.class.getSimpleName(), PaymentProperty.class);
		
		endpointPathes.put(Pair.of("PaymentDefaultProperty", ""), Pair.of("/paymentdefaultproperties", false));
		classes.put(PaymentDefaultProperty.class.getSimpleName(), PaymentDefaultProperty.class);
		
		endpointPathes.put(Pair.of("Cost", ""), Pair.of("/costs", false));
		classes.put(Cost.class.getSimpleName(), Cost.class);
		
		endpointPathes.put(Pair.of("Cost", "CostProperty"), Pair.of("/costs/{id}/costproperties", true));
		classes.put(CostProperty.class.getSimpleName(), CostProperty.class);
		
		endpointPathes.put(Pair.of("CostDefaultProperty", ""), Pair.of("/costdefaultproperties", false));
		classes.put(CostDefaultProperty.class.getSimpleName(), CostDefaultProperty.class);
		
		endpointPathes.put(Pair.of("ContactPerson", ""), Pair.of("/contactpersons", false));
		classes.put(ContactPerson.class.getSimpleName(), ContactPerson.class);
		
		endpointPathes.put(Pair.of("ChatEntry", ""), Pair.of("/orders/chat", false));
		classes.put(ChatEntry.class.getSimpleName(), ChatEntry.class);
		
		endpointPathes.put(Pair.of("ChatEntry", ""), Pair.of("/orders/{id}/chat", false));
		classes.put(ChatEntry.class.getSimpleName(), ChatEntry.class);
		
		endpointPathes.put(Pair.of("Company", "Customer"), Pair.of("/companies/{id}/customers", true));
		classes.put(Customer.class.getSimpleName(), Customer.class);
		
		endpointPathesExtra.put(Pair.of("Company", "CompanyAddress"), Pair.of("/companies/{id}/companyaddresses", true));
		classes.put(CompanyAddress.class.getSimpleName(), CompanyAddress.class);
		
		
		endpointPathes.put(Pair.of("Company", ""), Pair.of("/companies/overview", false));
		classes.put(Company.class.getSimpleName(), Company.class);
		
		endpointPathes.put(Pair.of("Order", "OrderCommentChat"), Pair.of("/orders/{id}/comment", true));
		classes.put(OrderCommentChat.class.getSimpleName(), OrderCommentChat.class);
		
		endpointPathes.put(Pair.of("Company", "CompanyFavorite"), Pair.of("/companies/{id}/favorites", true));
		classes.put(CompanyFavorite.class.getSimpleName(), CompanyFavorite.class);
		
		
	}
	
	public String readOne(String pathending) {
		for (var entry : this.endpointPathes.entrySet()) {
			if (entry.getValue().getFirst().endsWith(pathending)) {
				if (entry.getKey().getSecond().isBlank()) {
					return entry.getKey().getFirst();
				} else {
					return entry.getKey().getSecond();
				}
			}
		}
		
		return null;
	}
}