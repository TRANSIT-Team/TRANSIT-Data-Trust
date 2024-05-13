package com.transit.backend.config.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transit.backend.datalayers.domain.*;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ChildAsIdOnlySerializer extends JsonDeserializer<AbstractEntity> {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public AbstractEntity deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		if (p.getText() == null) {
			return null;
		}
		
		
		ObjectCodec oc = p.getCodec();
		JsonNode node = oc.readTree(p);
		
		AbstractEntity value = objectMapper.readValue(p, AbstractEntity.class);
		var id = value.getId();
		if (value instanceof Car) {
			value = new Car();
			value.setId(id);
		}
		if (value instanceof User) {
			value = new User();
			value.setId(id);
		}
		if (value instanceof Company) {
			value = new Company();
			value.setId(id);
		}
		if (value instanceof Order) {
			value = new Order();
			value.setId(id);
		}
		if (value instanceof OrderType) {
			value = new OrderType();
			value.setId(id);
		}
		if (value instanceof PackageItem) {
			value = new PackageItem();
			value.setId(id);
		}
		if (value instanceof Payment) {
			value = new Payment();
			value.setId(id);
		}
		if (value instanceof Warehouse) {
			value = new Warehouse();
			value.setId(id);
		}
		return value;
	}
	
	
}