package com.transit.backend.config.jackson;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;

public class OwnCollectionSerializer extends CollectionSerializer {
	
	
	public OwnCollectionSerializer(CollectionSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSerializer, Boolean unwrapSingle) {
		super(src, property, vts, valueSerializer, unwrapSingle);
	}
}
