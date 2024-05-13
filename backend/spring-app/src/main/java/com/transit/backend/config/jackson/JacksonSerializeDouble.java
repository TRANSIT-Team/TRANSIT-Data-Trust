package com.transit.backend.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class JacksonSerializeDouble extends JsonSerializer<Double> {
	@Override
	public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value == null) {
			gen.writeNull();
		} else if (Double.isNaN(value)) {
			gen.writeNull();
		} else if (value == 0.0) {
			gen.writeNull();
		} else {
			gen.writeString(String.valueOf(value));
		}
		
		
	}
}
