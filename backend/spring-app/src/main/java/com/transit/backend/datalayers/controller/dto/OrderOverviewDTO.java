package com.transit.backend.datalayers.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.transit.backend.config.jackson.JacksonOffSerDateTimeMapperSerialize;
import com.transit.backend.config.jackson.JacksonOffsetDateTimeMapper;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.hateoas.server.core.Relation;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Relation(collectionRelation = "orders", itemRelation = "order")
public class OrderOverviewDTO extends AbstractDTO<OrderOverviewDTO> {
	
	
	private UUID shortId;
	
	private IdentifierDTO parentOrderId;
	
	@JsonDeserialize(using = JacksonOffsetDateTimeMapper.class)
	@JsonSerialize(using = JacksonOffSerDateTimeMapperSerialize.class)
	private OffsetDateTime destinationDate;
	
	
	@JsonDeserialize(using = JacksonOffsetDateTimeMapper.class)
	@JsonSerialize(using = JacksonOffSerDateTimeMapperSerialize.class)
	private OffsetDateTime pickUpDate;
	
	
	private AddressDTO addressFrom;
	
	private AddressDTO addressTo;
	
	private AddressDTO addressBilling;
	
	
	private IdentifierDTO companyId;
	
	
	private String orderStatus;
	
	
	private Set<PackageItemDTO> packageItems;
	
	
	private boolean attentionFlag;
	
	private boolean suborderType;
	
	
	private double price;
	
	
	private double packagesPrice;
	
	
	private String deliveryPerson;
	
	private double outsourceCost;
	
	private double orderAltPrice;
	
	private UUID oldOrderId;
	
	private UUID newOrderId;
	
	private IdentifierDTO customerId;
	
	
	private OffsetDateTime pickUpDateTo;
	
	
	private OffsetDateTime destinationDateTo;
	
	private String pickUpPerson;
	
	
	private OffsetDateTime pickUpTimestampLeave;
	
	private OffsetDateTime pickUpTimestampArrive;
	
	private OffsetDateTime deliveryTimestampLeave;
	
	private OffsetDateTime deliveryTimestampArrive;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OrderOverviewDTO that)) return false;
		if (!super.equals(o)) return false;
		return attentionFlag == that.attentionFlag && suborderType == that.suborderType && Double.compare(price, that.price) == 0 && Double.compare(packagesPrice, that.packagesPrice) == 0 && Double.compare(outsourceCost, that.outsourceCost) == 0 && Double.compare(orderAltPrice, that.orderAltPrice) == 0 && Objects.equals(parentOrderId, that.parentOrderId) && Objects.equals(destinationDate, that.destinationDate) && Objects.equals(pickUpDate, that.pickUpDate) && Objects.equals(addressFrom, that.addressFrom) && Objects.equals(addressTo, that.addressTo) && Objects.equals(addressBilling, that.addressBilling) && Objects.equals(companyId, that.companyId) && Objects.equals(orderStatus, that.orderStatus) && Objects.equals(packageItems, that.packageItems) && Objects.equals(deliveryPerson, that.deliveryPerson);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parentOrderId, destinationDate, pickUpDate, addressFrom, addressTo, addressBilling, companyId, orderStatus, packageItems, attentionFlag, suborderType, price, packagesPrice, deliveryPerson, outsourceCost, orderAltPrice);
	}
}
